package com.esferalia.aon.ingenet.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.esferalia.aon.ingenet.api.consultaElaboraciones.ACCIONTYPE;
import com.esferalia.aon.ingenet.api.consultaElaboraciones.CONSULTAELABORACIONES;
import com.esferalia.aon.ingenet.api.consultaElaboraciones.ESTADOTYPE;
import com.esferalia.aon.ingenet.api.consultaElaboraciones.PARAMETROSBUSQUEDATYPE;
import com.esferalia.aon.ingenet.api.consultaElaboraciones.REFERENCIATYPE;
import com.esferalia.aon.ingenet.api.respuestaElaboraciones.CIFNIFTYPE;
import com.esferalia.aon.ingenet.api.respuestaElaboraciones.DATOSCENTROTRABAJOTYPE;
import com.esferalia.aon.ingenet.api.respuestaElaboraciones.DATOSCLIENTETYPE;
import com.esferalia.aon.ingenet.api.respuestaElaboraciones.DATOSDIRECCIONTYPE;
import com.esferalia.aon.ingenet.api.respuestaElaboraciones.DATOSPEDIDOORIGENTYPE;
import com.esferalia.aon.ingenet.api.respuestaElaboraciones.DATOSPRODUCTOTYPE;
import com.esferalia.aon.ingenet.api.respuestaElaboraciones.DATOSREGISTROTYPE;
import com.esferalia.aon.ingenet.api.respuestaElaboraciones.ERRORESTYPE;
import com.esferalia.aon.ingenet.api.respuestaElaboraciones.PAISTYPE;
import com.esferalia.aon.ingenet.api.respuestaElaboraciones.RESPUESTAELABORACIONES;
import com.esferalia.aon.ingenet.api.respuestaElaboraciones.RESPUESTAELABORACIONTYPE;
import com.esferalia.aon.ingenet.api.util.IngenetXmlValidator;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.ElaborationSource;
import com.esferalia.aon.occam.api.model.type.ElaborationStatus;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.impl.jooq.dao.ElaborationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.GeoZoneDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SalesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WorkplaceDAO;

public class IngenetElaborationServlet extends AbstractIngenetServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IngenetElaborationServlet.class.getName());
	
	private List<String> errorList;
	
	
	protected void processRequest(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws ServletException, IOException {
		
		errorList = new LinkedList<>();
		
		String _xml = httpRequest.getParameter(PARAM_VALUE);
		PARAMETROSBUSQUEDATYPE params = null;
		if(_xml!=null){
			CONSULTAELABORACIONES consulta = null;
			try {
				super.validateConsultaElaboracionesXmlPattern(new ByteArrayInputStream(_xml.getBytes()));
				consulta = (CONSULTAELABORACIONES) IngenetXmlValidator.extractValue(_xml, CONSULTAELABORACIONES.class);
				if(consulta!=null && consulta.getDATOSCONSULTAELABORACIONES()!=null
						&& consulta.getDATOSCONSULTAELABORACIONES().getPARAMETROSBUSQUEDA()!=null){
					params = consulta.getDATOSCONSULTAELABORACIONES().getPARAMETROSBUSQUEDA();
				}
			} catch (SAXException e) {
				errorList.add("El fichero no ha pasado el proceso de validacion");
				errorList.add(e.getMessage());
			} catch (Exception e) {
				errorList.add(e.getMessage());
			}
		}
		
		if(params==null) {
			params = new PARAMETROSBUSQUEDATYPE();
			params.setFECHA(getDateFormatter().format(new Date()));
		}
		if(params.getACCION()==null) {
			params.setACCION(ACCIONTYPE.RECUPERAR);
		}
		
		AONContext ctx = AONContext.getAONContext(getDomain(), getDomainId(), getUser());
		Date date = null;
		if(params.getFECHA()!=null){
			try {
				date = getDateFormatter().parse(params.getFECHA());
			} catch (ParseException e) {
				LOGGER.error("Cannot parse date value. Reason: "+ e.getMessage());
			}
		}
		List<ElaborationStatus> statusList = new LinkedList<>();
		if(params.getESTADO()!=null){
			if(params.getESTADO().contains(ESTADOTYPE.PENDIENTE)){
				statusList.add(ElaborationStatus.PENDING);
			}
			if(params.getESTADO().contains(ESTADOTYPE.PROCESANDO)){
				statusList.add(ElaborationStatus.IN_PROGRESS);
			}
			if(params.getESTADO().contains(ESTADOTYPE.FINALIZADO)){
				statusList.add(ElaborationStatus.CLOSED);
			}
			if(params.getESTADO().contains(ESTADOTYPE.FALLIDO)){
				statusList.add(ElaborationStatus.FAIL);
			}
			if(params.getESTADO().contains(ESTADOTYPE.REABIERTO)){
				statusList.add(ElaborationStatus.REOPEN);
			}
		}
		
		List<Elaboration> elaborationList = null;
		try {
			if(ACCIONTYPE.RECUPERAR==params.getACCION()) {
				elaborationList = getElaborationList(ctx, date, statusList);
				flushElaborations(httpResponse, ctx, elaborationList);
				elaborationList.forEach(elaboration -> {
					elaboration.setStatus(ElaborationStatus.IN_PROGRESS.value());
					ElaborationDAO.updateElaboration(ctx, elaboration);
				});
				
				// TODO 
//				String subject = "[AON] Recuperación automática de elaboraciones";
//				String content = fillResponseMessage(elaborationList);
//				sendEmail(subject, content, "recuperar", _xml, RECIPIENTS_TO_LOG);
				saveToDisk("elaboration", "elaboration-request", _xml!=null?_xml:"");
			} else if(ACCIONTYPE.CANCELAR==params.getACCION()) {
				if(params.getELABORACIONES()!=null 
						&& params.getELABORACIONES().getREFERENCIAS()!=null 
						&& params.getELABORACIONES().getREFERENCIAS().size()>0){
					elaborationList = new ArrayList<>();
					reopenElaborations(ctx, elaborationList, params.getELABORACIONES().getREFERENCIAS());
					elaborationList.forEach(elaboration -> {
						ElaborationDAO.updateElaboration(ctx, elaboration);
					});
					httpResponse.setStatus(HttpServletResponse.SC_OK);					
				}
				
				String subject = "[AON] Cancelación automática de elaboraciones";
				String content = fillCancellationMessage(params.getELABORACIONES().getREFERENCIAS());
				sendEmail(subject, content, "cancelar", _xml, RECIPIENTS_TO_LOG);
				saveToDisk("elaboration", "elaboration-cancellation", _xml!=null?_xml:"");
			} else {
				errorList.add("No se ha indicado la accion a realizar");
			}
		} catch (Exception e) {
			LOGGER.error(e.toString());
			errorList.add(e.toString());
		}
		
		if(errorList!=null && errorList.size()>0){
			errorList.add(0, "Se han producido errores al procesar el fichero");
			flushErrors(httpResponse, errorList);
		}
		
	}

	private void reopenElaborations(AONContext ctx,
			List<Elaboration> elaborationList, List<REFERENCIATYPE> referencias) {
		referencias.forEach(ref -> {
			String series = ref.getSERIE();
			Integer number = Integer.parseInt(ref.getNUMERO());
			Elaboration elaboration = ElaborationDAO.getElaboration(ctx,
					series, number);
			if(elaboration.getStatus()==ElaborationStatus.IN_PROGRESS.value()){
				elaboration.setStatus(ElaborationStatus.REOPEN.value());
				elaboration.setComments(StringUtils.mid(ref.getOBSERVACIONES(), 0, 128));
				elaborationList.add(elaboration);
			}
		});
	}
	
	private String fillResponseMessage(List<Elaboration> elaborationList) {
		StringBuffer bf = new StringBuffer("<h1>Recuperación de elaboraciones.</h1>");
		bf.append("<ul>");
		elaborationList.forEach(elab -> {
			bf.append("<li>Elaboración " + elab.getSeries() + "/"
					+ elab.getNumber() + " del " + elab.getDate() + "</li>");
		});
		bf.append("</ul>");
		return bf.toString();
	}
	
	private String fillCancellationMessage(List<REFERENCIATYPE> list) {
		StringBuffer bf = new StringBuffer("<h1>Cancelación de elaboraciones.</h1>");
		bf.append("<ul>");
		list.forEach(elab -> {
			bf.append("<li>Elaboración " + elab.getSERIE() + "/"
					+ elab.getNUMERO() + "</li>");
			bf.append("<li>" + elab.getOBSERVACIONES() + "</li>");
		});
		bf.append("</ul>");
		return bf.toString();
	}
	
	private String fillErrorMessage(ERRORESTYPE errorestype, List<String> errorList) {
		StringBuffer bf = new StringBuffer("<h1>Envío de elaboraciones.</h1>");
		bf.append("<ul>");
		errorList.forEach(error -> {
			if(error!=null)
				bf.append("<li>"+error+"</li>");
		});
		bf.append("</ul>");
		bf.append("<ul>");
		errorestype.getERRORES().forEach(error -> {
			if(error!=null)
				bf.append("<li>"+error+"</li>");
		});
		bf.append("</ul>");
		return bf.toString();
	}

	private void flushErrors(HttpServletResponse httpResponse,
			List<String> errorList) throws IOException {
		RESPUESTAELABORACIONES respuesta = new RESPUESTAELABORACIONES();
		respuesta.setERRORES(new ERRORESTYPE());
		errorList.forEach(error -> {
			respuesta.getERRORES().getERRORES().add(error);
		});
		String xml = IngenetXmlValidator.convertToXml(respuesta, RESPUESTAELABORACIONES.class);
		
		String subject = "[AON] Envío automático de elaboraciones";
		String content = fillErrorMessage(respuesta.getERRORES(), errorList);
		sendEmail(subject, content, "elaboraciones", xml, RECIPIENTS_TO_FAILURES);
		
		httpResponse.setContentType("application/xml");
		httpResponse.setContentLength(xml.length());
		
		PrintWriter out = httpResponse.getWriter();
		out.print(xml);
		out.flush();
	}
	
	private void flushElaborations(HttpServletResponse httpResponse,
			AONContext ctx, List<Elaboration> pendingList) throws IOException {
		RESPUESTAELABORACIONES elaboraciones = fillElaborationData(ctx, pendingList);
		String xml = IngenetXmlValidator.convertToXml(elaboraciones, RESPUESTAELABORACIONES.class);
		
		httpResponse.setContentType("application/xml");
		httpResponse.setContentLength(xml.length());
		PrintWriter out = httpResponse.getWriter();
		out.print(xml);
		out.flush();
	}
	
	private RESPUESTAELABORACIONES fillElaborationData(AONContext ctx, List<Elaboration> pendingList) {
		RESPUESTAELABORACIONES elaboraciones = new RESPUESTAELABORACIONES();
		pendingList.forEach(elaboration -> {
			Item item = AON.getItem(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(),
					elaboration.getItem().getId());
			Product product = ProductDAO.getProduct(ctx, item.getProduct().getId());
			SalesDetail salesDetail = obtainSalesDetail(ctx, elaboration);
			Customer customer = null;
			if(salesDetail!=null && salesDetail.getId()!=null && salesDetail.getSales()!=null){
				customer = obtainCustomer(ctx, salesDetail.getSales().getId());
			}
			RESPUESTAELABORACIONTYPE elaboracion = new RESPUESTAELABORACIONTYPE();
			elaboracion.setSERIE(elaboration.getSeries());
			elaboracion.setNUMERO(String.valueOf(elaboration.getNumber()));
			elaboracion.setFECHAEMISION(getDateFormatter().format(elaboration.getDate()));
			elaboracion.setCOMENTARIOS(elaboration.getComments());
			if(salesDetail!=null && salesDetail.getId()!=null && salesDetail.getSales()!=null){
				Sales sales = obtainSales(ctx, salesDetail.getSales().getId());
				elaboracion.setDATOSPEDIDOORIGEN(new DATOSPEDIDOORIGENTYPE());
				elaboracion.getDATOSPEDIDOORIGEN().setSERIE(sales.getSeries());
				elaboracion.getDATOSPEDIDOORIGEN().setNUMERO(String.valueOf(sales.getNumber()));
				elaboracion.getDATOSPEDIDOORIGEN().setREFERENCIACOMPRA(sales.getPurchaseReference());
				if(customer!=null && customer.getId()!=null){
					elaboracion.getDATOSPEDIDOORIGEN().setDATOSCLIENTE(obtainDATOSCLIENTE(ctx, salesDetail, customer));
				}
				elaboracion.getDATOSPEDIDOORIGEN().setDATOSDIRECCIONENTREGA(obtainDATOSDIRECCIONENTREGA(ctx, sales));
				elaboracion.setDATOSCENTROTRABAJO(obtainDATOSCENTROTRABAJO(ctx, sales));
			}
			elaboracion.setDATOSPRODUCTO(new DATOSPRODUCTOTYPE());
			elaboracion.getDATOSPRODUCTO().setCODIGO(product.getCode());
			elaboracion.getDATOSPRODUCTO().setNOMBRE(product.getName());
			elaboracion.getDATOSPRODUCTO().setDESCRIPCION(item.getDescription());
			elaboracion.getDATOSPRODUCTO().setDETALLE(item.getDetail());
			elaboracion.getDATOSPRODUCTO().setDETALLE2(item.getDetail2());
			elaboracion.getDATOSPRODUCTO().setDETALLE3(item.getDetail3());
			elaboracion.getDATOSPRODUCTO().setFECHASERIE(null);
			elaboracion.getDATOSPRODUCTO().setNUMEROSERIE(null);
			elaboracion.getDATOSPRODUCTO().setCODIGOBARRAS(item.getBarcode());
			elaboracion.getDATOSPRODUCTO().setPRECIO(String.format(Locale.US, "%.3f%n", item.getPrice()));
			if(customer!=null && customer.getId()!=null){
				elaboracion.getDATOSPRODUCTO().setREFERENCIACLIENTE(obtainCustomerProductCode(ctx, elaboration.getItem(), customer));
			}
			elaboracion.setCANTIDAD(String.format(Locale.US, "%.3f%n", elaboration.getQuantity()));
			elaboracion.setUNIDADMEDIDA(elaboration.getItem().getStockUnitTag().getName());
			ElaborationStatus elaborationStatus = ElaborationStatus.values()[elaboration.getStatus()];
			if(elaborationStatus==ElaborationStatus.PENDING){
				elaboracion.setESTADO(com.esferalia.aon.ingenet.api.respuestaElaboraciones.ESTADOTYPE.PENDIENTE);
			} else if(elaborationStatus==ElaborationStatus.IN_PROGRESS){
				elaboracion.setESTADO(com.esferalia.aon.ingenet.api.respuestaElaboraciones.ESTADOTYPE.PROCESANDO);
			}
			elaboracion.setFECHACONSULTA(getDateFormatter().format(elaboration.getModificationDate()));
			elaboracion.setHORACONSULTA(getTimeFormatter().format(elaboration.getModificationDate()));
			elaboraciones.getDATOSRESPUESTAELABORACIONES().add(elaboracion);
		});
		elaboraciones.setTOTAL(String.valueOf(pendingList.size()));
		return elaboraciones;
	}

	private String obtainCustomerProductCode(AONContext ctx, Item item, Customer customer) {
		if(item!=null && item.getId()!=null
			&& customer!=null && customer.getId()!=null){
			return ElaborationDAO.getCustomerItemCode(ctx, item.getId(), customer.getId());
		}
		return null;
	}
	
	private DATOSCENTROTRABAJOTYPE obtainDATOSCENTROTRABAJO(AONContext ctx,
			Sales sales) {
		Workplace workplace = WorkplaceDAO.getWorkplace(ctx, p -> p.getIdProperty().eq(sales.getWorkplace()));
		RAddress address = RegistryDAO
				.getRAddressStream(ctx,
						p -> p.getIdProperty().eq(workplace.getAddress()))
				.findFirst().orElse(new RAddress());

		DATOSCENTROTRABAJOTYPE datos = new DATOSCENTROTRABAJOTYPE();
		datos.setDESCRIPCION(workplace.getDescription());
		datos.setDATOSDIRECCION(new DATOSDIRECCIONTYPE());
		datos.getDATOSDIRECCION().setDIRECCION(address.getAddress());
		datos.getDATOSDIRECCION().setDIRECCION2(address.getAddress2());
		datos.getDATOSDIRECCION().setDIRECCION3(address.getAddress3());
		datos.getDATOSDIRECCION().setCIUDAD(address.getCity());
		datos.getDATOSDIRECCION().setCODIGOPOSTAL(address.getZip());
		datos.getDATOSDIRECCION().setPROVINCIA(null);
		return datos;
	}

	private DATOSDIRECCIONTYPE obtainDATOSDIRECCIONENTREGA(AONContext ctx,
			Sales sales) {
		DATOSDIRECCIONTYPE datos = new DATOSDIRECCIONTYPE();
		if (sales.getShippingAlternativeAddress() != null
				&& sales.getShippingAlternativeAddress2() != null
				&& sales.getShippingAlternativeCity() != null
				&& sales.getShippingAlternativeZip() != null) {
			datos.setDIRECCION(sales.getShippingAlternativeAddress());
			datos.setDIRECCION2(sales.getShippingAlternativeAddress2());
			datos.setDIRECCION3(null);
			datos.setCIUDAD(sales.getShippingAlternativeCity());
			datos.setCODIGOPOSTAL(sales.getShippingAlternativeZip());
			datos.setPROVINCIA(null);
		} else {
			RAddress address = obtainAddress(ctx, sales.getShippingAddress());
			GeoZone gz = obtainGeozone(ctx, address.getGeozone());
			datos.setDIRECCION(address.getAddress());
			datos.setDIRECCION2(address.getAddress2());
			datos.setDIRECCION3(address.getAddress3());
			datos.setCIUDAD(address.getCity());
			datos.setCODIGOPOSTAL(address.getZip());
			datos.setPROVINCIA(gz!=null?gz.getName():null);
		}
		return datos;
	}

	private DATOSCLIENTETYPE obtainDATOSCLIENTE(AONContext ctx, SalesDetail salesDetail, Customer customer) {
		Registry registry = obtainRegistry(ctx, customer.getId());
		
		DATOSCLIENTETYPE datos = new DATOSCLIENTETYPE();
		datos.setCODIGO(String.valueOf(customer.getId()));
		datos.setALBARANVALORADO(new Byte("1").equals(customer.getDeliveryValuated())?"S":"N");
		datos.setDATOSREGISTRO(new DATOSREGISTROTYPE());
		datos.getDATOSREGISTRO().setDATOSDOCUMENTO(new CIFNIFTYPE());
		datos.getDATOSREGISTRO().getDATOSDOCUMENTO().setPAISDOCUMENTO(new PAISTYPE());
		if(registry!=null){
			datos.getDATOSREGISTRO().getDATOSDOCUMENTO().getPAISDOCUMENTO().setCODIGO(String.valueOf(registry.getDocumentCountry().getIsoCode()));
			datos.getDATOSREGISTRO().getDATOSDOCUMENTO().getPAISDOCUMENTO().setDESCRIPCION(registry.getDocumentCountry().getName());
			datos.getDATOSREGISTRO().getDATOSDOCUMENTO().setTIPODOCUMENTO(registry.getDocumentType().name());
			datos.getDATOSREGISTRO().getDATOSDOCUMENTO().setDOCUMENTO(registry.getDocument());
			datos.getDATOSREGISTRO().setNOMBRE(registry.getName());
			datos.getDATOSREGISTRO().setALIAS(registry.getAlias());
			datos.getDATOSREGISTRO().setNACIONALIDAD(new PAISTYPE());
			datos.getDATOSREGISTRO().getNACIONALIDAD().setCODIGO(String.valueOf(registry.getNationality().getIsoCode()));
			datos.getDATOSREGISTRO().getNACIONALIDAD().setDESCRIPCION(registry.getNationality().getName());
		}
		datos.getDATOSREGISTRO().setTELEFONOFIJO(obtainCustomerPhone(ctx, customer));
		datos.getDATOSREGISTRO().setTELEFONOMOVIL(obtainCustomerCellular(ctx, customer));
		return datos;
	}
	
	
	private Registry obtainRegistry(AONContext ctx, Integer id){
		if(id!=null){
			return RegistryDAO.getRegistry(ctx, id);
		}
		return null;
	}
	
	private Customer obtainCustomer(AONContext ctx, Integer salesId){
		if(salesId!=null){
			Sales sales = obtainSales(ctx, salesId);
			return AON.getCustomer(ctx.getDomainName(), ctx.getDomainId(),
					ctx.getUser(), sales.getCustomer().getId());
		}
		return null;
	}
	
	private String obtainCustomerPhone(AONContext ctx, Customer customer) {
		if (customer != null && customer.getId() != null) {
			return RegistryDAO
					.getRMediaStream(
							ctx,
							filter -> filter
									.getRegistryProperty()
									.eq(customer.getId())
									.and(filter.getMediaProperty().eq(
											MediaType.FIXED_PHONE.value())))
					.findFirst().orElse(new RegistryMedia()).getValue();
		}
		return null;
	}

	private String obtainCustomerCellular(AONContext ctx, Customer customer) {
		if (customer != null && customer.getId() != null) {
			return RegistryDAO
					.getRMediaStream(
							ctx,
							filter -> filter
									.getRegistryProperty()
									.eq(customer.getId())
									.and(filter.getMediaProperty().eq(
											MediaType.CELLULAR.value())))
					.findFirst().orElse(new RegistryMedia()).getValue();
		}
		return null;
	}

	private Sales obtainSales(AONContext ctx, Integer salesId){
		if(salesId!=null){
			return SalesDAO.getSales(ctx, salesId);
		}
		return null;
	}

	private SalesDetail obtainSalesDetail(AONContext ctx, Elaboration elaboration){
		if(elaboration.getSource()!=null
				&& elaboration.getSourceId()!=null
				&& elaboration.getSource()==ElaborationSource.SALES.value()){
			return SalesDAO.getSalesDetail(ctx, elaboration.getSourceId());
		}
		return null;
	}
	
	private RAddress obtainAddress(AONContext ctx, Integer id) {
		if (id != null) {
			return RegistryDAO
					.getRAddressStream(ctx, f -> f.getIdProperty().eq(id))
					.findFirst().orElse(new RAddress());
		}
		return null;
	}
	
	private GeoZone obtainGeozone(AONContext ctx, Integer id) {
		if (id != null) {
			return GeoZoneDAO.get(ctx, id);
		}
		return null;
	}

	private List<Elaboration> getElaborationList(AONContext ctx, Date date,
			List<ElaborationStatus> statusList) {
		List<Elaboration> elaborationList = ElaborationDAO.getElaborationList(
				ctx,
				p -> {
					Byte[] statuses = { null, null, null, null, null };
					if (statusList != null && statusList.size() > 0) {
						for (int i = 0; i < statusList.size(); i++) {
							statuses[i] = statusList.get(i).value();
						}
					} else {
						statuses[0] = ElaborationStatus.PENDING.value();
						statuses[1] = ElaborationStatus.IN_PROGRESS.value();
						statuses[2] = ElaborationStatus.FAIL.value();
						statuses[3] = ElaborationStatus.REOPEN.value();
					}
					java.sql.Timestamp start = null;
					java.sql.Timestamp end = null;
					if (date != null) {
						start = new java.sql.Timestamp(DateUtils
								.setSeconds(
										DateUtils.setMinutes(
												DateUtils.setHours(date, 0), 0), 0)
												.getTime());
						end = new java.sql.Timestamp(DateUtils
								.setSeconds(
										DateUtils.setMinutes(
												DateUtils.setHours(date, 23), 59),
												59).getTime());
						return p.getStatusProperty()
								.in(statuses)
								.and(date != null ? p.getDateProperty().between(
										start, end) : p.getDateProperty()
										.isNotNull());
					} else {
						return p.getStatusProperty()
								.in(statuses);
					}
				});
		return elaborationList;
	}
	

}
