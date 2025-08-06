package net.aonsolutions.aon.verifactu;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Document;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatch;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatchDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.finance.InvoiceData;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.finance.VerifactuConfiguration;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.DataRequestType;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AttachmentDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataRequestDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceBatchDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceBatchDetailDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceDataDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceInfoDAO;
import com.esferalia.aon.watson.util.AonChronometer;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.respuestasuministro.EstadoRegistroType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.respuestasuministro.RespuestaRegFactuSistemaFacturacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAltaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.TipoOperacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegFactuSistemaFacturacion;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegistroFacturaType;
import net.aonsolutions.aon.verifactu.exceptions.VerifactuError;
import net.aonsolutions.aon.verifactu.exceptions.VerifactuException;

public class VERIFACTU {

	private VERIFACTU() {
		
	}

	// **************************************************************
	// ************************************************ [ACCEPT] ****
	// **************************************************************
	/**
	 * @deprecated This method is deprecated, use the one with AONContext parameter
	 * 			La llamada debe hacerse con una trasacción abierta.
	 */
	@Deprecated
	public static void accept(
			Occam occam,
			VerifactuConfiguration verifactuConfiguration, 
			Company company, 
			List<Invoice> invoices, 
			VerifactuBlockchain blockchain) {
			
		VerifactuContext vc = new VerifactuContext()
			.setConfig(verifactuConfiguration)
			.setCompany(company)
			.setInvoices(invoices)
			.setBlockchain(blockchain)
			.setUser(occam.getUser())
			.setOperation(InvoiceCommunicationOperation.REGISTER);
		try (CloseableAONContext ctx = AONContext.getAONContext(vc.getOccam())) {
			ctx.getDslContext().transaction(configuration -> accept(ctx, vc));
		}
	}
	
	public static void accept(
			AONContext ctx,
			VerifactuConfiguration verifactuConfiguration, 
			Company company, 
			List<Invoice> invoices, 
			VerifactuBlockchain blockchain,
			String user) throws VerifactuException {
			
		VerifactuContext vc = new VerifactuContext()
			.setConfig(verifactuConfiguration)
			.setCompany(company)
			.setInvoices(invoices)
			.setBlockchain(blockchain)
			.setUser(user)
			.setOperation(InvoiceCommunicationOperation.REGISTER);
		
		accept(ctx, vc);
	}
	
	private static void accept(AONContext ctx, VerifactuContext vc) throws VerifactuException {
		check(vc);
		RegFactuSistemaFacturacion request = Invoice2Verifactu.build(vc);
		vc.setRequest( request );
		Document document = VerifactuXMLUtils.toDocument(request, RegFactuSistemaFacturacion.class);
		vc.setRequestBytes(VerifactuXMLUtils.toBytes(document));
		SOAPMessage requestMessage = VerifactuXMLUtils.soapMarshal(document);
		vc.setResponse( VerifactuXMLUtils.post(vc.getConfig().getCertificate(), VerifactuUri.getUrlEmision(true),requestMessage));
		save( ctx, vc );
	}

	private static void check(VerifactuContext vc) throws VerifactuException {
		if (vc == null) {
			throw new VerifactuException(VerifactuError.AON_0001);
		}
		if (vc.getCompany() == null) {
			throw new VerifactuException(VerifactuError.AON_0002);
		}
		if (vc.getCompany().getDomain() == null
			|| vc.getCompany().getDomain().getId() == null
			|| AonStringUtils.isBlank( vc.getCompany().getDomain().getName())) {
			throw new VerifactuException(VerifactuError.AON_0003);
		}
		if (AonStringUtils.isBlank( vc.getUser())) {
			throw new VerifactuException(VerifactuError.AON_0004);
		}
		if (AonCollectionUtils.isEmpty(vc.getInvoices())) {
			throw new VerifactuException(VerifactuError.AON_0005);
		}
		if (vc.getConfig() == null) {
			throw new VerifactuException(VerifactuError.AON_0006);
		}
		if (vc.getConfig().getCertificate() == null) {
			throw new VerifactuException(VerifactuError.AON_0007);
		}
	}
	
	private static void save(AONContext ctx, VerifactuContext vc) throws VerifactuException {
		AonChronometer cr = new AonChronometer();
		cr.start();
        System.out.println("\tSTART TRACING SAVE:");
        System.out.println("\t\tTRACING SAVE: 1 - " + cr.getCurrentTime());

		// save DATA REQUEST
		DataRequest dataRequest = saveRequest(ctx, vc.getDomain(), vc.getRequestBytes());
		// save DATA RESPONSE
		DataResponse dataResponse = saveResponse(ctx, vc.getDomain(), dataRequest, vc.getResponse().getBytes());
		// save INVOICE DATA
		for ( RegistroFacturaType req : vc.getRequest().getRegistroFactura()) {
			System.out.println("\t\tTRACING SAVE: 2 - " + cr.getCurrentTime());
			saveInvoiceData(ctx, vc.getDomain(), req.getRegistroAlta() , cr);
			System.out.println("\t\tTRACING SAVE: 3 - " + cr.getCurrentTime());
		}
		
		saveVerifactuBlockchain(ctx, vc.getDomain(), vc.getBlockchain());
		System.out.println("\t\tTRACING SAVE: 4 - " + cr.getCurrentTime());
		// save INVOICE DATA
		InvoiceBatch invoiceBatch = saveInvoiceBatch(ctx, vc.getDomain(), dataResponse);
		System.out.println("\t\tTRACING SAVE: 5 - " + cr.getCurrentTime());
		if (vc.getResponse().isError()) {
			System.out.println("\t\tTRACING SAVE: 6- " + cr.getCurrentTime());
			vc.getInvoices().stream().forEach(inv -> {
				System.out.println("\t\tTRACING SAVE: 7 - " + cr.getCurrentTime());
				saveInvoiceInfo(ctx, vc.getDomain(), inv.getId(), InvoiceCommunicationStatus.WRONG);
				System.out.println("\t\tTRACING SAVE: 8 - " + cr.getCurrentTime());
				saveInvoiceBatchdetail(ctx, vc.getDomain(), invoiceBatch, inv.getId(), InvoiceCommunicationStatus.WRONG);					
				System.out.println("\t\tTRACING SAVE: 9 - " + cr.getCurrentTime());
			});
		} else {
			System.out.println("\t\tTRACING SAVE: 10 - " + cr.getCurrentTime());
			RespuestaRegFactuSistemaFacturacionType respuesta = vc.getResponse().getResponse();
			if (!respuesta.getRespuestaLinea().isEmpty()) {
				respuesta.getRespuestaLinea().stream().forEach(r -> {
					System.out.println("\t\tTRACING SAVE: 11 - " + cr.getCurrentTime());
					Integer invoiceId = AonNumberUtils.toInteger(r.getRefExterna());
					InvoiceCommunicationStatus status = getInvoiceCommunicationStatus(r.getEstadoRegistro());
					System.out.println("\t\tTRACING SAVE: 12 - " + cr.getCurrentTime());
					saveInvoiceInfo(ctx, vc.getDomain(), invoiceId, status); 
					System.out.println("\t\tTRACING SAVE: 13 - " + cr.getCurrentTime());
					saveInvoiceBatchdetail(ctx, vc.getDomain(), invoiceBatch, invoiceId, status);				
					System.out.println("\t\tTRACING SAVE: 14 - " + cr.getCurrentTime());
				});
			}
		}		
	}
	
	// **************************************************************
	// **************************************************************
	// **************************************************************
	public static void cancel(
			Occam occam, 
			VerifactuConfiguration verifactuConfiguration, 
			Company company, 
			List<Invoice> invoices, 
			VerifactuBlockchain blockchain) {
		
		VerifactuContext vc = new VerifactuContext()
			.setConfig(verifactuConfiguration)
			.setCompany(company)
			.setInvoices(invoices)
			.setBlockchain(blockchain)
			.setUser(occam.getUser())
			.setOperation(InvoiceCommunicationOperation.ANNULMENT);
		try (CloseableAONContext ctx = AONContext.getAONContext(vc.getOccam())) {
			ctx.getDslContext().transaction(configuration -> cancel(ctx, vc));
		}
	}
	
	public static void cancel(AONContext ctx, VerifactuContext vc) throws VerifactuException {
		check(vc);
		RegFactuSistemaFacturacion request = Invoice2Verifactu.build(vc);
		vc.setRequest( request );
		Document document = VerifactuXMLUtils.toDocument(request, RegFactuSistemaFacturacion.class);
		vc.setRequestBytes(VerifactuXMLUtils.toBytes(document));
		SOAPMessage requestMessage = VerifactuXMLUtils.soapMarshal(document);
		vc.setResponse( VerifactuXMLUtils.post(vc.getConfig().getCertificate(), VerifactuUri.getUrlEmision(true),requestMessage));
		saveCancel( ctx, vc );
//		check(vc);
//		vc.setRequest(Invoice2Verifactu.build(vc) );
//		SOAPMessage request = VerifactuXMLUtils.soapMarshal(vc.getRequest(),RegFactuSistemaFacturacion.class);
//		vc.setResponse( VerifactuXMLUtils.post(vc.getConfig().getCertificate(), VerifactuUri.getUrlEmision(true),request));
//		saveCancel( ctx, vc );
	}
	
	private static void saveCancel(AONContext ctx, VerifactuContext vc) throws VerifactuException {
		// byte[] requestData = VerifactuXMLUtils.marshal(vc.getRequest(), RegFactuSistemaFacturacion.class);
		byte[] requestData = vc.getRequestBytes();
		byte[] responseData = vc.getResponse().getBytes();
		// save DATA REQUEST
		DataRequest dataRequest = saveRequest(ctx, vc.getDomain(), requestData);
		// save DATA RESPONSE
		saveResponse(ctx, vc.getDomain(), dataRequest, responseData);
		
		saveVerifactuBlockchain(ctx, vc.getDomain(), vc.getBlockchain());	
		
		// DELETE ANNULLED INVOICES
		if (!vc.getResponse().isError()) {
			RespuestaRegFactuSistemaFacturacionType respuesta = vc.getResponse().getResponse();
			if (!respuesta.getRespuestaLinea().isEmpty()) {
				respuesta.getRespuestaLinea().stream().forEach(r -> {
					Integer invoiceId = AonNumberUtils.toInteger(r.getRefExterna());
					if(TipoOperacionType.ANULACION.equals(r.getOperacion().getTipoOperacion()) 
							&& EstadoRegistroType.CORRECTO.equals(r.getEstadoRegistro())) {
						InvoiceDAO.delete(ctx, invoiceId);
					}
				});
			}
		}
	}

	private static DataRequest saveRequest(AONContext ctx, Domain domain, byte[] request) {
		DataRequest dataRequest = new DataRequest()
			.setDomain(domain.getId())
			.setDate(new Date())
			.setBlackBox("")
			.setType(DataRequestType.VERIFACTU);
		dataRequest = DataRequestDAO.save(ctx, dataRequest);
		
		Attach attach = new Attach()
			.setDomain(domain)
			.setAttachType(AttachType.DATA)
			.setType(DataAttachType.REQUEST.value())
			.setSource(DataAttachSource.VERIFACTU.value())
			.setSourceId(dataRequest.getId())
			.setMimeType(MimeType.XML)
			.setData(request);
		AttachmentDAO.insertDataAttach(ctx, attach);
		return dataRequest;		
	}

	private static DataResponse saveResponse(AONContext ctx, Domain domain, DataRequest dataRequest, byte[] response) {
		DataResponse dataResponse = new DataResponse()
			.setDomain(dataRequest.getDomain())
			.setDataRequest(dataRequest.getId())
			.setCode("")
			.setSource(DataResponseSource.VERIFACTU);
		DataResponseDAO.insertDataResponse(ctx, dataResponse);
		
		Attach attach = new Attach()
			.setDomain(domain)
			.setAttachType(AttachType.DATA)
			.setType(DataAttachType.RESPONSE_OK.value())
			.setSource(DataAttachSource.VERIFACTU.value())
			.setSourceId(dataResponse.getId())
			.setMimeType(MimeType.XML)
			.setData(response);
		AttachmentDAO.insertDataAttach(ctx, attach);
		
		return dataResponse;		
	}

	private static void saveInvoiceData(AONContext ctx, Domain domain, RegistroFacturacionAltaType registroAlta, AonChronometer cr) throws VerifactuException {
		Integer invoiceId = AonNumberUtils.toInteger(registroAlta.getRefExterna());
		System.out.println("\t\tTRACING SAVE: 2.0 - " + cr.getCurrentTime());
		InvoiceDataDAO.save(ctx, new InvoiceData()
			.setDomain(domain.getId())
			.setInvoice(invoiceId)
			.setName(InvoiceData.VERIFACTU_HUELLA)
			.setValue(registroAlta.getHuella())
		);
		System.out.println("\t\tTRACING SAVE: 2.1 - " + cr.getCurrentTime());
		String qrUrl = getQrUrl(registroAlta);
		System.out.println("\t\tTRACING SAVE: 2.2 - " + cr.getCurrentTime());
		InvoiceDataDAO.save(ctx, new InvoiceData()
			.setDomain(domain.getId())
			.setInvoice(invoiceId)
			.setName(InvoiceData.VERIFACTU_QR)
			.setValue(qrUrl));
		System.out.println("\t\tTRACING SAVE: 2.2 - " + cr.getCurrentTime());
	}
	
	private static String getQrUrl(RegistroFacturacionAltaType alta) throws VerifactuException {
		return new StringBuilder(VerifactuUri.getUrlQr())
			.append("?")
			.append("nif=").append(encodeParam(alta.getIDFactura().getIDEmisorFactura()))
			.append("numserie=").append(encodeParam(alta.getIDFactura().getNumSerieFactura()))
			.append("fecha=").append(encodeParam(alta.getIDFactura().getFechaExpedicionFactura()))
			.append("importe=").append(encodeParam(alta.getImporteTotal()))
			.toString();
	}
	
	private static String encodeParam(String param) throws VerifactuException {
		try {
			return URLEncoder.encode(param, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new VerifactuException(VerifactuError.AON_0008, e );
		}
	}
	
	private static void saveVerifactuBlockchain(AONContext ctx, Domain domain, VerifactuBlockchain blockchain) {
		AppParamDAO.saveApplicationParameter(ctx, new ApplicationParameter()
			.setDomain(domain.getId())
			.setName(AppParam.VERIFACTU_BLOCKCHAIN_DOCUMENT.name())
			.setValue(blockchain.getDocument()));
		AppParamDAO.saveApplicationParameter(ctx, new ApplicationParameter()
			.setDomain(domain.getId())
			.setName(AppParam.VERIFACTU_BLOCKCHAIN_REFERENCE.name())
			.setValue(blockchain.getReference()));
		AppParamDAO.saveApplicationParameter(ctx, new ApplicationParameter()
			.setDomain(domain.getId())
			.setName(AppParam.VERIFACTU_BLOCKCHAIN_DATE.name())
			.setValue(blockchain.getDate()));
		AppParamDAO.saveApplicationParameter(ctx, new ApplicationParameter()
			.setDomain(domain.getId())
			.setName(AppParam.VERIFACTU_BLOCKCHAIN_HUELLA.name())
			.setValue(blockchain.getHuella()));
	}
	
	private static InvoiceBatch saveInvoiceBatch(AONContext ctx, Domain domain, DataResponse dataResponse) {
		InvoiceBatch invoiceBatch = new InvoiceBatch()
			.setDomain(domain.getId())
			.setType(InvoiceCommunicationType.VERIFACTU)
			.setDate(new Date())
			.setOperation(InvoiceCommunicationOperation.REGISTER)
			.setDataResponse(dataResponse.getId());
		return InvoiceBatchDAO.save(ctx, invoiceBatch);
	}
	
	private static InvoiceBatchDetail saveInvoiceBatchdetail(AONContext ctx, Domain domain, InvoiceBatch invoiceBatch, Integer invoice, InvoiceCommunicationStatus status) {
		InvoiceBatchDetail ibd = new InvoiceBatchDetail()
			.setDomain(invoiceBatch.getDomain())
			.setInvoiceBatch(invoiceBatch.getId())
			.setInvoice(invoice)
			.setStatus(status);
		return InvoiceBatchDetailDAO.save(ctx, ibd);
	}
	
	private static InvoiceInfo saveInvoiceInfo(AONContext ctx, Domain domain, Integer invoice, InvoiceCommunicationStatus status) {
		InvoiceInfo info = new InvoiceInfo()
			.setDomain(domain.getId())
			.setInvoice(invoice)
			.setType(InvoiceCommunicationType.VERIFACTU)
			.setStatus(status);
		return InvoiceInfoDAO.save(ctx, info);
	}
	
	private static InvoiceCommunicationStatus getInvoiceCommunicationStatus(EstadoRegistroType status) {
		if (EstadoRegistroType.CORRECTO == status) return InvoiceCommunicationStatus.ACCEPTED;
		else if (EstadoRegistroType.ACEPTADO_CON_ERRORES == status) return InvoiceCommunicationStatus.ACCEPTED_WITH_ERRORS;
		else if (EstadoRegistroType.INCORRECTO == status) return InvoiceCommunicationStatus.WRONG;
		return null;
	}

}
