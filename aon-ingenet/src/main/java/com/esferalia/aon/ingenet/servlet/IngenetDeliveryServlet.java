package com.esferalia.aon.ingenet.servlet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

import com.esferalia.aon.ingenet.api.albaranes.ALBARANES;
import com.esferalia.aon.ingenet.api.albaranes.ALBARANTYPE;
import com.esferalia.aon.ingenet.api.albaranes.DATOSAGENCIATRANSPORTETYPE;
import com.esferalia.aon.ingenet.api.albaranes.DATOSCLIENTETYPE;
import com.esferalia.aon.ingenet.api.albaranes.DATOSDIRECCIONTYPE;
import com.esferalia.aon.ingenet.api.albaranes.DATOSLINEAALBARANTYPE;
import com.esferalia.aon.ingenet.api.albaranes.ELABORACIONORIGENTYPE;
import com.esferalia.aon.ingenet.api.albaranes.PRODUCTOTYPE;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.ElaborationDetail;
import com.esferalia.aon.occam.api.model.ElaborationDetailComposition;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.DeliveryStatus;
import com.esferalia.aon.occam.api.model.type.ElaborationStatus;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingStatus;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingType;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.impl.jooq.dao.ElaborationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WarehouseDAO;

public class IngenetDeliveryServlet extends AbstractIngenetServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
	
	private final static String PARAM_VALUE = "value";
	
		
	protected void processRequest(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws ServletException, IOException {
		
		String _xml = httpRequest.getParameter(PARAM_VALUE);
		if(_xml!=null){
			ALBARANES deliveryList = extractValue(_xml);
			if(deliveryList!=null && deliveryList.getDATOSALBARANES()!=null 
					&& deliveryList.getDATOSALBARANES().size()>0){
				processData(deliveryList.getDATOSALBARANES());
				httpResponse.sendError(HttpServletResponse.SC_CREATED);
			} else {
				// TODO: deliveries file is empty
				httpResponse.sendError(HttpServletResponse.SC_NO_CONTENT);
			}
		} else {
			httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
		
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
		WarehouseDAO.insertDelivery(ctx, delivery);
		List<DeliveryDetail> detailList = new LinkedList<DeliveryDetail>();
		fillDeliveryDetailList(ctx, albaran.getLINEASALBARAN().getDATOSLINEAALBARAN(), delivery, detailList);
		WarehouseDAO.insertDeliveryDetails(ctx, detailList);
		return delivery;
	}

	private Delivery fillDelivery(AONContext ctx, ALBARANTYPE albaran, Delivery delivery) {
		delivery.setDomain(ctx.getDomainId());
		delivery.setProject(null);
		delivery.setSeries("IGN"+new SimpleDateFormat("yy").format(new Date()));
		delivery.setNumber(Integer.valueOf(albaran.getNUMERO()));
		Customer customer = obtainCustomer(ctx, albaran.getDATOSCLIENTE());
		delivery.setCustomer(customer.getId());
		delivery.setAddress(obtainAddress(ctx, customer, albaran.getDATOSDIRECCIONENTREGA()).getId());
		// if address == null, fill shippingAlternative
		
		try {
			delivery.setIssueTime(dateFormatter.parse(albaran.getFECHAEMISION()));
		} catch (ParseException e) {
			// TODO log me
			delivery.setIssueTime(new Date());
		}
		delivery.setSecurityLevel((byte) 0);
		delivery.setStatus(DeliveryStatus.PENDING);
		delivery.setComments(albaran.getCOMENTARIOS());
		delivery.setRemarks("Creado por '"+ctx.getUser()+"' el "
				+ new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
		delivery.setWorkplace(null);
		delivery.setScope(null);
		delivery.setPayMethod(null);
		delivery.setNumberOfPymnts((short) 0);
		delivery.setDaysToFirstPymnt((short) 0);
		delivery.setDaysBetweenPymnt((short) 0);
		delivery.setPymntDays(null);
		delivery.setBankAccount(null);
		delivery.setBankAlias(null);
		delivery.setBic(null);
		return delivery;
	}


	private List<DeliveryDetail> fillDeliveryDetailList(AONContext ctx,
			List<DATOSLINEAALBARANTYPE> list, Delivery delivery,
			List<DeliveryDetail> detailList) {
		if (list != null && list.size() > 0) {
			list.stream()
					.filter(linea -> !isPackageItem(linea))
					.forEach(
							linea -> {
								Item item = createItem(ctx,
										linea.getPRODUCTO());
								Elaboration elaboration = obtainElaboration(
										ctx, linea.getDATOSELABORACIONORIGEN());
								DeliveryDetail detail = new DeliveryDetail();
								detail.setDomain(ctx.getDomainId());
								detail.setDelivery(delivery);
								detail.setLine(Short.valueOf(linea.getLINEA()));
								detail.setItem(item);
								detail.setDescription(linea.getDESCRIPCION());
								detail.setWarehouse(null);
								detail.setQuantity(Double.valueOf(linea
										.getCANTIDAD()));
								detail.setPrice(item.getPrice());
								detail.setDiscountExpression("");
								detail.setSalesDetail(elaboration.getSourceId());
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
			issueDate = dateFormatter.parse(albaran.getDATOSHOJARUTA()
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
		RegistryDAO.getCarrierStream(
				ctx,
				f -> f.getDomainProperty().eq(ctx.getDomainId())
						.and(f.getDocumentTypeProperty().eq(type))
						.and(f.getDocumentProperty().eq(document)));
		return null;
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
			Item item = ProductDAO
					.getItemList(
							ctx,
							f -> f.getDomainProperty()
							.eq(ctx.getDomainId())
							.and(f.getProductProperty()
									.eq(product.getId())
									.and(f.getSerialNumberProperty().eq(
											productoelaborado
											.getNUMEROLOTESERIE()))))
											.getFirst();
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
			Item item = new Item();
			item.setDomain(ctx.getDomainId());
			item.setProductId(product.getId());
			item.setActive(true);
			item.setCode(productoelaborado.getCODIGO());
			item.setBarcode(productoelaborado.getCODIGOBARRAS());
			item.setName(productoelaborado.getNOMBRE());
			item.setDescription(productoelaborado.getDESCRIPCION());
			item.setDetail(productoelaborado.getDETALLE());
			item.setDetail2(productoelaborado.getDETALLE2());
			item.setDetail3(productoelaborado.getDETALLE3());
			Date serialDate = null;
			try {
				serialDate = dateFormatter.parse(
						productoelaborado.getFECHALOTESERIE());
			} catch (ParseException e) {
				serialDate = new Date();
			}
			item.setSerialDate(new java.sql.Date(serialDate.getTime()));
			item.setSerialNumber(productoelaborado.getNUMEROLOTESERIE());
			ProductDAO.insertItem(ctx, item);
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
		return null;
	}
	
	
	/*
	 * JAXB
	 */
	private ALBARANES extractValue(String xml) throws IOException {
    	InputStream inputStream = null;
    	try {
			byte[] bytes = xml.getBytes("UTF-8");
			inputStream = new ByteArrayInputStream(bytes);
			String contextPath = ALBARANES.class.getPackage().getName();
			JAXBContext context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			unmarshaller.setEventHandler(new AlbaranesValidationEventHandler());
			ALBARANES albaranes = (ALBARANES) unmarshaller.unmarshal(inputStream);
			return albaranes;
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} finally {
			if(inputStream!=null){
				inputStream.close();
			}
		}
    }
    
    public class AlbaranesValidationEventHandler implements ValidationEventHandler {
		public boolean handleEvent(ValidationEvent ve) {
			if (ve.getSeverity() == ValidationEvent.FATAL_ERROR || ve.getSeverity() == ValidationEvent.ERROR) {
				ValidationEventLocator locator = ve.getLocator();
				// Print message from valdation event
				System.out.println("Invalid value: " + locator.getURL());
				System.out.println("Error: " + ve.getMessage());
				// Output line and column number
				System.out.println("Error at column "
						+ locator.getColumnNumber() + ", line "
						+ locator.getLineNumber());
			}
			return true;
		}
	}
    
    
	

	public static void main(String[] args) throws Exception {
		String path = "http://";
		path += "udapa.esferalia.net";
		path += ":8080";
		path += "/aon-aio";
		path += "/ingenet/delivery";

		String user = "ingenet";
		String passwd = "1ng3n3t";
		
		String FILENAME = "C:\\TEMP\\delivery_example.xml";
		String xml = "";
		try (
			BufferedReader xml_br = new BufferedReader(new FileReader(FILENAME))) {
			String sCurrentLine;
			while ((sCurrentLine = xml_br.readLine()) != null) {
				xml += sCurrentLine;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        
        StringBuilder postData = new StringBuilder();
        postData.append('&');
        postData.append(URLEncoder.encode(PARAM_USERNAME, "UTF-8"));
        postData.append('=');
        postData.append(URLEncoder.encode(user, "UTF-8"));
        postData.append('&');
        postData.append(URLEncoder.encode(PARAM_PASSWORD, "UTF-8"));
        postData.append('=');
        postData.append(URLEncoder.encode(passwd, "UTF-8"));
        postData.append('&');
        postData.append(URLEncoder.encode(PARAM_VALUE, "UTF-8"));
        postData.append('=');
        postData.append(URLEncoder.encode(xml, "UTF-8"));
        
        byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8.name());

        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.connect();
        conn.getOutputStream().write(postDataBytes);

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8.name()));
        StringBuffer sb = new StringBuffer();
        for(String in; (in = br.readLine()) != null;) {
            sb.append(in + "\n");
        }
        br.close();
	}
		
}
