package com.esferalia.aon.ingenet.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.customer.IEdiSupport;
import com.esferalia.aon.ingenet.api.albaranes.ALBARANES;
import com.esferalia.aon.ingenet.api.albaranes.ALBARANTYPE;
import com.esferalia.aon.ingenet.api.albaranes.DATOSLINEAALBARANTYPE;
import com.esferalia.aon.ingenet.api.albaranes.ERRORESTYPE;
import com.esferalia.aon.ingenet.api.util.IngenetXmlValidator;
import com.esferalia.aon.ingenet.servlet.delivery.AbstractDeliveryCreator;
import com.esferalia.aon.ingenet.servlet.delivery.DeliveryCreator;
import com.esferalia.aon.ingenet.servlet.delivery.DeliveryCreatorSales;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.registry.NoteType;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.seres.ftp.seres.FtpDeliveryUploadOccamHandler;

@WebServlet(name = "IngenetDeliveryServlet", urlPatterns = { "/ingenet/delivery/*", "/ingenet/delivery/dev/*",
		"/ingenet/delivery/deprecated/*" })
public class IngenetDeliveryServlet extends AbstractIngenetServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IngenetDeliveryServlet.class.getName());
	
	
	private boolean isDev(HttpServletRequest httpRequest) {
		return httpRequest.getRequestURI().contains("/dev");
	}
		
	protected void processRequest(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws ServletException, IOException {
		
		String _xml = httpRequest.getParameter(PARAM_VALUE);
		
		String subject = "Recepcion automatica de albaranes";
		String content = "Se ha detectado una nueva comunicación para albaranes";
		log("delivery", IngenetLogLevel.DEBUG, subject, content, "delivery", _xml, RECIPIENTS_TO_LOG);
		
		Attach attach = new Attach();
		createDataAttach(attach, _xml);
		
		
//		DeliveryCreator creator = new DeliveryCreator(getDomain(), getDomainId(), getUser());
		
		AbstractDeliveryCreator creator = null;
		if(isDev(httpRequest)) {
			creator = new DeliveryCreatorSales(getDomain(), getDomainId(), getUser());
		} else {
			creator = new DeliveryCreator(getDomain(), getDomainId(), getUser());
		}
		
		
		try {
			creator.validateAlbaranesXmlPattern(_xml);
		} catch (Exception e) {
			creator.getErrorList().add("Los datos no han pasado el proceso de validacion");
			creator.getErrorList().add(e.getMessage());
			content = e.getMessage();
			log(IngenetLogLevel.ERROR, subject, content, "invalid_pattern", _xml, RECIPIENTS_TO_FAILURES);
		}
		
		if(creator.getErrorList()==null || creator.getErrorList().isEmpty() ) {
			creator.create(_xml);
		}
		
		
		
		List<Delivery> deliveryList = creator.getDeliveryList();
		ALBARANES albaranes = creator.getAlbaranes();
		List<String> errorList = creator.getErrorList();
		List<String> warningList = creator.getWarningList();
		List<ALBARANTYPE> invalidDeliveries = null;
		if(albaranes==null){
			albaranes = new ALBARANES();
		} else if(albaranes.getDATOSALBARANES()!=null
				&& !albaranes.getDATOSALBARANES().isEmpty()) {
			invalidDeliveries = albaranes
					.getDATOSALBARANES()
					.stream()
					.filter(o -> o.getERRORES() != null
							&& o.getERRORES().getERRORES() != null
							&& !o.getERRORES().getERRORES().isEmpty())
					.collect(Collectors.toList());
			if(invalidDeliveries!=null && invalidDeliveries.size()>0){				
				albaranes.getDATOSALBARANES().clear();
				albaranes.getDATOSALBARANES().addAll(invalidDeliveries);
				errorList.add("Albaranes con errores: " + invalidDeliveries.size());
			}
		}
		
		if (errorList != null && errorList.size() > 0) {
			errorList.add(0, "Se han producido errores al procesar el fichero");
			errorList.forEach(LOGGER::error);
			warningList.forEach(LOGGER::error);
			List<String> list = new LinkedList<>();
			list.addAll(errorList);
			list.addAll(warningList);
			flushErrors(httpResponse, albaranes, list);
		} else {
			httpResponse.setStatus(HttpServletResponse.SC_OK);
			boolean isTest = "S".equals(albaranes.getPRUEBA());
			if (!isTest) {
				AONContext ctx = AONContext.getAONContext(getDomain(), getDomainId(), getUser());
				try {
					if(deliveryList!=null && deliveryList.size()>0){
						subject = "Recepción automática de albaranes";
						content = fillSuccessMessage(ctx, albaranes.getDATOSALBARANES(), deliveryList, warningList, creator);
						log(IngenetLogLevel.INFO, subject, content, null, null, RECIPIENTS_TO_SUCCESS);
						
						creator.processDataAttach(attach, deliveryList);
						
						// if autoCommit enabled, commit automatically the deliveries to SERES
						if(!this.isDevEnabled()){
							FtpDeliveryUploadOccamHandler handler = new FtpDeliveryUploadOccamHandler(getDomain(), getDomainId(), getUser());
							for(Delivery d: deliveryList){
								// commit the delivery EDI file
								RegistryNote rNote = searchCustomerNote(ctx, d.getCustomer(), "", IEdiSupport.SERES_AUTO_COMMIT_DELIVERY);
								boolean autoSendDelivery = rNote!=null && new Boolean(rNote.getComments());
								if(autoSendDelivery){
									boolean success = false;
									try {
//										handler.transferEdiFtp(d);
										handler.onEdiFtpTransfer(d);
										success = true;
									} catch (Throwable th) {
										success = false;
										subject = "Envio de albaranes a Seresnet";
										content = "El albaran no se ha podido enviar automaticamente";
										content += "<br/>MOTIVO: " + th.getMessage();
										log(IngenetLogLevel.ERROR, subject, content, null, null, RECIPIENTS_TO_FAILURES);
									}
									if(success) {
										// TODO mark delivery as sended
//										setDeliverySended(attach, d, success);
									}
//										updateDataAttach(attach, d, success);
								}
							}
						}
						deliveryList.clear();
					}
				} finally {
					if (ctx != null)
						ctx.close();
				}
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
		
		String subject = "Fallos en la recepción de albaranes";
		String content = fillErrorMessage(deliveryList.getDATOSALBARANES(), errorList);
		log(IngenetLogLevel.ERROR, subject, content, "albaranes", xml, RECIPIENTS_TO_FAILURES);
		
		httpResponse.setContentType("application/xml");
		httpResponse.setContentLength(xml.length());
		PrintWriter out = httpResponse.getWriter();
		out.print(xml);
		out.flush();
	}
	
	private String fillSuccessMessage(AONContext ctx, List<ALBARANTYPE> albaranes, List<Delivery> deliveryList, List<String> warningList, AbstractDeliveryCreator creator) {
		StringBuffer bf = new StringBuffer("<h1>Recepción de albaranes.</h1>");
		bf.append("<ul>");
		albaranes.forEach(alb -> {
			bf.append("<li>Nuevo albarán ");
			bf.append("<b>").append(alb.getSERIE()).append("/").append(alb.getNUMERO())
					.append("</b> del ").append(alb.getFECHAEMISION());
			try {
				if(!"".equals(alb.getDATOSCLIENTE().getDATOSREGISTRO().getNOMBRE())) {
					Integer customerId = deliveryList.stream().filter( d -> d.getSeries().startsWith(alb.getSERIE()) && (d.getNumber()==Integer.parseInt(alb.getNUMERO())))
						.findFirst().orElse(new Delivery()) .getCustomer();
					bf.append(", a nombre de ");
					if(customerId!=null){
						Registry registry = AON.getRegistry(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), customerId);
						bf.append("<b>").append(registry.getName()).append("</b>");
					}
					bf.append(" (")
						.append(alb.getDATOSCLIENTE().getDATOSREGISTRO().getDATOSDOCUMENTO().getDOCUMENTO())
						.append(")");
				}
			} catch (Exception e) {
				System.err.println("Error on customer name: " + e.getMessage());
			}
			bf.append("<ul>");
			alb.getLINEASALBARAN().getDATOSLINEAALBARAN().forEach(lin -> {
				creator.fillSuccessMessage(bf, lin);
			});
			bf.append("</ul>");
			bf.append("</li>");
		});
		bf.append("</ul>");
		if (warningList != null && warningList.size() > 0) {
			bf.append("<h1>Avisos:</h1>");
			bf.append("<ul>");
			warningList.forEach(msg -> {
				bf.append("<li>").append(msg).append("</li>");
			});
			bf.append("</ul>");
		}
		return bf.toString();
	}
	

	private String fillErrorMessage(List<ALBARANTYPE> deliveryList, List<String> errorList) {
		StringBuffer bf = new StringBuffer("<h1>Recepción de albaranes.</h1>");
		bf.append("<ul>");
		errorList.forEach(error -> {
			if(error!=null)
				bf.append("<li>").append(error).append("</li>");
		});
		bf.append("</ul>");
		deliveryList.forEach(alb -> {
			bf.append("<h2>Albarán ").append(alb.getSERIE()).append("/").append(alb.getNUMERO()).append("</h2>");
			bf.append("<ul>");
			alb.getERRORES().getERRORES().forEach(error -> {
				if(error!=null)
					bf.append("<li>").append(error).append("</li>");
			});
			bf.append("</ul>");
		});
		return bf.toString();
	}
	

	
	private RegistryNote searchCustomerNote(AONContext ctx, Integer customerId,
			String customerCode, String key) {
		List<RegistryNote> rNotes = AON.getRNoteList(
				ctx.getDomainName(),
				ctx.getDomainId(),
				ctx.getUser(),
				f -> f.getNoteTypeProperty().eq(NoteType.FACTURAE.value())
						.and(f.getRegistryProperty().eq(customerId))
						.and(f.getDescriptionProperty().eq(key))
						);
		return rNotes!=null && rNotes.size()>0?rNotes.get(0):null;
	}
	
	
	private void createDataAttach(Attach attach, String data) {
		attach.setDomain(new Domain().setId(getDomainId()));
		attach.setSourceType(DataAttachSource.INGENET.value());
		attach.setAttachType(AttachType.DATA);
		attach.setType(DataAttachType.REQUEST.value());
		attach.setData(data.getBytes());
		attach.setMimeType(MimeType.TXT);
		attach.setCreationUser(getUser());
		attach.setCreationDate(new Date());
		int id = AON.insertAttach(getDomain(), getDomainId(), getUser(), attach);
		attach.setId(id);
	}
	
	private void updateDataAttach(Attach attach, Integer deliveryId) {
		attach.setSourceBatch(deliveryId);
		attach.setModificationUser(getUser());
		attach.setModificationDate(new Date());
		AON.updateAttach(getDomain(), getDomainId(), getUser(), attach);
	}

	private void updateDataAttach(Attach attach, Delivery delivery, boolean success) {
		attach.setSourceBatch(delivery.getId());
		attach.setType((success ? DataAttachType.RESPONSE_OK : DataAttachType.RESPONSE_ERROR).value());
		AON.updateAttach(getDomain(), getDomainId(), getUser(), attach);
	}
	
	private void setDeliverySended(Attach attach, Delivery delivery, boolean success) {
		if (attach.getSourceBatch() != null) {
			DataResponseDetail detail = AON.getDataResponseDetail(getDomain(), getDomainId(), getUser(),
					f -> f.getDomainProperty().eq(getDomainId())
							.and(f.getDataResponseProperty().eq(attach.getSourceBatch()))
							.and(f.getDataVariableProperty().eq(delivery.getId().toString())))
					.orElse(null);
			if (detail != null) {
				detail.setDataValue("SENDED");
				detail.setModificationUser(getUser());
				detail.setModificationDate(new Date());
			}
		}
	}
		
}

