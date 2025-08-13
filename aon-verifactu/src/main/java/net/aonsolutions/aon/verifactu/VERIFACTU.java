package net.aonsolutions.aon.verifactu;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Document;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatch;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatchDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.finance.InvoiceData;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
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
	
	public static DataResponse accept(
			AONContext ctx,
			InvoiceCommunicationConfiguration configuration, 
			Company company, 
			List<Invoice> invoices) throws VerifactuException {
			
		VerifactuContext vc = new VerifactuContext()
			.setConfig(configuration)
			.setCompany(company)
			.setInvoices(invoices)
			.setBlockchain( getBlockchain(ctx) )
			.setOperation(InvoiceCommunicationOperation.REGISTER);
		
		return accept(ctx, vc);
	}
	
	private static DataResponse accept(AONContext ctx, VerifactuContext vc) throws VerifactuException {
		check(vc);
		RegFactuSistemaFacturacion request = Invoice2Verifactu.build(vc);
		vc.setRequest( request );
		Document document = VerifactuXMLUtils.toDocument(request, RegFactuSistemaFacturacion.class);
		vc.setRequestBytes(VerifactuXMLUtils.toBytes(document));
		SOAPMessage requestMessage = VerifactuXMLUtils.soapMarshal(document);
		vc.setResponse( VerifactuXMLUtils.post(vc.getConfig().getCertificate(), VerifactuUri.getUrlEmision(true),requestMessage));
		return saveAccept( ctx, vc );
	}

	private static DataResponse saveAccept(AONContext ctx, VerifactuContext vc) throws VerifactuException {
		DataRequest dataRequest = saveRequest(ctx, vc.getDomain(), vc.getRequestBytes());	// save DATA REQUEST
		DataResponse dataResponse = saveResponse(ctx, vc.getDomain(), dataRequest, vc.getResponse().getBytes());	// save DATA RESPONSE
		for ( RegistroFacturaType req : vc.getRequest().getRegistroFactura()) {		
			saveInvoiceData(ctx, vc.getDomain(), req.getRegistroAlta());	// save INVOICE DATA
		}
		saveVerifactuBlockchain(ctx, vc.getDomain(), vc.getBlockchain());	// save BLOCKCHAIN DATA		
		InvoiceBatch invoiceBatch = saveInvoiceBatch(ctx, vc.getDomain()	// save INVOICE BATCH
			, dataResponse	
			, InvoiceCommunicationOperation.REGISTER);								
		if (vc.getResponse().isError()) {
			vc.getInvoices().stream().forEach(inv -> {
				saveInvoiceInfo(ctx, vc.getDomain(), inv.getId(), InvoiceCommunicationStatus.WRONG);
				saveInvoiceBatchdetail(ctx, vc.getDomain(), invoiceBatch, inv.getId(), InvoiceCommunicationStatus.WRONG);					
			});
		} else {
			RespuestaRegFactuSistemaFacturacionType respuesta = vc.getResponse().getResponse();
			if (!respuesta.getRespuestaLinea().isEmpty()) {
				respuesta.getRespuestaLinea().stream().forEach(r -> {
					Integer invoiceId = AonNumberUtils.toInteger(r.getRefExterna());
					InvoiceCommunicationStatus status = getInvoiceCommunicationStatus(r.getEstadoRegistro());
					saveInvoiceInfo(ctx, vc.getDomain(), invoiceId, status); 
					saveInvoiceBatchdetail(ctx, vc.getDomain(), invoiceBatch, invoiceId, status);				
				});
			}
		}
		return dataResponse;
	}
	
	// **************************************************************
	// ************************************************ [CANCEL] ****
	// **************************************************************
	public static DataResponse cancel(
			AONContext ctx,
			InvoiceCommunicationConfiguration configuration, 
			Company company, 
			List<Invoice> invoices) throws VerifactuException {
		
		VerifactuContext vc = new VerifactuContext()
			.setConfig(configuration)
			.setCompany(company)
			.setInvoices(invoices)
			.setBlockchain( getBlockchain(ctx) )
			.setOperation(InvoiceCommunicationOperation.ANNULMENT);
		return cancel(ctx, vc);
	}
	
	private static DataResponse cancel(AONContext ctx, VerifactuContext vc) throws VerifactuException {
		check(vc);
		RegFactuSistemaFacturacion request = Invoice2Verifactu.build(vc);
		vc.setRequest( request );
		Document document = VerifactuXMLUtils.toDocument(request, RegFactuSistemaFacturacion.class);
		vc.setRequestBytes(VerifactuXMLUtils.toBytes(document));
		SOAPMessage requestMessage = VerifactuXMLUtils.soapMarshal(document);
		vc.setResponse( VerifactuXMLUtils.post(vc.getConfig().getCertificate(), VerifactuUri.getUrlEmision(true),requestMessage));
		return saveCancel( ctx, vc );
	}
	
	private static DataResponse saveCancel(AONContext ctx, VerifactuContext vc) {
		DataRequest dataRequest = saveRequest(ctx, vc.getDomain(), vc.getRequestBytes());	// save DATA REQUEST
		DataResponse dataResponse = saveResponse(ctx, vc.getDomain(), dataRequest, vc.getResponse().getBytes());	// save DATA RESPONSE
		saveVerifactuBlockchain(ctx, vc.getDomain(), vc.getBlockchain());	// save BLOCKCHAIN DATA
		if (!vc.getResponse().isError()) {	// DELETE ANNULLED INVOICES
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
		return dataResponse;
	}
	
	// **************************************************************
	// ************************************************ [PRIVATE] ***
	// **************************************************************
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
	
	// **************************************************************
	// **************************************************************
	// **************************************************************

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

	private static void saveInvoiceData(AONContext ctx, Domain domain, RegistroFacturacionAltaType registroAlta) throws VerifactuException {
		Integer invoiceId = AonNumberUtils.toInteger(registroAlta.getRefExterna());
		InvoiceDataDAO.save(ctx, new InvoiceData()
			.setDomain(domain.getId())
			.setInvoice(invoiceId)
			.setName(InvoiceData.VERIFACTU_HUELLA)
			.setValue(registroAlta.getHuella())
		);
		String qrUrl = getQrUrl(registroAlta);
		InvoiceDataDAO.save(ctx, new InvoiceData()
			.setDomain(domain.getId())
			.setInvoice(invoiceId)
			.setName(InvoiceData.VERIFACTU_QR)
			.setValue(qrUrl));
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
	
	private static InvoiceBatch saveInvoiceBatch(AONContext ctx, Domain domain, DataResponse dataResponse, InvoiceCommunicationOperation operation) {
		InvoiceBatch invoiceBatch = new InvoiceBatch()
			.setDomain(domain.getId())
			.setType(InvoiceCommunicationType.VERIFACTU)
			.setDate(new Date())
			.setOperation(operation)
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

	private static VerifactuBlockchain getBlockchain(AONContext ctx) {	
		return new VerifactuBlockchain()
			.setDocument(AppParamDAO.fetchValue(ctx, AppParam.VERIFACTU_BLOCKCHAIN_DOCUMENT))
			.setReference(AppParamDAO.fetchValue(ctx, AppParam.VERIFACTU_BLOCKCHAIN_REFERENCE))
			.setDate(AppParamDAO.fetchValue(ctx, AppParam.VERIFACTU_BLOCKCHAIN_DATE))
			.setHuella(AppParamDAO.fetchValue(ctx, AppParam.VERIFACTU_BLOCKCHAIN_HUELLA));
	}
}
