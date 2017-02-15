package com.esferalia.aon.ingenet.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

import com.esferalia.aon.ingenet.api.albaranes.ALBARANES;
import com.esferalia.aon.ingenet.api.albaranes.ALBARANTYPE;
import com.esferalia.aon.ingenet.api.albaranes.DATOSAGENCIATRANSPORTETYPE;
import com.esferalia.aon.ingenet.api.albaranes.DATOSCLIENTETYPE;
import com.esferalia.aon.ingenet.api.albaranes.DATOSDIRECCIONTYPE;
import com.esferalia.aon.ingenet.api.albaranes.DATOSLINEAALBARANTYPE;
import com.esferalia.aon.ingenet.api.albaranes.ELABORACIONORIGENTYPE;
import com.esferalia.aon.ingenet.api.albaranes.PRODUCTOTYPE;
import com.esferalia.aon.ingenet.api.util.IngenetXmlValidator;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.ElaborationDetail;
import com.esferalia.aon.occam.api.model.ElaborationDetailComposition;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.DeliveryStatus;
import com.esferalia.aon.occam.api.model.type.ElaborationStatus;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingStatus;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingType;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.occam.impl.jooq.dao.ElaborationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WarehouseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WorkplaceDAO;
import com.esferalia.aon.watson.error.AonCoreException;

public class IngenetDeliveryServlet extends AbstractIngenetServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<String> errorList;
	
		
	protected void processRequest(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws ServletException, IOException {
		
		errorList = new LinkedList<>();
		
		String _xml = httpRequest.getParameter(PARAM_VALUE);
		if(_xml!=null){
			ALBARANES deliveryList = null;
			try {
				super.validateAlbaranesXmlPattern(new ByteArrayInputStream(_xml.getBytes()));
				deliveryList = (ALBARANES) IngenetXmlValidator.extractValue(_xml, ALBARANES.class);
			} catch (SAXException e) {
				errorList.add("El fichero no ha pasado el proceso de validacion");
				errorList.add(e.getMessage());
			} catch (Exception e) {
				errorList.add(e.getMessage());
			}
			if(deliveryList!=null && deliveryList.getDATOSALBARANES()!=null 
					&& deliveryList.getDATOSALBARANES().size()>0){
				// TODO uncomment line below before commit
				processData(deliveryList.getDATOSALBARANES());
				httpResponse.setStatus(HttpServletResponse.SC_OK);
			} else {
				errorList.add("No se han encontrado datos de albaranes");
				httpResponse.sendError(HttpServletResponse.SC_NO_CONTENT);
			}
		} else {
			errorList.add("Es necesario el parametro 'value'");
			httpResponse.sendError(HttpServletResponse.SC_NO_CONTENT);
		}
		
		if (errorList != null && errorList.size() > 0) {
			flushErrors(httpResponse, errorList);
			errorList.forEach(System.out::println);
		}
		
	}
	
	// TODO flushErrors
	private void flushErrors(HttpServletResponse httpResponse,
			List<String> errorList) throws IOException {
//		RESPUESTAELABORACIONES respuesta = new RESPUESTAELABORACIONES();
//		respuesta.setERRORES(new ERRORESTYPE());
//		errorList.forEach(error -> {
//			respuesta.getERRORES().getERRORES().add(error);
//		});
//		String xml = convertToXml(respuesta, RESPUESTAELABORACIONES.class);
//		httpResponse.setContentType("application/xml");
//		httpResponse.setContentLength(xml.length());
//		
//		PrintWriter out = httpResponse.getWriter();
//		out.print(xml);
//		out.flush();
	}
	
	
	private void processData(List<ALBARANTYPE> list){
		AONContext ctx = AONContext.getAONContext(getDomain(), getDomainId(), getUser());
		ctx.getDslContext().transaction(configuration -> {
			list.forEach(albaran -> {
				createDelivery(ctx, albaran);
				createCarrierPacking(ctx, albaran);
			});
		});
	}

	private Delivery createDelivery(AONContext ctx, ALBARANTYPE albaran) {
		Delivery delivery = new Delivery(); 
		fillDelivery(ctx, albaran, delivery);		
		int id = WarehouseDAO.insertDelivery(ctx, delivery);
		delivery.setId(id);
		List<DeliveryDetail> detailList = new LinkedList<DeliveryDetail>();
		fillDeliveryDetailList(ctx, albaran.getLINEASALBARAN().getDATOSLINEAALBARAN(), delivery, detailList);
		WarehouseDAO.insertDeliveryDetails(ctx, detailList);
		return delivery;
	}

	private Delivery fillDelivery(AONContext ctx, ALBARANTYPE albaran, Delivery delivery) {
//		Integer[] scopes = SecurityDAO.getUserScopes(ctx, ctx.getUser());
		LinkedList<Scope> scopes = SecurityDAO.getDomainScopes(ctx);
		
		String series = "IGN"+new SimpleDateFormat("yy").format(new Date());
		
		int number = WarehouseDAO
				.getDeliveryList(
						ctx,
						f -> f.getDomainProperty().eq(ctx.getDomainId())
								.and(f.getSeriesProperty().eq(series)))
				.stream()
				.sorted((d1, d2) -> Integer.compare(d2.getNumber(),
						d1.getNumber())).findFirst().orElse(new Delivery())
				.getNumber();
		Workplace wp = WorkplaceDAO.getWorkplace(ctx, f -> f
				.getDomainProperty().eq(ctx.getDomainId())
				.and(f.getActiveProperty().eq((byte)1))); 
//		Workplace wp = wpList==null || wpList.isEmpty() ? null:wpList.get(0);
		
		delivery.setDomain(ctx.getDomainId());
		delivery.setProject(new Project());
		delivery.setSeries(series);
//		delivery.setNumber(Integer.valueOf(albaran.getNUMERO()));
		delivery.setNumber(++number);
		Customer customer = obtainCustomer(ctx, albaran.getDATOSCLIENTE());
		delivery.setCustomer(customer.getId());
		delivery.setAddress(obtainAddress(ctx, customer, albaran.getDATOSDIRECCIONENTREGA()).getId());
		// if address == null, fill shippingAlternative
		
		try {
			delivery.setIssueTime(getDateFormatter().parse(albaran.getFECHAEMISION()));
		} catch (ParseException e) {
			System.err.println("Cannot parse date value. Reason: "+ e.getMessage());
			delivery.setIssueTime(new Date());
		}
		delivery.setSecurityLevel((byte) 0);
		delivery.setStatus(DeliveryStatus.PENDING);
		delivery.setComments(albaran.getCOMENTARIOS());
		delivery.setRemarks("Creado por '"+ctx.getUser()+"' el "
				+ new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
		delivery.setWorkplace(wp.getId());
		delivery.setScope(scopes.get(0).getId());
		delivery.setPayMethod(null);
		delivery.setNumberOfPymnts((short) 0);
		delivery.setDaysToFirstPymnt((short) 0);
		delivery.setDaysBetweenPymnt((short) 0);
		delivery.setPymntDays("");
		delivery.setBankAccount("");
		delivery.setBankAlias("");
		delivery.setBic("");
		return delivery;
	}


	private List<DeliveryDetail> fillDeliveryDetailList(AONContext ctx,
			List<DATOSLINEAALBARANTYPE> list, Delivery delivery,
			List<DeliveryDetail> detailList) {
		if (list != null && list.size() > 0) {
			Warehouse warehouse = WarehouseDAO.getWarehouse(
					ctx,
					f -> f.getDomainProperty()
							.eq(ctx.getDomainId())
							.and(f.getActiveProperty().eq((byte) 1))
							.and(f.getWorkplaceProperty().eq(
									delivery.getWorkplace())));
			
			list.stream()
					.filter(linea -> !isPackageItem(linea))
					.forEach(
							linea -> {
								Item item = obtainItem(ctx,
										linea.getPRODUCTO());
								Elaboration elaboration = obtainElaboration(
										ctx, linea.getDATOSELABORACIONORIGEN());
								DeliveryDetail detail = new DeliveryDetail();
								detail.setDomain(ctx.getDomainId());
								detail.setDelivery(delivery);
								detail.setLine(Short.valueOf(linea.getLINEA()));
								detail.setItem(item);
								detail.setDescription(linea.getDESCRIPCION());
								detail.setWarehouse(warehouse.getId());
								detail.setQuantity(Double.valueOf(linea
										.getCANTIDAD()));
								detail.setPrice(item.getPrice());
								detail.setDiscountExpression("0");
//								detail.setSalesDetail(elaboration.getSourceId());
								detailList.add(detail);
								manageElaboration(ctx, linea);
							});
			list.stream()
					.filter(linea -> isPackageItem(linea))
					.sorted((linea1, linea2) -> linea1.getDESCRIPCION()
							.compareTo(linea2.getDESCRIPCION()))
					.forEach(
							linea -> {
								Item item = obtainItem(ctx,
										linea.getPRODUCTO());
								DeliveryDetail detail = new DeliveryDetail();
								detail.setDomain(ctx.getDomainId());
								detail.setDelivery(delivery);
								detail.setLine(Short.valueOf(linea.getLINEA()));
								detail.setItem(item);
								detail.setDescription(linea.getDESCRIPCION());
								detail.setWarehouse(warehouse.getId());
								detail.setDiscountExpression("0");
								detail.setQuantity(Double.valueOf(linea
										.getCANTIDAD()));
								detailList.add(detail);
							});
		}
		return detailList;
	}

	
	private void manageElaboration(AONContext ctx,
			DATOSLINEAALBARANTYPE lineaAlbaran) {

		Elaboration elaboration = obtainElaboration(ctx,
				lineaAlbaran.getDATOSELABORACIONORIGEN());
		if(elaboration!=null && elaboration.getId()!=null){
			elaboration.setStatus(ElaborationStatus.CLOSED.value());
			ElaborationDAO.updateElaboration(ctx, elaboration);
			
			ElaborationDetail elaborationDetail = new ElaborationDetail();
			elaborationDetail.setDomain(ctx.getDomainId());
			elaborationDetail.setElaboration(elaboration);
			elaborationDetail.setDate(new Date());
			elaborationDetail.setItem(obtainItem(ctx,
					lineaAlbaran.getPRODUCTO()));
			elaborationDetail
			.setQuantity(Double.valueOf(lineaAlbaran.getCANTIDAD()));
			elaborationDetail.setWarehouse(null);
			elaborationDetail.setAddInfo("");
			ElaborationDAO.insertElaborationDetail(ctx, elaborationDetail);
			
			lineaAlbaran
			.getCOMPOSICIONPRODUCTOELABORADO()
			.getDATOSCOMPOSICIONPRODUCTO()
			.forEach(
					lineaComposicion -> {
						Item compositionItem = createItem(ctx,
								lineaComposicion.getPRODUCTO());
						ElaborationDetailComposition elaborationDetailComposition = new ElaborationDetailComposition();
						elaborationDetailComposition.setDomain(ctx
								.getDomainId());
						elaborationDetailComposition
						.setElaborationDetail(elaborationDetail);
						elaborationDetailComposition
						.setItem(compositionItem);
						elaborationDetailComposition.setQuantity(Double
								.valueOf(lineaComposicion.getCANTIDAD()));
						elaborationDetailComposition.setWarehouse(null);
						elaborationDetailComposition.setAddInfo(null);
						ElaborationDAO.insertElaborationDetailComposition(
								ctx, elaborationDetailComposition);
					});
		}
	}
	
	
	private void createCarrierPacking(AONContext ctx, ALBARANTYPE albaran) {
		CarrierPacking carrierPacking = new CarrierPacking();
		carrierPacking.setDomain(ctx.getDomainId());
		carrierPacking.setSeries("IGN"
				+ new SimpleDateFormat("yy").format(new Date()));
		carrierPacking.setNumber(null);
		carrierPacking.setType(CarrierPackingType.WAYBILL);
		carrierPacking.setStatus(CarrierPackingStatus.PENDING);
		Date issueDate = null;
		try {
			issueDate = getDateFormatter().parse(albaran.getDATOSHOJARUTA()
					.getFECHAEMISION());
		} catch (ParseException e) {
			issueDate = new Date();
		}
		carrierPacking.setIssueDate(issueDate);
		Carrier carrier = obtainCarrier(ctx, albaran.getDATOSHOJARUTA()
				.getDATOSAGENCIATRANSPORTE());
		carrierPacking.setCarrier(carrier.getId());
		carrierPacking.setDeliveryDate(null);
		carrierPacking.setCarrierReference(albaran.getDATOSHOJARUTA()
				.getREFERNCIAAGENCIATRANSPORTE());
		carrierPacking.setNumberPlate(albaran.getDATOSHOJARUTA()
				.getNUMEROMATRICULA());
		carrierPacking.setDriverName(albaran.getDATOSHOJARUTA()
				.getNOMBRECONDUCTOR());
		carrierPacking.setDriverDocument(albaran.getDATOSHOJARUTA()
				.getDOCUMENTOCONDUCTOR());
		WarehouseDAO.insertCarrierPacking(ctx, carrierPacking);
	}
	

	private Carrier obtainCarrier(AONContext ctx,
			DATOSAGENCIATRANSPORTETYPE datosagenciatransportetype) {
		Byte type = Byte.valueOf(datosagenciatransportetype.getDATOSREGISTRO()
				.getDATOSDOCUMENTO().getTIPODOCUMENTO());
		String document = datosagenciatransportetype.getDATOSREGISTRO()
				.getDATOSDOCUMENTO().getDOCUMENTO();
		Carrier carrier = RegistryDAO.getCarrierStream(
				ctx,
				f -> f.getDomainProperty().eq(ctx.getDomainId())
						.and(f.getDocumentTypeProperty().eq(type))
						.and(f.getDocumentProperty().eq(document))).findFirst().orElse(new Carrier());
		return carrier;
	}
	
	private boolean isPackageItem(DATOSLINEAALBARANTYPE linea) {
		return "S".equals(linea.getENVASE());
	}
	
	private Elaboration obtainElaboration(AONContext ctx,
			ELABORACIONORIGENTYPE datoselaboracionorigen) {
		List<Elaboration> list = ElaborationDAO.getElaborationList(
				ctx,
				f -> f.getDomainProperty()
						.eq(ctx.getDomainId())
						.and(f.getSeriesProperty()
								.eq(datoselaboracionorigen.getSERIE())
								.and(f.getNumberProperty().eq(
										Integer.valueOf(datoselaboracionorigen
												.getNUMERO())))));
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	private RAddress obtainAddress(AONContext ctx, Customer customer,
			DATOSDIRECCIONTYPE datosdireccionentrega) {
		RAddress raddress = RegistryDAO
				.getRAddressStream(
						ctx,
						f -> f.getDomainProperty()
								.eq(ctx.getDomainId())
								.and(f.getRegistryProperty()
										.eq(customer.getId())
										.and(f.getCityProperty()
												.eq(datosdireccionentrega
														.getCIUDAD())
												.and(f.getZipProperty()
														.eq(datosdireccionentrega
																.getCODIGOPOSTAL())))))
				.findFirst().orElse(new RAddress());
		return raddress;
	}
	
	private Customer obtainCustomer(AONContext ctx,
			DATOSCLIENTETYPE datoscliente) {
		Registry registry = RegistryDAO.getRegistry2(ctx, datoscliente
				.getDATOSREGISTRO().getDATOSDOCUMENTO().getDOCUMENTO());
		if (registry != null && registry.getId() != null) {
			Customer customer = RegistryDAO
					.getCustomerStream(
							ctx,
							f -> f.getDomainProperty()
									.eq(ctx.getDomainId())
									.and(f.getRegistryProperty().eq(
											registry.getId()))).findFirst()
					.orElse(new Customer());
			return customer;
		}
		return null;
	}
	
	private Item obtainItem(AONContext ctx, PRODUCTOTYPE productoelaborado) {
		Product product = ProductDAO
				.getProductStream(
						ctx,
						f -> f.getDomainProperty()
								.eq(ctx.getDomainId())
								.and(f.getCodeProperty().eq(
										productoelaborado.getCODIGO())))
				.findFirst().orElse(null);
		if (product != null && product.getId() != null) {
			List<Item> itemList = ProductDAO
					.getItemList(
							ctx,
							f -> f.getDomainProperty()
									.eq(ctx.getDomainId())
									.and(f.getProductProperty()
											.eq(product.getId())
											.and(StringUtils
													.isNotBlank(productoelaborado
															.getNUMEROLOTESERIE()) ? f
													.getSerialNumberProperty()
													.eq(productoelaborado
															.getNUMEROLOTESERIE())
													: f.getSerialNumberProperty()
															.isNull())));
			Item item = itemList == null || itemList.isEmpty() ? null
					: itemList.get(0);
			if (item == null || item.getId() == null) {
				item = createItem(ctx, productoelaborado);
			}
			return item;
		}
		return null;
	}
	
	private Item createItem(AONContext ctx, PRODUCTOTYPE productoelaborado) {
		Product product = ProductDAO
				.getProductStream(
						ctx,
						f -> f.getDomainProperty()
								.eq(ctx.getDomainId())
								.and(f.getCodeProperty().eq(
										productoelaborado.getCODIGO())))
				.findFirst().orElse(null);
		if (product != null && product.getId() != null) {
			Item item = ProductDAO
					.getItem(
							ctx,
							f -> f.getDomainProperty()
									.eq(ctx.getDomainId())
									.and(f.getProductProperty()
											.eq(product.getId())
											.and(StringUtils
													.isNotBlank(productoelaborado
															.getNUMEROLOTESERIE()) ? f
													.getSerialNumberProperty()
													.eq(productoelaborado
															.getNUMEROLOTESERIE())
													: f.getSerialNumberProperty()
															.isNull())));
			if(item==null || item.getId()==null){
				item = new Item();
				item.setDomain(ctx.getDomainId());
				item.setProductId(product.getId());
				item.setActive(true);
				item.setCode(productoelaborado.getCODIGO());
//				item.setBarcode(productoelaborado.getCODIGOBARRAS());
				item.setName(productoelaborado.getNOMBRE());
				item.setDescription(productoelaborado.getDESCRIPCION());
				item.setDetail(productoelaborado.getDETALLE());
				item.setDetail2(productoelaborado.getDETALLE2());
				item.setDetail3(productoelaborado.getDETALLE3());
				if(productoelaborado.getFECHALOTESERIE()!=null){
					Date serialDate = null;
					try {
						serialDate = getDateFormatter().parse(
								productoelaborado.getFECHALOTESERIE());
					} catch (ParseException e) {
						serialDate = new Date();
					}
					item.setSerialDate(new java.sql.Date(serialDate.getTime()));
				}
				if(productoelaborado.getNUMEROLOTESERIE()!=null){
					item.setSerialNumber(productoelaborado.getNUMEROLOTESERIE());
				}
				try {
					ProductDAO.insertItem(ctx, item);
				} catch (AonCoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				item = ProductDAO.getItemList(
						ctx,
						f -> f.getDomainProperty()
						.eq(ctx.getDomainId())
						.and(f.getProductProperty()
								.eq(product.getId())
								.and(f.getSerialNumberProperty().eq(
										productoelaborado
										.getNUMEROLOTESERIE()))))
										.getFirst();
			}
			return item;
		}
		return null;
	}
	
		
}
