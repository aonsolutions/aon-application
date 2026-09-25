package com.esferalia.aon.ingenet.servlet.delivery;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.code.aon.common.AonException;
import com.esferalia.aon.ingenet.api.albaranes.ALBARANES;
import com.esferalia.aon.ingenet.api.albaranes.ALBARANTYPE;
import com.esferalia.aon.ingenet.api.albaranes.DATOSAGENCIATRANSPORTETYPE;
import com.esferalia.aon.ingenet.api.albaranes.DATOSCLIENTETYPE;
import com.esferalia.aon.ingenet.api.albaranes.DATOSLINEAALBARANTYPE;
import com.esferalia.aon.ingenet.api.albaranes.DATOSLINEAENVASETYPE;
import com.esferalia.aon.ingenet.api.albaranes.ERRORESTYPE;
import com.esferalia.aon.ingenet.api.util.IngenetXmlValidator;
import com.esferalia.aon.ingenet.util.ProductUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.registry.NoteType;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.RegistryItem;
import com.esferalia.aon.occam.api.model.registry.RegistryItemStatus;
import com.esferalia.aon.occam.api.model.registry.RegistryMode;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.CarrierStatus;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.DeliveryStatus;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.api.model.type.SalesDetailStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingStatus;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingType;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.occam.impl.jooq.dao.CarrierDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SalesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TaxDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WarehouseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WorkplaceDAO;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

@Deprecated
public abstract class AbstractDeliveryCreator implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDeliveryCreator.class.getName());

	private SimpleDateFormat dateFormatter;
	
	private List<Delivery> deliveryList = null;
	private ALBARANES albaranes = null;
	
	private List<String> errorList;
	private List<String> warningList;

	private String domain;
	private Integer domainId;
	private String user;
	
	
	public AbstractDeliveryCreator(String domain, Integer domainId, String user) {
		this.domain = domain;
		this.domainId = domainId;
		this.user = user;
	}
	
	protected abstract void manageSales(AONContext ctx, ALBARANTYPE albaran, DATOSLINEAALBARANTYPE linea, boolean test);
	protected abstract void fillDeliveryDetailSourceSales(AONContext ctx, DeliveryDetail detail, ALBARANTYPE albaran, DATOSLINEAALBARANTYPE linea);
	protected abstract void afterDeliverySaved(AONContext ctx, ALBARANTYPE albaran, boolean test);
	protected abstract void afterDeliverySavedFail(AONContext ctx, ALBARANTYPE albaran, Throwable th);
	protected abstract void manageAlbaranErrors(AONContext ctx, ALBARANTYPE albaran);
	public abstract void fillSuccessMessage(StringBuffer bf, DATOSLINEAALBARANTYPE lin);
		
	public String getDomain() {
		return domain;
	}
	public Integer getDomainId() {
		return domainId;
	}
	public String getUser() {
		return user;
	}
	public List<String> getErrorList() {
		return errorList;
	}
	public void setErrorList(List<String> errorList) {
		this.errorList = errorList;
	}
	public List<String> getWarningList() {
		return warningList;
	}
	public void setWarningList(List<String> warningList) {
		this.warningList = warningList;
	}
	public List<Delivery> getDeliveryList() {
		return deliveryList;
	}
	public void setDeliveryList(List<Delivery> deliveryList) {
		this.deliveryList = deliveryList;
	}
	public ALBARANES getAlbaranes() {
		return albaranes;
	}
	public void setAlbaranes(ALBARANES albaranes) {
		this.albaranes = albaranes;
	}

	public void create(String _xml) throws IOException {
		
		boolean test = true;
		
		
		errorList = new LinkedList<>();
		warningList = new LinkedList<>();
		
		if(_xml==null){
			errorList.add("Contenido del mensaje vacío, es necesario el parametro 'value'.");
		} else {
			albaranes = (ALBARANES) IngenetXmlValidator.extractValue(_xml, ALBARANES.class);
			albaranes.setERRORES(null);
			albaranes.getDATOSALBARANES().forEach(alb->alb.setERRORES(null));
			
			if(albaranes!=null && albaranes.getDATOSALBARANES()!=null 
					&& albaranes.getDATOSALBARANES().size()>0){
				
				
				deliveryList = new LinkedList<>();
				processDelivery(albaranes.getDATOSALBARANES(), deliveryList, true);
				if(albaranes.getERRORES()!=null)
					errorList.addAll(albaranes.getERRORES().getERRORES());
				albaranes.getDATOSALBARANES().forEach(albaran -> {
					if(albaran.getERRORES()!=null)
						errorList.addAll(albaran.getERRORES().getERRORES());
				});
				
				
				test = "S".equals(albaranes.getPRUEBA());
				if (test)
					warningList.add("Los datos han sido enviados en MODO DE PRUEBAS, no se guardarán.");
				
				if (!test && (errorList == null || errorList.size() <= 0)) {
					errorList.clear();
					warningList.clear();
					
					deliveryList = new LinkedList<>();
					processDelivery(albaranes.getDATOSALBARANES(), deliveryList, test);
					if(deliveryList!=null && deliveryList.size()>0){
						processSourceSales(albaranes.getDATOSALBARANES(), deliveryList, test);
					}
				}
			} else {
				errorList.add("No se han encontrado datos de albaranes");
			}
		}
		
		errorList.removeIf(error->error==null);
		warningList.removeIf(warn->warn==null);
		
	}
	
	public void validateAlbaranesXmlPattern(String _xml) throws IOException, SAXException {
		validateAlbaranesXmlPattern(new ByteArrayInputStream(_xml.getBytes()));
	}
	
	private void validateAlbaranesXmlPattern(InputStream xmlStream) throws IOException, SAXException {
		IngenetXmlValidator.validateXmlPattern(xmlStream, IngenetXmlValidator.SCHEMA_FILE_NAME_ALBARANES);
	}
	
	protected void addError(ALBARANTYPE albaran, String msg){
		LOGGER.error(msg);
		if(albaran.getERRORES()==null){
			albaran.setERRORES(new ERRORESTYPE());
		}
		albaran.getERRORES().getERRORES().add(msg);
	}
	
	private void processDelivery(List<ALBARANTYPE> list, List<Delivery> deliveryList, boolean test){
		deliveryList.clear();
		CloseableAONContext ctx =AONContext.getAONContext(getDomain(), getDomainId(), getUser());
		try {
			ctx.getDslContext().transaction(configuration -> {
				list.forEach(albaran -> {
					Delivery delivery = createDelivery(ctx, albaran, test);
					deliveryList.add(delivery);
					createCarrierPacking(ctx, albaran, delivery, test);
				});
			});
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	protected void processSourceSales(List<ALBARANTYPE> list, List<Delivery> deliveryList, boolean test) {
		if(!test){
			manageSalesDetail(deliveryList);
			
			CloseableAONContext ctx =AONContext.getAONContext(getDomain(), getDomainId(), getUser());
			try {
				ctx.getDslContext().transaction(configuration -> {
					list.forEach(albaran -> {
						albaran.getLINEASALBARAN().getDATOSLINEAALBARAN().forEach(linea -> {
							manageSales(ctx, albaran, linea, test);
						});
					});
				});
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
	}
	
	protected Delivery createDelivery(AONContext ctx, ALBARANTYPE albaran, boolean test) {
		albaran.setERRORES(null);
		Delivery delivery = new Delivery(); 
		List<DeliveryDetail> detailList = new LinkedList<>();
		Attach attach = new Attach();

		try {
			// TODO checkExistingDelivery
			checkExistingDelivery(ctx, albaran);
			fillDelivery(ctx, albaran, delivery);
		} catch (Throwable th) {
			th.printStackTrace();
			addError(albaran, th.getLocalizedMessage());
		}
		try {
			fillDeliveryDetailList(ctx, albaran, delivery, detailList, test);
		} catch (Throwable th) {
			th.printStackTrace();
			addError(albaran, th.getLocalizedMessage());
		}
		try {
			fillAttach(ctx, albaran, delivery, detailList, attach, test);
		} catch (Throwable th) {
			th.printStackTrace();
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
							th.printStackTrace();
							addError(albaran, th.getLocalizedMessage());
						}
						
						afterDeliverySaved(ctx, albaran, test);
						
						try {
							attach.setSourceBatch(deliveryId);
							AON.insertAttach(ctx.getDomainName(),
									ctx.getDomainId(),
									ctx.getUser(), attach);
						} catch (Throwable th) {
							th.printStackTrace();
							addError(albaran, th.getLocalizedMessage());
						}
						return delivery;
					}
				} catch (Throwable th) {
					th.printStackTrace();
					addError(albaran, th.getLocalizedMessage());
					afterDeliverySavedFail(ctx, albaran, th);
				}
				
			} else {
				manageAlbaranErrors(ctx, albaran);
			}
		}
		return null;
	}
	
	protected void checkExistingDelivery(AONContext ctx, ALBARANTYPE albaran) {
		long deliveryCount = 0;
		try {
			deliveryCount = WarehouseDAO
					.getDeliveryList(
							ctx,
							f -> f.getDomainProperty().eq(ctx.getDomainId())
									.and(f.getSeriesProperty().like(albaran.getSERIE()))
									.and(f.getNumberProperty().eq(Integer.parseInt(albaran.getNUMERO()))))
					.stream().count();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO log me
//			String errorMsg = "Error desconocido comprobando si existe el albaran " 
//					+ albaran.getSERIE() + "/" + albaran.getNUMERO();
//			addError(albaran, errorMsg);
		}
		if(deliveryCount>0){
			Stream<Delivery> stream = WarehouseDAO
					.getDeliveryList(
							ctx,
							f -> f.getDomainProperty().eq(ctx.getDomainId())
									.and(f.getSeriesProperty().like(albaran.getSERIE()))
									.and(f.getNumberProperty().eq(Integer.parseInt(albaran.getNUMERO()))))
					.stream();
			
			// TODO log me
//			stream.forEach(delivery -> {
//				WarehouseDAO.deleteDelivery(ctx, delivery.getId());
//			});
		}
	}

	protected Delivery fillDelivery(AONContext ctx, ALBARANTYPE albaran, Delivery delivery) {
		LinkedList<Scope> scopes = SecurityDAO.getDomainScopes(ctx);
		
		Workplace wp = WorkplaceDAO.get(ctx, f -> f
				.getDomainProperty().eq(ctx.getDomainId())
				.and(f.getActiveProperty().eq((byte)1)), new Options().setSecurity(false));
		
		delivery.setDomain(ctx.getDomainId());
		delivery.setProject(new Project());
		
		String series = albaran.getSERIE();
		int number = 0;
		try {
			number = Integer.parseInt(albaran.getNUMERO());
		} catch (Exception e) {
			e.printStackTrace();
			String errorMsg = "Error desconocido comprobando el numero de albaran "
					+ albaran.getNUMERO() + ": " + e.getMessage();
			addError(albaran, errorMsg);
		}
		
		long deliveryCount = 0;
		try {
			deliveryCount = WarehouseDAO
					.getDeliveryList(
							ctx,
							f -> f.getDomainProperty().eq(ctx.getDomainId())
									.and(f.getSeriesProperty().like(albaran.getSERIE()+"%"))
									.and(f.getNumberProperty().eq(Integer.parseInt(albaran.getNUMERO()))))
					.stream().count();
		} catch (Exception e) {
			e.printStackTrace();
			String errorMsg = "Error desconocido comprobando si existe el albaran " 
					+ albaran.getSERIE() + "/" + albaran.getNUMERO();
			addError(albaran, errorMsg);
		}
		
		if(deliveryCount>0){
			series = series.length()<5
					? (series.length()<=3?series:series.substring(0, 2))+"*"+(deliveryCount>1?deliveryCount:"")
					: "IGN"+new SimpleDateFormat("yy").format(new Date());
			String errorMsg = "El albaran " + albaran.getSERIE()
					+ "/" + albaran.getNUMERO() + " ya existe";
			// + ". Se guarda con " + series + "/" + number;
			addError(albaran, errorMsg);
			// warningList.add(errorMsg);			
		}
		delivery.setSeries(series);
		delivery.setNumber(number);
		
		Customer customer = obtainCustomer(ctx, albaran.getDATOSCLIENTE());
		if (customer != null && customer.getId() != null) {
			delivery.setCustomer(customer);
		} else {
			addError(albaran, "El cliente no se ha dado de alta: "
					+ albaran.getDATOSCLIENTE().getDATOSREGISTRO()
							.getDATOSDOCUMENTO().getDOCUMENTO());
		}
		delivery.setAddress(ProductUtils.obtainAddress(ctx, customer, albaran.getDATOSDIRECCIONENTREGA()));
		
		try {
			delivery.setIssueTime(getDateFormatter().parse(albaran.getFECHAEMISION()));
		} catch (ParseException e) {
			e.printStackTrace();
			System.err.println("Cannot parse date value. Reason: "+ e.getMessage());
			delivery.setIssueTime(new Date());
		}
		delivery.setSecurityLevel(SecurityLevel.OFFICIAL);
		delivery.setStatus(DeliveryStatus.PENDING);
		delivery.setComments(null);
		delivery.setRemarks("Creado por '"+ctx.getUser()+"' el "
				+ new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()) + "."
				+ "\n" + "La unidad de la cantidad es KILOS." );
		try {
			delivery.setWorkplace(wp);
		} catch (Throwable th) {
			th.printStackTrace();
			addError(albaran, th.getLocalizedMessage());
		}
		try {
			delivery.setScope(scopes.get(0));
		} catch (Throwable th) {
			th.printStackTrace();
			addError(albaran, th.getLocalizedMessage());
		}
		
		delivery.setTotalPackages(0.0);
		delivery.setTotalWeight(0.0);
		delivery.setPayMethod(null);
		delivery.setNumberOfPymnts((short) 0);
		delivery.setDaysToFirstPymnt((short) 0);
		delivery.setDaysBetweenPymnt((short) 0);
		delivery.setPymntDays("");
		delivery.setBankAccount("");
		delivery.setBankAlias("");
		delivery.setBic("");
		delivery.setCarrierPacking(null);
		delivery.setCreationUser(ctx.getUser());
		delivery.setCreationDate(new Date());
		return delivery;
	}


	protected List<DeliveryDetail> fillDeliveryDetailList(AONContext ctx,
			ALBARANTYPE albaran, Delivery delivery, List<DeliveryDetail> detailList, boolean test) {
		List<DATOSLINEAALBARANTYPE> lineasAlbaran = null;
		if(albaran.getLINEASALBARAN()!=null){
			lineasAlbaran = albaran.getLINEASALBARAN().getDATOSLINEAALBARAN();
		}
		List<DATOSLINEAENVASETYPE> lineasEnvase = null;
		if(albaran.getLINEASENVASES()!=null){
			lineasEnvase = albaran.getLINEASENVASES().getDATOSLINEAENVASE();
		}
		Warehouse warehouse = WarehouseDAO.get(
				ctx,
				f -> f.getDomainProperty()
				.eq(ctx.getDomainId())
				.and(f.getActiveProperty().eq((byte) 1))
				.and(f.getWorkplaceProperty().eq(
						delivery.getWorkplace().getId())));
		
		if(warehouse.isEmpty()) {
		    warehouse = WarehouseDAO.get(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()).and(f.getActiveProperty().eq((byte) 1)));
		}
		Integer warehouseId = warehouse.getId();
		if (lineasAlbaran != null && lineasAlbaran.size() > 0) {
			lineasAlbaran.stream()
			.forEach(
					linea -> {
						Item item;
						try {
							item = ProductUtils.obtainItem(ctx, linea.getPRODUCTO());
							RegistryItem customerRItem = obtainCustomerItem(item.getProduct().getId(), delivery.getCustomer().getId());
							DeliveryDetail detail = new DeliveryDetail();
							detail.setDomain(ctx.getDomainId());
							detail.setDelivery(delivery);
							detail.setLine(Short.valueOf(linea.getLINEA()));
							detail.setItem(item);
							String description = item.getProduct().getName();
							description += " #" + item.getSerialNumber();
							String document = albaran.getDATOSCLIENTE().getDATOSREGISTRO().getDATOSDOCUMENTO().getDOCUMENTO();
							if(ProductUtils.isEroski(document)
							        && AonStringUtils.containsIgnoreCase(description, "natur")) {
							    description += " CUMPLE TOTALMENTE GRASP";
							}
							detail.setDescription(description);
							detail.setWarehouse(warehouseId);
							detail.setQuantity(Double.valueOf(linea
									.getCANTIDAD()));

							fillDeliveryDetailSourceSales(ctx, detail, albaran, linea);
							
							if(detail.getPrice()==null && customerRItem!=null){
								detail.setPrice(customerRItem.getPrice());
								detail.setDiscountExpression(customerRItem.getDiscountExpression().getDiscountExpr());
							}
							if(detail.getPrice()==null){
								detail.setPrice(item.getPrice());
								detail.setDiscountExpression("0");
							}
							detail.setCreationUser(ctx.getUser());
							detail.setCreationDate(new Date());
							detailList.add(detail);
						} catch (Exception e) {
							e.printStackTrace();
							addError(albaran, e.getMessage());
						}
					});
		}
		
		if (lineasEnvase != null && lineasEnvase.size() > 0) {
			List<DATOSLINEAENVASETYPE> lineas = lineasEnvase
					.stream()
					.sorted((linea1, linea2) -> linea1.getLINEA().compareTo(
							linea2.getLINEA())).collect(Collectors.toList());
			Integer linesCount = detailList.size();
			for(DATOSLINEAENVASETYPE linea: lineas){
				Item item = null;
				try {
					item = ProductUtils.obtainItem(ctx, linea.getPRODUCTO());
				} catch (Exception e) {
					e.printStackTrace();
					warningList.add("[ENVASES] " + e.getMessage());
				}
				if(item==null || item.getId()==null){
					try {
						item = ProductUtils.createPackage(ctx, linea.getPRODUCTO());
						warningList.add("Nuevo envase creado " + linea.getPRODUCTO().getCODIGO());
					} catch (AonException e) {
						e.printStackTrace();
						addError(albaran, "[ENVASES] " + e.getMessage());
					}
				}
				try {
					if(item!=null){
						DeliveryDetail detail = new DeliveryDetail();
						detail.setDomain(ctx.getDomainId());
						detail.setDelivery(delivery);
						detail.setLine(Integer.valueOf(linesCount+Integer.valueOf(linea.getLINEA())).shortValue());
						detail.setItem(item);
						String description = linea.getDESCRIPCION()!=null?linea.getDESCRIPCION():item.getProduct().getName();
						detail.setDescription(description);
						detail.setWarehouse(warehouse.getId());
						detail.setDiscountExpression("0");
						detail.setQuantity(Double.valueOf(linea
								.getCANTIDAD()));
						detail.setCreationUser(ctx.getUser());
						detail.setCreationDate(new Date());
						detailList.add(detail);
					}
				} catch (Exception e) {
					e.printStackTrace();
					addError(albaran, "[ENVASES] " + e.getMessage());
				}
			}
		}
				
		return detailList;
	}
	
	
	/**
	 * 
	 * ELABORATIONS
	 * 
	 */
//	private void manageElaborations(AONContext ctx, ALBARANTYPE albaran, DATOSLINEAALBARANTYPE linea,
//			boolean test) {
//
//		Elaboration elaboration = obtainElaboration(ctx,
//				linea.getDATOSELABORACIONORIGEN());
//		ElaborationDetail elaborationDetail = new ElaborationDetail();
//		List<ElaborationDetailComposition> compositionList = new LinkedList<>();
//
//		if (elaboration != null && elaboration.getId() != null) {
//
//			try {
//				elaborationDetail.setDomain(ctx.getDomainId());
//				elaborationDetail.setElaboration(elaboration);
//				elaborationDetail.setDate(new Date());
//				elaborationDetail
//						.setItem(obtainItem(ctx, linea.getPRODUCTO(), test));
//				elaborationDetail.setQuantity(Double.valueOf(linea.getCANTIDAD()));
//				elaborationDetail.setWarehouse(null);
//				elaborationDetail.setAddInfo("");
//			} catch (AonException e) {
//				e.printStackTrace();
//				String msg = "[Producto " + linea.getPRODUCTO().getCODIGO() + "] ";
//				addError(albaran, msg + e.getMessage());
//			}
//
//			linea.getCOMPOSICIONPRODUCTOELABORADO()
//					.getDATOSCOMPOSICIONPRODUCTO()
//					.forEach(
//							lineaComposicion -> {
//								try {
//									Item compositionItem = createItem(ctx,
//											lineaComposicion.getPRODUCTO(), test);
//									ElaborationDetailComposition elaborationDetailComposition = new ElaborationDetailComposition();
//									elaborationDetailComposition.setDomain(ctx
//											.getDomainId());
//									elaborationDetailComposition
//									.setElaborationDetail(elaborationDetail);
//									elaborationDetailComposition
//									.setItem(compositionItem);
//									elaborationDetailComposition.setQuantity(Double
//											.valueOf(lineaComposicion.getCANTIDAD()));
//									elaborationDetailComposition.setWarehouse(null);
//									elaborationDetailComposition.setAddInfo(null);
//									elaborationDetailComposition.setCreationUser(ctx.getUser());
//									elaborationDetailComposition.setCreationDate(new Date());
//									compositionList.add(elaborationDetailComposition);
//								} catch (Exception e) {
//									String msg = "[Compuesto " + lineaComposicion.getPRODUCTO().getCODIGO() + "] ";
//									addError(albaran, msg + e.getMessage());
//								}
//							});
//			
//			if (!test) {
//				int elaborationDetailId = ElaborationDAO
//						.insertElaborationDetail(ctx, elaborationDetail);
//				elaborationDetail.setId(elaborationDetailId);
//
//				compositionList.forEach(c -> {
//					c.setElaborationDetail(elaborationDetail);
//					ElaborationDAO.insertElaborationDetailComposition(ctx, c);
//				});
//
//				elaboration.setStatus(ElaborationStatus.CLOSED.value());
//				elaboration.setModificationUser(ctx.getUser());
//				elaboration.setModificationDate(new Date());
//				ElaborationDAO.updateElaboration(ctx, elaboration);
//			}
//		}
//	}
//	
//	private void failElaborations(AONContext ctx, ALBARANTYPE albaran,
//			DATOSLINEAALBARANTYPE linea, String cause) {
//		Elaboration elaboration = obtainElaboration(ctx,
//				linea.getDATOSELABORACIONORIGEN());
//		if (elaboration != null && elaboration.getId() != null) {
//			elaboration.setStatus(ElaborationStatus.FAIL.value());
//			elaboration.setRemarks(StringUtils.mid(cause, 0, 128));
//			elaboration.setModificationUser(ctx.getUser());
//			elaboration.setModificationDate(new Date());
//			ElaborationDAO.updateElaboration(ctx, elaboration);
//		}
//	}
//	
//	private Elaboration obtainElaboration(AONContext ctx,
//			ELABORACIONORIGENTYPE datoselaboracionorigen) {
//		String series = datoselaboracionorigen.getSERIE();
//		Integer number = Integer.valueOf(datoselaboracionorigen.getNUMERO());
//		List<Elaboration> list = ElaborationDAO.getElaborationList(
//				ctx,
//				f -> f.getDomainProperty()
//						.eq(ctx.getDomainId())
//						.and(f.getSeriesProperty().eq(series)
//								.and(f.getNumberProperty().eq(number))));
//		if (list != null && !list.isEmpty()) {
//			return list.get(0);
//		}
//		return null;
//	}
	
	
	
	protected RegistryItem obtainCustomerItem(Integer productId, Integer customerId) {
		OldItem baseItem = AON.getItem(
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
	
	protected Attach fillAttach(AONContext ctx, ALBARANTYPE albaran,
			Delivery delivery, List<DeliveryDetail> detailList, Attach attach,
			boolean test) {
		List<DATOSLINEAENVASETYPE> lineasEnvase = null;
		if(albaran.getLINEASENVASES()!=null){
			lineasEnvase = albaran.getLINEASENVASES().getDATOSLINEAENVASE();
		}
		
		if (lineasEnvase != null && lineasEnvase.size() > 0) {
			List<DATOSLINEAENVASETYPE> lineas = lineasEnvase
					.stream()
					.sorted((linea1, linea2) -> linea1.getLINEA().compareTo(
							linea2.getLINEA())).collect(Collectors.toList());
			Integer linesCount = albaran.getLINEASALBARAN().getDATOSLINEAALBARAN().size();
			StringBuilder packagesBuilder = new StringBuilder();

			// CONTENEDORES (lo que va sobre el transporte)
			for(DATOSLINEAENVASETYPE linea: lineas){
				try {
					if(linea.getLINEAENVASECONTENEDOR()!=null){
						String sscc = linea.getSSCC();
						packagesBuilder.append("[ENV=")
							.append(linesCount+Integer.valueOf(linea.getLINEA()))
							.append(";CONT=")
							.append(linesCount+Integer.valueOf(linea.getLINEAENVASECONTENEDOR()))
							.append(sscc!=null?";SSCC="+linea.getSSCC():"")
							.append("]");
					}
				} catch (Exception e) {
					e.printStackTrace();
					addError(albaran, e.getMessage());
				}
			}
			
			// ENVASES (lo que va dentro de los contenedores)
			for(DATOSLINEAENVASETYPE linea: lineas){
				try {
					if(linea.getLINEAALBARANCONTENIDA()!=null){
						String sscc = linea.getSSCC();
						packagesBuilder.append("[ENV=")
							.append(linesCount+Integer.valueOf(linea.getLINEA()))
							.append(";LIN=")
							.append(linea.getLINEAALBARANCONTENIDA())
							.append(sscc!=null?";SSCC="+linea.getSSCC():"")
							.append("]");
					}
				} catch (Exception e) {
					e.printStackTrace();
					addError(albaran, e.getMessage());
				}
			}
			
			
			attach.setAttachType(AttachType.DATA);
			attach.setDomain(new Domain().setId(delivery.getDomain()));
			attach.setDate(new Date());
			attach.setData(packagesBuilder.toString().getBytes());
			attach.setMimeType(MimeType.TXT);
			attach.setSourceType(DataAttachSource.DELIVERY.value());
			attach.setSourceBatch(delivery.getId());
			attach.setType((byte)0);
		}
		return attach;
	}
	
	/**
	 * 
	 * SALES
	 * 
	 */
	private SalesDetail obtainSalesDetail(AONContext ctx, Integer detailId) {
		return SalesDAO.getSalesDetail(ctx, detailId);
	}
	private void manageSalesDetail(List<Delivery> deliveryList){
		deliveryList.forEach(delivery->{			
			AON.getDeliveryDetailStream(getDomain(), getDomainId(), getUser(),
					f->f.getDelivery().eq(delivery.getId()))
			.filter(d->d.getSalesDetail()!=null)
			.collect(Collectors.groupingBy(DeliveryDetail::getSalesDetail,
					Collectors.summingDouble(DeliveryDetail::getQuantity)))
			.forEach((salesDetailId, totalQuantity) -> {
				SalesDetail salesDetail = AON.getSalesDetailStream(getDomain(), getDomainId(), getUser(),
						f->f.getIdProperty().eq(salesDetailId)).findFirst().orElse(null);
				if(salesDetail!=null){
					Double delivered = salesDetail.getDelivered();
					delivered += totalQuantity;
					salesDetail.setDelivered(delivered);
					if(delivered>0.0 && delivered<=salesDetail.getQuantity()) {
						if(delivered<salesDetail.getQuantity()) {
							salesDetail.setStatus(SalesDetailStatus.PARTIAL_SETTLED);
						} else {
							salesDetail.setStatus(SalesDetailStatus.SETTLED);
						}
					}
					AON.updateSalesDetail(getDomain(), getDomainId(), getUser(), salesDetail);
				}
			});
		});
	}
	
//	private void manageSales(AONContext ctx, ALBARANTYPE albaran, DATOSLINEAALBARANTYPE linea, boolean test) {
//		Elaboration elaboration = obtainElaboration(ctx,
//				linea.getDATOSELABORACIONORIGEN());
//		if (elaboration != null && elaboration.getId() != null
//				&& elaboration.getSource() == 0 // SALES
//				&& elaboration.getSourceId() != null) {
//			Integer salesDetailId = elaboration.getSourceId();
//			try {
//				SalesDetail sd = AON.getSalesDetailStream(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(),
//						f -> f.getIdProperty().eq(salesDetailId)).findFirst().orElse(null);
//				if (!test) {
//					if(sd!=null && sd.getId()!=null){
//						Sales sales = sd.getSales();
//						Double totalSalesPending = AON.getSalesDetailStream(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(),
//								f -> f.getSalesProperty().eq(sales.getId()))
//								.mapToDouble(o->o.getQuantity()-o.getDelivered()).sum();
//						if(totalSalesPending==0.0){
//							sales.setStatus(SalesStatus.SERVED);
//							SalesDAO.updateSales(ctx, sales);
//						}
//					}
//				}
//			} catch (Exception e) {
//				String msg = "[Pedido origen] ";
//				warningList.add(msg + e.getMessage());
//			}
//		}
//		
//	}
	
	/**
	 * 
	 * CARRIER PACKING
	 * 
	 */
	protected void createCarrierPacking(AONContext ctx, ALBARANTYPE albaran, Delivery delivery, boolean test) {
		if(albaran.getDATOSHOJARUTA()!=null) {
			Carrier carrier = obtainCarrier(ctx, albaran.getDATOSHOJARUTA()
					.getDATOSAGENCIATRANSPORTE());
			if(carrier==null || carrier.getId()==null){
				LinkedList<Scope> scopes = SecurityDAO.getDomainScopes(ctx);
				DATOSAGENCIATRANSPORTETYPE at = albaran.getDATOSHOJARUTA().getDATOSAGENCIATRANSPORTE();
				carrier = new Carrier();
				carrier.setDomain(new Domain().setId(ctx.getDomainId()));
				carrier.setScope(new Scope().setId(scopes.get(0).getId()));
				carrier.setName(at.getDATOSREGISTRO().getNOMBRE());
				carrier.setAlias(at.getDATOSREGISTRO().getALIAS());
				carrier.setDocument(at.getDATOSREGISTRO().getDATOSDOCUMENTO().getDOCUMENTO());
				carrier.setDocumentCountry(Country.safeValueOf("ES"));
				carrier.setDocumentType(DocumentType.NIF);
				carrier.setNationality(Country.safeValueOf("ES"));
				carrier.setSecurityLevel(SecurityLevel.OFFICIAL);
				carrier.setLegalPerson( AonDocumentUtil.isEntity(at.getDATOSREGISTRO().getDATOSDOCUMENTO().getDOCUMENTO()));
				carrier.setStatus(CarrierStatus.ACTIVE);
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
						e.printStackTrace();
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
					carrierPacking.setCreationUser(ctx.getUser());
					carrierPacking.setCreationDate(new Date());
				} catch (Throwable th) {
					th.printStackTrace();
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
					th.printStackTrace();
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
	

	protected Carrier obtainCarrier(AONContext ctx,
			DATOSAGENCIATRANSPORTETYPE datosagenciatransportetype) {
		String document = datosagenciatransportetype.getDATOSREGISTRO()
				.getDATOSDOCUMENTO().getDOCUMENTO();
		Carrier carrier = CarrierDAO.getStream(
				ctx,
				f -> f.getDomainProperty().eq(ctx.getDomainId())
						.and(f.getDocumentProperty().eq(document))).findFirst().orElse(new Carrier());
		return carrier;
	}
	
	/**
	 * 
	 * DATA ATATCH
	 * 
	 */
	public void processDataAttach(Attach attach, List<Delivery> deliveryList) {
		if(deliveryList!=null && deliveryList.size()>0) {
			DataResponse response = new DataResponse();
			response.setCode("");
			response.setDomain(getDomainId());
			response.setResponseDate(new Date());
			response.setSource(DataResponseSource.INGENET);
			response.setSourceId(attach.getId());
			response.setCreationUser(getUser());
			response.setCreationDate(new Date());
			int id = AON.insertDataResponse(getDomain(), getDomainId(), getUser(), response).getId();
			response.setId(id);
			
			String description = "";
			for(Delivery delivery: deliveryList) {
				description += delivery.getReferenceCode() + ", ";
				DataResponseDetail detail = new DataResponseDetail();
				detail.setDomain(getDomainId());
				detail.setDataResponse(response.getId());
				detail.setDataVariable(delivery.getId().toString());
				detail.setDataValue("PROCESSED");
				detail.setCreationUser(getUser());
				detail.setCreationDate(new Date());
				AON.insertDataResponseDetail(getDomain(), getDomainId(), getUser(), detail);
			}
			description = description.replaceAll("(.+),\\s*$", "$1");
			description = (attach.getDescription()!=null?attach.getDescription()+", ":"") + description;
			description = description.length()>63?description.substring(0, 63):description;
			
			attach.setSourceBatch(response.getId());
			attach.setDescription(description);
			attach.setType(DataAttachType.RESPONSE_OK.value());
		} else {
			attach.setType(DataAttachType.RESPONSE_ERROR.value());
		}
		AON.updateAttach(getDomain(), getDomainId(), getUser(), attach);
	}
	
	protected Customer obtainCustomer(AONContext ctx, DATOSCLIENTETYPE datoscliente) {
		String document = datoscliente.getDATOSREGISTRO().getDATOSDOCUMENTO().getDOCUMENTO();
		
		List<Customer> customerList = CustomerDAO.getList(ctx, f-> 
				f.getDomainProperty().eq(ctx.getDomainId())
				.and(f.getDocumentProperty().eq(document))
				.and(f.getStatusProperty().eq(RegistryStatus.ACTIVE.value())));
		
		List<Integer> ids = customerList.stream().map(Customer::getId)
				.map(i -> Integer.valueOf(i)).collect(Collectors.toList());
		List<RegistryNote> ediRNotes = AON.getRegistryNoteList(
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
				e.printStackTrace();
				customer = null;
			}
		}
		if (customer != null && customer.getId() != null) {
			return customer;
		}
		return customerList!=null&&customerList.size()>0?customerList.get(0):null;
	}
	
	protected int obtainDefaultVat(AONContext ctx) {
		ApplicationParameter ap = AON.getApplicationParameter(ctx.getDomainName(),
				ctx.getDomainId(),
				ctx.getUser(),
				AppParam.ACC_DEFAULT_VAT_PERCENT.name());
		if(ap!=null && ap.getValue()!=null && NumberUtils.isNumber(ap.getValue())) {
			return Integer.valueOf(ap.getValue());
		} else {
			return TaxDAO.getVatTaxes(ctx, ctx.getDomainId())
				.sorted( (o1, o2) -> o1.getId().compareTo(o2.getId()) )
				.findFirst()
				.map(t -> t.getId())
				.orElse(0);
//			Tax tax = AON.getTaxStream(ctx.getDomainName(),
//					ctx.getDomainId(),
//					ctx.getUser(),
//					f -> f.getDomainProperty().eq(ctx.getDomainId())
//					.and(f.getTaxTypeProperty().eq(TaxType.VAT.value()))
//					).sorted((o1, o2) -> o1.getId().compareTo(o2.getId()))
//					.findFirst().orElse(null);
//			return tax.getId();
		}
	}
	protected SimpleDateFormat getDateFormatter() {
		if(dateFormatter==null){
			dateFormatter = new SimpleDateFormat("yyyyMMdd");
		}
		return dateFormatter;
	}
		
}

