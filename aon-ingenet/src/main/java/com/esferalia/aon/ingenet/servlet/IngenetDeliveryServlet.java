package com.esferalia.aon.ingenet.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.code.aon.common.AonException;
import com.esferalia.aon.ingenet.api.albaranes.ALBARANES;
import com.esferalia.aon.ingenet.api.albaranes.ALBARANTYPE;
import com.esferalia.aon.ingenet.api.albaranes.DATOSAGENCIATRANSPORTETYPE;
import com.esferalia.aon.ingenet.api.albaranes.DATOSCLIENTETYPE;
import com.esferalia.aon.ingenet.api.albaranes.DATOSDIRECCIONTYPE;
import com.esferalia.aon.ingenet.api.albaranes.DATOSLINEAALBARANTYPE;
import com.esferalia.aon.ingenet.api.albaranes.DATOSLINEAENVASETYPE;
import com.esferalia.aon.ingenet.api.albaranes.ELABORACIONORIGENTYPE;
import com.esferalia.aon.ingenet.api.albaranes.ERRORESTYPE;
import com.esferalia.aon.ingenet.api.albaranes.PRODUCTOTYPE;
import com.esferalia.aon.ingenet.api.util.IngenetXmlValidator;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.ElaborationDetail;
import com.esferalia.aon.occam.api.model.ElaborationDetailComposition;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.registry.NoteType;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryItem;
import com.esferalia.aon.occam.api.model.registry.RegistryItemStatus;
import com.esferalia.aon.occam.api.model.registry.RegistryMode;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DeliveryStatus;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.ElaborationStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingStatus;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingType;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.occam.impl.jooq.dao.ElaborationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SalesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WarehouseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WorkplaceDAO;

public class IngenetDeliveryServlet extends AbstractIngenetServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IngenetDeliveryServlet.class.getName());
	
	private List<String> errorList;
	
		
	protected void processRequest(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws ServletException, IOException {
		
		boolean test = true;
		
		String _xml = httpRequest.getParameter(PARAM_VALUE);
		
		String subject = "[AON] Recepcion automatica de albaranes";
		String content = "Se ha detectado una nueva comunicación para albaranes";
		sendEmail(subject, content, "delivery", _xml, RECIPIENTS_TO_LOG);
		saveToDisk("delivery", "delivery", _xml!=null?_xml:"");
		
		
		ALBARANES deliveryList = null;
		errorList = new LinkedList<>();
		if(_xml!=null){
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
				
				processData(deliveryList.getDATOSALBARANES(), true);
				test = "S".equals(deliveryList.getPRUEBA());
				if (!test && (errorList == null || errorList.size() <= 0)) {
					processData(deliveryList.getDATOSALBARANES(), test);
				}
			} else {
				errorList.add("No se han encontrado datos de albaranes");
			}
		} else {
			errorList.add("Es necesario el parametro 'value'");
		}
		
		List<ALBARANTYPE> invalidDeliveries = null;
		if(deliveryList==null){
			deliveryList = new ALBARANES();
		} else if(deliveryList.getDATOSALBARANES()!=null
				&& !deliveryList.getDATOSALBARANES().isEmpty()) {
			invalidDeliveries = deliveryList
					.getDATOSALBARANES()
					.stream()
					.filter(o -> o.getERRORES() != null
							&& o.getERRORES().getERRORES() != null
							&& !o.getERRORES().getERRORES().isEmpty())
					.collect(Collectors.toList());
			if(invalidDeliveries!=null && invalidDeliveries.size()>0){				
				deliveryList.getDATOSALBARANES().clear();
				deliveryList.getDATOSALBARANES().addAll(invalidDeliveries);
				errorList.add("Albaranes con errores: " + invalidDeliveries.size());
			}
		}
		
		if (errorList != null && errorList.size() > 0) {
			errorList.add(0, "Se han producido errores al procesar el fichero");
			errorList.forEach(System.out::println);
			flushErrors(httpResponse, deliveryList, errorList);
		} else {
			httpResponse.setStatus(HttpServletResponse.SC_OK);
			if (!test) {
				subject = "[AON] Recepcion automatica de albaranes";
				content = fillSuccessMessage(deliveryList.getDATOSALBARANES());
				sendEmail(subject, content, null, null, RECIPIENTS_TO_SUCCESS);
			}
		}
	}
	
	private void flushErrors(HttpServletResponse httpResponse, ALBARANES deliveryList,
			List<String> errorList) throws IOException {
		deliveryList.setERRORES(new ERRORESTYPE());
		errorList.forEach(error -> {
			deliveryList.getERRORES().getERRORES().add(error);
		});
		String xml = IngenetXmlValidator.convertToXml(deliveryList, ALBARANES.class);
		
		String subject = "[AON] Recepcion automatica de albaranes";
		String content = fillErrorMessage(deliveryList.getDATOSALBARANES(), errorList);
		sendEmail(subject, content, "albaranes", xml, RECIPIENTS_TO_FAILURES);
		
		httpResponse.setContentType("application/xml");
		httpResponse.setContentLength(xml.length());
		PrintWriter out = httpResponse.getWriter();
		out.print(xml);
		out.flush();
	}
	
	private String fillSuccessMessage(List<ALBARANTYPE> deliveryList) {
		StringBuffer bf = new StringBuffer("<h1>Recepción de albaranes.</h1>");
		bf.append("<ul>");
		deliveryList.forEach(alb -> {
			bf.append("<li>Albarán " + alb.getNUMERO()+" del " + alb.getFECHAEMISION()+"</li>");
		});
		bf.append("</ul>");
		return bf.toString();
	}
	
	private String fillErrorMessage(List<ALBARANTYPE> deliveryList, List<String> errorList) {
		StringBuffer bf = new StringBuffer("<h1>Recepción de albaranes.</h1>");
		bf.append("<ul>");
		errorList.forEach(error -> {
			if(error!=null)
				bf.append("<li>"+error+"</li>");
		});
		bf.append("</ul>");
		deliveryList.forEach(alb -> {
			bf.append("<h2>Albarán "+alb.getNUMERO()+"</h2>");
			bf.append("<ul>");
			alb.getERRORES().getERRORES().forEach(error -> {
				if(error!=null)
					bf.append("<li>"+error+"</li>");
			});
			bf.append("</ul>");
		});
		return bf.toString();
	}
	
	private void processData(List<ALBARANTYPE> list, boolean test){
		AONContext ctx = AONContext.getAONContext(getDomain(), getDomainId(), getUser());
		ctx.getDslContext().transaction(configuration -> {
			list.forEach(albaran -> {
				Delivery delivery = createDelivery(ctx, albaran, test);
				createCarrierPacking(ctx, albaran, delivery, test);
			});
		});
	}
	
	private void addError(ALBARANTYPE albaran, String msg){
		LOGGER.error(msg);
		if(albaran.getERRORES()==null){
			albaran.setERRORES(new ERRORESTYPE());
		}
		albaran.getERRORES().getERRORES().add(msg);
	}

	private Delivery createDelivery(AONContext ctx, ALBARANTYPE albaran, boolean test) {
		Delivery delivery = new Delivery(); 
		List<DeliveryDetail> detailList = new LinkedList<DeliveryDetail>();
		try {
			fillDelivery(ctx, albaran, delivery);
		} catch (Throwable th) {
			addError(albaran, th.getLocalizedMessage());
		}
		try {
			fillDeliveryDetailList(ctx, albaran, delivery, detailList, test);
		} catch (Throwable th) {
			addError(albaran, th.getLocalizedMessage());
		}
		
		if(!test){
			if(albaran.getERRORES()==null
				|| albaran.getERRORES().getERRORES()==null
				|| albaran.getERRORES().getERRORES().isEmpty()){
				
				try {
					Integer deliveryId = WarehouseDAO.insertDelivery(ctx, delivery);
					if(deliveryId!=null){
						delivery.setId(deliveryId);
						try {
							detailList.forEach(detail -> {detail.getDelivery().setId(deliveryId);});
							WarehouseDAO.insertDeliveryDetails(ctx, detailList);
						} catch (Throwable th) {
							addError(albaran, th.getLocalizedMessage());
						}
						try {
							albaran.getLINEASALBARAN().getDATOSLINEAALBARAN().forEach(linea -> {
								manageElaborations(ctx, albaran, linea, test);
							});
						} catch (Throwable th) {
							addError(albaran, th.getLocalizedMessage());
						}
						return delivery;
					}
				} catch (Throwable th) {
					addError(albaran, th.getLocalizedMessage());
					albaran.getLINEASALBARAN().getDATOSLINEAALBARAN().forEach(linea -> {
						failElaborations(ctx, albaran, linea, th.getLocalizedMessage());
					});
				}
				
			} else {
				albaran.getLINEASALBARAN().getDATOSLINEAALBARAN().forEach(linea -> {
					String cause = "";
					if(albaran.getERRORES()==null
							|| albaran.getERRORES().getERRORES()==null
							|| albaran.getERRORES().getERRORES().isEmpty()){
						for(String error: albaran.getERRORES().getERRORES()){
							cause += error+". ";
						}
					}
					failElaborations(ctx, albaran, linea, cause);
				});
			}
		}
		return null;
	}

	private Delivery fillDelivery(AONContext ctx, ALBARANTYPE albaran, Delivery delivery) {
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
		
		delivery.setDomain(ctx.getDomainId());
		delivery.setProject(new Project());
		delivery.setSeries(series);
		delivery.setNumber(++number);
		Customer customer = obtainCustomer(ctx, albaran.getDATOSCLIENTE());
		if (customer != null && customer.getId() != null) {
			delivery.setCustomer(customer.getId());
		} else {
			addError(albaran, "El cliente no se ha dado de alta: "
					+ albaran.getDATOSCLIENTE().getDATOSREGISTRO()
							.getDATOSDOCUMENTO().getDOCUMENTO());
		}
		delivery.setAddress(obtainAddress(ctx, customer, albaran.getDATOSDIRECCIONENTREGA()).getId());
		
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
		try {
			delivery.setWorkplace(wp.getId());
		} catch (Throwable th) {
			addError(albaran, th.getLocalizedMessage());
		}
		try {
			delivery.setScope(scopes.get(0).getId());
		} catch (Throwable th) {
			addError(albaran, th.getLocalizedMessage());
		}
		delivery.setPayMethod(null);
		delivery.setNumberOfPymnts((short) 0);
		delivery.setDaysToFirstPymnt((short) 0);
		delivery.setDaysBetweenPymnt((short) 0);
		delivery.setPymntDays("");
		delivery.setBankAccount("");
		delivery.setBankAlias("");
		delivery.setBic("");
		delivery.setCarrierPacking(null);
		return delivery;
	}


	private List<DeliveryDetail> fillDeliveryDetailList(AONContext ctx,
			ALBARANTYPE albaran, Delivery delivery, List<DeliveryDetail> detailList, boolean test) {
		List<DATOSLINEAALBARANTYPE> lineasAlbaran = null;
		if(albaran.getLINEASALBARAN()!=null){
			lineasAlbaran = albaran.getLINEASALBARAN().getDATOSLINEAALBARAN();
		}
		List<DATOSLINEAENVASETYPE> lineasEnvase = null;
		if(albaran.getLINEASENVASES()!=null){
			lineasEnvase = albaran.getLINEASENVASES().getDATOSLINEAENVASE();
		}
		Warehouse warehouse = WarehouseDAO.getWarehouse(
				ctx,
				f -> f.getDomainProperty()
				.eq(ctx.getDomainId())
				.and(f.getActiveProperty().eq((byte) 1))
				.and(f.getWorkplaceProperty().eq(
						delivery.getWorkplace())));
		
		if (lineasAlbaran != null && lineasAlbaran.size() > 0) {
			lineasAlbaran.stream()
			.forEach(
					linea -> {
						Item item;
						try {
							item = obtainItem(ctx,
									linea.getPRODUCTO(), test);
							Elaboration elaboration = obtainElaboration(
									ctx, linea.getDATOSELABORACIONORIGEN());
							RegistryItem customerRItem = obtainCustomerItem(item.getProduct().getId(), delivery.getCustomer());
							DeliveryDetail detail = new DeliveryDetail();
							detail.setDomain(ctx.getDomainId());
							detail.setDelivery(delivery);
							detail.setLine(Short.valueOf(linea.getLINEA()));
							detail.setItem(item);
							detail.setDescription(linea.getDESCRIPCION());
							detail.setWarehouse(warehouse.getId());
							detail.setQuantity(Double.valueOf(linea
									.getCANTIDAD()));
							if(customerRItem!=null){
								detail.setPrice(customerRItem.getPrice());
								detail.setDiscountExpression(customerRItem.getDiscountExpr());
							}
							if(detail.getPrice()==null || detail.getPrice().equals(0.0)){
								detail.setPrice(item.getPrice());
								detail.setDiscountExpression("0");
							}
							if(elaboration==null || elaboration.getId()==null){
								addError(albaran, "No hay ninguna elaboracion asociada a la linea " + linea.getLINEA());
							} else {
								detail.setSalesDetail(elaboration.getSourceId());
							}
							detailList.add(detail);
						} catch (Exception e) {
							addError(albaran, e.getMessage());
						}
					});
		}
		
		// TODO packages 
		if (lineasEnvase != null && lineasEnvase.size() > 0) {
			List<DATOSLINEAENVASETYPE> lineas = lineasEnvase
					.stream()
					.sorted((linea1, linea2) -> linea1.getLINEA().compareTo(
							linea2.getLINEA())).collect(Collectors.toList());
			Integer linesCount = detailList.size();
			for(DATOSLINEAENVASETYPE linea: lineas){
				Item item;
				try {
					item = obtainItem(ctx,
							linea.getPRODUCTO(), test);
					DeliveryDetail detail = new DeliveryDetail();
					detail.setDomain(ctx.getDomainId());
					detail.setDelivery(delivery);
					detail.setLine(Integer.valueOf(linesCount+Integer.valueOf(linea.getLINEA())).shortValue());
					detail.setItem(item);
					String packages = "[ENV=" + (linesCount+Integer.valueOf(linea.getLINEA()));
					if(linea.getLINEAENVASECONTENEDOR()!=null)
						packages += ";CONT=" + (linesCount+Integer.valueOf(linea.getLINEAENVASECONTENEDOR()));
					if(linea.getLINEAALBARANCONTENIDA()!=null)
						packages += ";LIN=" + linea.getLINEAALBARANCONTENIDA();
					packages += "]";
					delivery.setRemarks(delivery.getRemarks() + "\n" + packages );
					String description = linea.getDESCRIPCION()!=null?linea.getDESCRIPCION():item.getProduct().getName();
					detail.setDescription(description);
					detail.setWarehouse(warehouse.getId());
					detail.setDiscountExpression("0");
					detail.setQuantity(Double.valueOf(linea
							.getCANTIDAD()));
					detailList.add(detail);
				} catch (Exception e) {
					addError(albaran, e.getMessage());
				}
			}
		}
				
		return detailList;
	}

	
	private void failElaborations(AONContext ctx, ALBARANTYPE albaran,
			DATOSLINEAALBARANTYPE linea, String cause) {
		Elaboration elaboration = obtainElaboration(ctx,
				linea.getDATOSELABORACIONORIGEN());
		if (elaboration != null && elaboration.getId() != null) {
			elaboration.setStatus(ElaborationStatus.FAIL.value());
			elaboration.setComments(StringUtils.mid(cause, 0, 128));
			ElaborationDAO.updateElaboration(ctx, elaboration);
		}
	}
	
	private void manageElaborations(AONContext ctx, ALBARANTYPE albaran, DATOSLINEAALBARANTYPE linea,
			boolean test) {

		Elaboration elaboration = obtainElaboration(ctx,
				linea.getDATOSELABORACIONORIGEN());
		ElaborationDetail elaborationDetail = new ElaborationDetail();
		List<ElaborationDetailComposition> compositionList = new LinkedList<>();

		if (elaboration != null && elaboration.getId() != null) {

			try {
				elaborationDetail.setDomain(ctx.getDomainId());
				elaborationDetail.setElaboration(elaboration);
				elaborationDetail.setDate(new Date());
				elaborationDetail
						.setItem(obtainItem(ctx, linea.getPRODUCTO(), test));
				elaborationDetail.setQuantity(Double.valueOf(linea.getCANTIDAD()));
				elaborationDetail.setWarehouse(null);
				elaborationDetail.setAddInfo("");
			} catch (AonException e) {
				addError(albaran, e.getMessage());
			}

			linea.getCOMPOSICIONPRODUCTOELABORADO()
					.getDATOSCOMPOSICIONPRODUCTO()
					.forEach(
							lineaComposicion -> {
								try {
									Item compositionItem = createItem(ctx,
											lineaComposicion.getPRODUCTO(), test);
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
									compositionList.add(elaborationDetailComposition);
								} catch (Exception e) {
									addError(albaran, e.getMessage());
								}
							});
			
			if (!test) {
				int elaborationDetailId = ElaborationDAO
						.insertElaborationDetail(ctx, elaborationDetail);
				elaborationDetail.setId(elaborationDetailId);

				compositionList.forEach(c -> {
					c.setElaborationDetail(elaborationDetail);
					ElaborationDAO.insertElaborationDetailComposition(ctx, c);
				});

				elaboration.setStatus(ElaborationStatus.CLOSED.value());
				ElaborationDAO.updateElaboration(ctx, elaboration);
			}
		}
	}
	
	
	private void createCarrierPacking(AONContext ctx, ALBARANTYPE albaran, Delivery delivery, boolean test) {
		if(albaran.getDATOSHOJARUTA()!=null) {
			Carrier carrier = obtainCarrier(ctx, albaran.getDATOSHOJARUTA()
					.getDATOSAGENCIATRANSPORTE());
			if(carrier==null || carrier.getId()==null){
				LinkedList<Scope> scopes = SecurityDAO.getDomainScopes(ctx);
				DATOSAGENCIATRANSPORTETYPE at = albaran.getDATOSHOJARUTA().getDATOSAGENCIATRANSPORTE();
				carrier = new Carrier();
				carrier.setDomain(ctx.getDomainId());
				carrier.setScope(scopes.get(0).getId());
				carrier.setName(at.getDATOSREGISTRO().getNOMBRE());
				carrier.setAlias(at.getDATOSREGISTRO().getALIAS());
				carrier.setDocument(at.getDATOSREGISTRO().getDATOSDOCUMENTO().getDOCUMENTO());
				carrier.setDocumentCountry(Country.safeValueOf("ES"));
				carrier.setDocumentType(DocumentType.NIF);
				carrier.setNationality(Country.safeValueOf("ES"));
				carrier.setSecurityLevel(SecurityLevel.OFFICIAL);
				carrier.setType((byte)1);
				int id = WarehouseDAO.insertCarrier(ctx, carrier);
				carrier.setId(id);
			}
			if(carrier!=null && carrier.getId()!=null){
				CarrierPacking carrierPacking = null;
				try {
					carrierPacking = new CarrierPacking();
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
				} catch (Throwable th) {
					addError(albaran, th.getLocalizedMessage());
				}
				try {
					if(!test){
						Integer carrierPackingId = WarehouseDAO.insertCarrierPacking(ctx, carrierPacking);
						if(delivery!=null && delivery.getId()!=null){
							delivery.setCarrierPacking(carrierPackingId);
							WarehouseDAO.updateDelivery(ctx, delivery);
						}
					}
				} catch (Throwable th) {
					addError(albaran, th.getLocalizedMessage());
				}
			} else {
				String document = albaran.getDATOSHOJARUTA()
						.getDATOSAGENCIATRANSPORTE().getDATOSREGISTRO()
						.getDATOSDOCUMENTO().getDOCUMENTO();
				addError(albaran, "Agencia de transporte no dada de alta: "
						+ document);
			}
		}
	}
	

	private Carrier obtainCarrier(AONContext ctx,
			DATOSAGENCIATRANSPORTETYPE datosagenciatransportetype) {
//		Byte type = Byte.valueOf(datosagenciatransportetype.getDATOSREGISTRO()
//				.getDATOSDOCUMENTO().getTIPODOCUMENTO());
		String document = datosagenciatransportetype.getDATOSREGISTRO()
				.getDATOSDOCUMENTO().getDOCUMENTO();
		Carrier carrier = RegistryDAO.getCarrierStream(
				ctx,
				f -> f.getDomainProperty().eq(ctx.getDomainId())
//						.and(f.getDocumentTypeProperty().eq(type))
						.and(f.getDocumentProperty().eq(document))).findFirst().orElse(new Carrier());
		return carrier;
	}
	
	private Elaboration obtainElaboration(AONContext ctx,
			ELABORACIONORIGENTYPE datoselaboracionorigen) {
		String series = datoselaboracionorigen.getSERIE();
		Integer number = Integer.valueOf(datoselaboracionorigen.getNUMERO());
		List<Elaboration> list = ElaborationDAO.getElaborationList(
				ctx,
				f -> f.getDomainProperty()
						.eq(ctx.getDomainId())
						.and(f.getSeriesProperty().eq(series)
								.and(f.getNumberProperty().eq(number))));
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
		List<Customer> customerList = SalesDAO.getCustomerList(ctx, datoscliente
				.getDATOSREGISTRO().getDATOSDOCUMENTO().getDOCUMENTO());
		List<Integer> ids = customerList.stream().map(Customer::getId)
				.map(i -> Integer.valueOf(i)).collect(Collectors.toList());
		List<RegistryNote> ediRNotes = AON.getRNoteList(
				ctx.getDomainName(),
				ctx.getDomainId(),
				ctx.getUser(),
				f -> f.getNoteTypeProperty()
						.eq(NoteType.FACTURAE.value())
						.and(f.getRegistryProperty().in(
								ids.toArray(new Integer[ids.size()])
										)));
		// TODO create method in AON for search customer by document and edi enabled
		Customer customer = null;
		if (ediRNotes != null && ediRNotes.size() > 0) {
			try {
				RegistryNote ediRNote = ediRNotes.get(0);
				customer = AON.getCustomer(ctx.getDomainName(), ctx.getDomainId(),
						ctx.getUser(), ediRNote.getRegistry());
			} catch (Exception e) {
				customer = null;
			}
		}
		if (customer != null && customer.getId() != null) {
			return customer;
		}
		return null;
	}
	
	private Item obtainItem(AONContext ctx, PRODUCTOTYPE productoelaborado, boolean test) throws AonException {
		Product product = ProductDAO
				.getProductStream(
						ctx,
						f -> f.getDomainProperty()
								.eq(ctx.getDomainId())
								.and(f.getCodeProperty().eq(
										productoelaborado.getCODIGO())))
				.findFirst().orElse(null);
		if (product != null && product.getId() != null) {
			List<Item> itemList = AON
					.getItemList(
							ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(),
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
				item = createItem(ctx, productoelaborado, test);
			}
			return item;
		} else {
			throw new AonException("Codigo de producto no encontrado " + productoelaborado.getCODIGO());
		}
	}
	
	private Item createItem(AONContext ctx, PRODUCTOTYPE productoelaborado, boolean test) throws AonException {
		Product product = ProductDAO
				.getProductStream(
						ctx,
						f -> f.getDomainProperty()
								.eq(ctx.getDomainId())
								.and(f.getCodeProperty().eq(
										productoelaborado.getCODIGO())))
				.findFirst().orElse(null);
		if (product != null && product.getId() != null) {
			Item item = AON
					.getItem(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(),
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
				Item baseItem = AON
						.getItem(
								ctx.getDomainName(),
								ctx.getDomainId(),
								ctx.getUser(),
								f -> f.getDomainProperty()
										.eq(ctx.getDomainId())
										.and(f.getProductProperty().eq(
												product.getId()))
										.and(f.getSerialDateProperty().isNull())
										.and(f.getSerialNumberProperty()
												.isNull()));
				item = new Item();
				item.setDomain(ctx.getDomainId());
				item.setProductId(product.getId());
				item.setActive(false);
				item.setCode(productoelaborado.getCODIGO());
				item.setName(productoelaborado.getNOMBRE());
				item.setDescription(productoelaborado.getDESCRIPCION());
				item.setDetail(productoelaborado.getDETALLE());
				item.setDetail2(productoelaborado.getDETALLE2());
				item.setDetail3(productoelaborado.getDETALLE3());
				item.setPackFormatTag(baseItem.getPackFormatTag());
				item.setPackMeasurement(baseItem.getPackMeasurement());
				item.setPackMeasurementTag(baseItem.getPackMeasurementTag());
				item.setPackUnits(baseItem.getPackUnits());
				item.setPackUnitsTag(baseItem.getPackUnitsTag());
				item.setStockUnitTag(baseItem.getStockUnitTag());
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
				ProductDAO.insertItem(ctx, item);
				item = AON.getItemList(
						ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(),
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
		} else {
			throw new AonException("Codigo de producto no encontrado " + productoelaborado.getCODIGO());
		}
	}

	private RegistryItem obtainCustomerItem(Integer productId, Integer customerId) {
		Item baseItem = AON.getItem(
				getDomain(),
				getDomainId(),
				getUser(),
				f -> f.getDomainProperty().eq(getDomainId())
						.and(f.getProductProperty().eq(productId))
						.and(f.getSerialDateProperty().isNull())
						.and(f.getSerialNumberProperty().isNull()));

		List<RegistryItem> rItemList = AON.getRItemStream(
				getDomain(),
				getDomainId(),
				getUser(),
				f -> f.getDomainProperty()
						.eq(getDomainId())
						.and(f.getItemProperty().eq(baseItem.getId()))
						.and(f.getRegistryProperty().eq(customerId))
						.and(f.getTypeProperty().eq(
								RegistryMode.CUSTOMER.value()))
						.and(f.getStatusProperty().eq(
								RegistryItemStatus.ACTIVE.value())))
								.collect(Collectors.toList());

		if (rItemList != null && !rItemList.isEmpty()) {
			return rItemList.get(0);
		}
		return null;
	}
		
}
