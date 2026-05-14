package net.aonsolutions.aon.verifactu;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Document;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatch;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatchDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceData;
import com.esferalia.aon.occam.api.model.finance.InvoiceDataName;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationPhaseListener;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorKey;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorMessages;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceBatchDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceBatchDetailDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceDataDAO;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.consultalr.ConsultaFactuSistemaFacturacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.consultalr.ObjectFactory;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.respuestasuministro.EstadoRegistroType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.respuestasuministro.RespuestaExpedidaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.respuestasuministro.RespuestaRegFactuSistemaFacturacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAltaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.TipoOperacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegFactuSistemaFacturacion;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegistroFacturaType;

public class VERIFACTU {

	private static final String VF = "VF";
	
	private VERIFACTU() {
		
	}

	// **************************************************************
	// ************************************************ [QUERY] *****
	// **************************************************************
	public static VerifactuContext query(InvoiceCommunicatorContext invoiceCommunicatorContext, CommunicationData enablerData) throws InvoiceCommunicationException {
		VerifactuContext vc = new VerifactuContext( invoiceCommunicatorContext, enablerData)
			.setOperation(InvoiceCommunicationOperation.CONSULTATION);
		return query(vc);
	}
	
	private static VerifactuContext query(VerifactuContext vc) throws InvoiceCommunicationException {
		checkQuery(vc);
		ConsultaFactuSistemaFacturacionType request = Query2Verifactu.build(vc);
		vc.setQueryRequest( request );
		ObjectFactory of = new ObjectFactory();
		JAXBElement<ConsultaFactuSistemaFacturacionType> jaxbElement = of.createConsultaFactuSistemaFacturacion(request);
		Document document = VerifactuXMLUtils.toDocument(jaxbElement, ConsultaFactuSistemaFacturacionType.class);
		vc.setRequestBytes(VerifactuXMLUtils.toBytes(document));
		SOAPMessage requestMessage = VerifactuXMLUtils.soapMarshal(document);
		VerifactuResponse dataResponse = VerifactuXMLUtils.postQuery(vc.getConfig().getCertificate(), VerifactuUri.getUrlEmision(vc.isVerifactuTest()),requestMessage);
		vc.setResponse( dataResponse );
		return vc;
	}

	// **************************************************************
	// ************************************************ [ACCEPT] ****
	// **************************************************************
	
	public static VerifactuContext accept(AONContext ctx, InvoiceCommunicatorContext invoiceCommunicatorContext, CommunicationData enablerData, InvoiceCommunicationPhaseListener phase) throws InvoiceCommunicationException {
		VerifactuContext vc = new VerifactuContext( invoiceCommunicatorContext, enablerData )
			.setBlockchain( getBlockchain(ctx) )
			.setOperation(InvoiceCommunicationOperation.REGISTER)
		;
		return accept(ctx, vc, phase);
	}
	
	private static VerifactuContext accept(AONContext ctx, VerifactuContext vc, InvoiceCommunicationPhaseListener phase) throws InvoiceCommunicationException {
		vc.getLogger().subtitle(VF, "Comunicaci\u00F3n VERIFACTU de " + vc.invoiceCount() + " factura(s).");		
		check(vc);
		vc.getLogger().message(VF, "Construyendo mensaje para VERIFACTU");
		RegFactuSistemaFacturacion request = Invoice2Verifactu.build(ctx, InvoiceCommunicationType.VERIFACTU, vc, phase );
		vc.setRequest( request );
		Document document = VerifactuXMLUtils.toDocument(request, RegFactuSistemaFacturacion.class);
		vc.setRequestBytes(VerifactuXMLUtils.toBytes(document));
		vc.getLogger().message(VF, "Enviando mensaje a VERIFACTU");
		SOAPMessage requestMessage = VerifactuXMLUtils.soapMarshal(document);
		VerifactuResponse dataResponse = VerifactuXMLUtils.post(vc.getConfig().getCertificate(), VerifactuUri.getUrlEmision(vc.isVerifactuTest()),requestMessage);
		vc.getLogger().message(VF, "Procesando respuesta de VERIFACTU");
		vc.setResponse( dataResponse );
		saveAccept( ctx, vc );
		vc.getLogger().message(VF, "Almacenando respuesta de VERIFACTU");
		return vc; 
	}
	

	private static VerifactuContext saveAccept(AONContext ctx, VerifactuContext vc) throws InvoiceCommunicationException {
		DataRequest dataRequest = saveRequest(ctx, vc.getDomain(), vc.getRequestBytes());	// save DATA REQUEST
		DataResponse dataResponse = saveResponse(ctx, vc.getDomain(), dataRequest, vc.getResponse().getBytes());	// save DATA RESPONSE
		for ( RegistroFacturaType req : vc.getRequest().getRegistroFactura()) {		
			saveInvoiceData(ctx, vc, req.getRegistroAlta());	// save INVOICE DATA
		}
		saveVerifactuBlockchain(ctx, vc.getDomain(), vc.getBlockchain());	// save BLOCKCHAIN DATA		
		InvoiceBatch invoiceBatch = saveInvoiceBatch(ctx, vc.getDomain()	// save INVOICE BATCH
			, dataResponse	
			, InvoiceCommunicationOperation.REGISTER);								
		RespuestaRegFactuSistemaFacturacionType respuesta = vc.getResponse().getResponse();
		AonCollectionUtils.stream(respuesta.getRespuestaLinea())
			.forEach(r -> doInAON( ctx, vc, invoiceBatch, r) )
		;
		return vc.setDataResponse(dataResponse);
	}
	
	// **************************************************************
	// ************************************************ [CANCEL] ****
	// **************************************************************
	public static VerifactuContext cancel(AONContext ctx, InvoiceCommunicatorContext invoiceCommunicatorContext, CommunicationData enablerData) throws InvoiceCommunicationException {
		VerifactuContext vc = new VerifactuContext( invoiceCommunicatorContext, enablerData)
			.setBlockchain( getBlockchain(ctx) )
			.setOperation(InvoiceCommunicationOperation.ANNULMENT);
		return cancel(ctx, vc);
	}
	
	private static VerifactuContext cancel(AONContext ctx, VerifactuContext vc) throws InvoiceCommunicationException {
		check(vc);
		RegFactuSistemaFacturacion request = Invoice2Verifactu.build(ctx, InvoiceCommunicationType.VERIFACTU, vc, null);
		vc.setRequest( request );
		Document document = VerifactuXMLUtils.toDocument(request, RegFactuSistemaFacturacion.class);
		vc.setRequestBytes(VerifactuXMLUtils.toBytes(document));
		SOAPMessage requestMessage = VerifactuXMLUtils.soapMarshal(document);
		vc.setResponse( VerifactuXMLUtils.post(vc.getConfig().getCertificate(), VerifactuUri.getUrlEmision(vc.isVerifactuTest()),requestMessage));
		return saveCancel( ctx, vc );
	}
	
	private static VerifactuContext saveCancel(AONContext ctx, VerifactuContext vc) {
		DataRequest dataRequest = saveRequest(ctx, vc.getDomain(), vc.getRequestBytes());	// save DATA REQUEST
		DataResponse dataResponse = saveResponse(ctx, vc.getDomain(), dataRequest, vc.getResponse().getBytes());	// save DATA RESPONSE
		InvoiceBatch invoiceBatch =  saveInvoiceBatch(ctx, vc.getDomain(), dataResponse, InvoiceCommunicationOperation.ANNULMENT);
		RespuestaRegFactuSistemaFacturacionType respuesta = vc.getResponse().getResponse();
		AonCollectionUtils.stream(respuesta.getRespuestaLinea())
			.forEach(r -> doInAON( ctx, vc, invoiceBatch, r) )
		;
		return vc.setDataResponse(dataResponse);
	}
	
	private static void doInAON(AONContext ctx, VerifactuContext vc, InvoiceBatch invoiceBatch, RespuestaExpedidaType r) {
		if ( r == null) return; 
		if ( r.getOperacion() == null) return;
		Integer invoiceId = AonNumberUtils.toInteger(r.getRefExterna());
		if ( invoiceId == null) return;
		if ( AonMathUtils.isZero(invoiceId)) return;
		TipoOperacionType operacion = r.getOperacion().getTipoOperacion();
		boolean correcto = AonEnumUtils.in(r.getEstadoRegistro(), EstadoRegistroType.CORRECTO, EstadoRegistroType.ACEPTADO_CON_ERRORES);
		switch (operacion) {
		
			case ALTA: {
				InvoiceCommunicationStatus status = getInvoiceCommunicationStatus(r.getEstadoRegistro());
				saveInvoiceCommunication(ctx, vc, invoiceBatch, invoiceId, status);
				if (AonEnumUtils.in(r.getEstadoRegistro(), EstadoRegistroType.INCORRECTO, EstadoRegistroType.ACEPTADO_CON_ERRORES)) {
					vc.invoiceStream()
						.filter(i -> AonNumberUtils.equals(i.getId(),invoiceId ))
						.forEach(i -> i.addMessage( InvoiceErrorMessages.C051.err(InvoiceErrorKey.COMMUNICATION,r.getCodigoErrorRegistro(),r.getDescripcionErrorRegistro())))							
					;
				}
				InvoiceDAO.saveInvoiceExpDate(ctx, invoiceId, new Date());
				break;
			}
			
			case ANULACION: {
				if (correcto) {
					// Si la respuesta es correcta, se borra la factura.
					InvoiceDAO.delete(ctx, invoiceId, vc.isPreserveRawdocOnDeletion());
				} else {
					// Si la respuesta es incorrecta, se guarda la comunicación.
					saveInvoiceBatchdetail(ctx, invoiceBatch, invoiceId, InvoiceCommunicationStatus.WRONG);
				}
				break;
			}
			
			default: {
				// Nothing
			}
		}
	}
	
	// **************************************************************
	// ************************************************ [PRIVATE] ***
	// **************************************************************
	
	private static void saveInvoiceCommunication(AONContext ctx
		, VerifactuContext vc
		, InvoiceBatch invoiceBatch
		, Integer invoiceId
		, InvoiceCommunicationStatus status) {
		saveInvoiceInfo(ctx, vc.getDomainId(), invoiceId, status);
		saveInvoiceBatchdetail(ctx, invoiceBatch, invoiceId, status);					
	}
	
	private static void checkQuery(VerifactuContext vc) throws InvoiceCommunicationException {
		checkCompany(vc);
		checkConfig(vc);
		if (vc.getInvoiceCommunicationQuery() == null) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0028);
		}
		if (vc.getInvoiceCommunicationQuery().getYear().isEmpty() ) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0029);
		}
		if (vc.getInvoiceCommunicationQuery().getMonth().isEmpty() ) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0030);
		}
	}
	
	private static void check(VerifactuContext vc) throws InvoiceCommunicationException {
		checkCompany(vc);
		checkConfig(vc);
		checkCertificate(vc);
		checkInvoices(vc);
	}
	
	static void checkCompany(VerifactuContext vc) throws InvoiceCommunicationException {
		if (vc == null) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0001);
		}
		if (vc.getCompany() == null) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0002);
		}
		if (vc.getCompany().getDomain() == null
			|| vc.getCompany().getDomain().getId() == null
			|| AonStringUtils.isBlank( vc.getCompany().getDomain().getName())) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0003);
		}
	}
	
	static void checkConfig(VerifactuContext vc) throws InvoiceCommunicationException {
		if (vc.getConfig() == null) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0006);
		}
	}
	
	private static void checkCertificate(VerifactuContext vc) throws InvoiceCommunicationException {
		if (vc.getConfig().getCertificate() == null) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0007);
		}
	}

	static void checkInvoices(VerifactuContext vc) throws InvoiceCommunicationException {
		if (vc.invoiceCount() == 0) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0005);
		}
	}
	
	// **************************************************************
	// **************************************************************
	// **************************************************************

	static DataRequest saveRequest(AONContext ctx, Domain domain, byte[] request) {
		return InvoiceCommunicationDAO.saveRequest(ctx, domain, InvoiceCommunicationType.VERIFACTU, request);
	}

	private static DataResponse saveResponse(AONContext ctx, Domain domain, DataRequest dataRequest, byte[] response) {
		return InvoiceCommunicationDAO.saveResponse(ctx, domain, InvoiceCommunicationType.VERIFACTU, dataRequest, response);		
	}

	static void saveInvoiceData(AONContext ctx, VerifactuContext vc, RegistroFacturacionAltaType registroAlta) throws InvoiceCommunicationException {
		Domain domain = vc.getDomain();
		Integer invoiceId = AonNumberUtils.toInteger(registroAlta.getRefExterna());
		InvoiceDataDAO.save(ctx, new InvoiceData()
			.setDomain(domain.getId())
			.setInvoice(invoiceId)
			.setName(InvoiceDataName.VERIFACTU_HUELLA)
			.setValue(registroAlta.getHuella())
		);
		
		String qrUrl = getQrUrl(vc, registroAlta);
		InvoiceDataDAO.save(ctx, new InvoiceData()
			.setDomain(domain.getId())
			.setInvoice(invoiceId)
			.setName(InvoiceDataName.VERIFACTU_QR)
			.setValue(qrUrl));
	}
	
	private static String getQrUrl(VerifactuContext vc, RegistroFacturacionAltaType alta) throws InvoiceCommunicationException {
//		return new StringBuilder(VerifactuUri.getUrlQr(vc.getConfig().isVerifactuTest()))
		return new StringBuilder()
			.append("?")
			.append("nif=").append(encodeParam(alta.getIDFactura().getIDEmisorFactura())).append("&")
			.append("numserie=").append(encodeParam(alta.getIDFactura().getNumSerieFactura())).append("&")
			.append("fecha=").append(encodeParam(alta.getIDFactura().getFechaExpedicionFactura())).append("&")
			.append("importe=").append(encodeParam(alta.getImporteTotal()))
			.toString();
	}
	
	private static String encodeParam(String param) throws InvoiceCommunicationException {
		try {
			return URLEncoder.encode(param, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0008, e );
		}
	}
	
	static void saveVerifactuBlockchain(AONContext ctx, Domain domain, VerifactuBlockchain blockchain) {
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
	
	private static InvoiceBatchDetail saveInvoiceBatchdetail(AONContext ctx, InvoiceBatch invoiceBatch, Integer invoice, InvoiceCommunicationStatus status) {
		InvoiceBatchDetail ibd = new InvoiceBatchDetail()
			.setDomain(invoiceBatch.getDomain())
			.setInvoiceBatch(invoiceBatch.getId())
			.setInvoice(invoice)
			.setStatus(status);
		return InvoiceBatchDetailDAO.save(ctx, ibd);
	}
	
	static InvoiceInfo saveInvoiceInfo(AONContext ctx, Integer domainId, Integer invoiceId, InvoiceCommunicationStatus status) {
		return InvoiceCommunicationDAO.saveInvoiceInfo(ctx, domainId, invoiceId, InvoiceCommunicationType.VERIFACTU, status);
	}
	
	private static InvoiceCommunicationStatus getInvoiceCommunicationStatus(EstadoRegistroType status) {
		if (EstadoRegistroType.CORRECTO == status) return InvoiceCommunicationStatus.ACCEPTED;
		else if (EstadoRegistroType.ACEPTADO_CON_ERRORES == status) return InvoiceCommunicationStatus.ACCEPTED_WITH_ERRORS;
		else if (EstadoRegistroType.INCORRECTO == status) return InvoiceCommunicationStatus.WRONG;
		return null;
	}

	static VerifactuBlockchain getBlockchain(AONContext ctx) {	
		return new VerifactuBlockchain()
			.setDocument(AppParamDAO.fetchValue(ctx, AppParam.VERIFACTU_BLOCKCHAIN_DOCUMENT))
			.setReference(AppParamDAO.fetchValue(ctx, AppParam.VERIFACTU_BLOCKCHAIN_REFERENCE))
			.setDate(AppParamDAO.fetchValue(ctx, AppParam.VERIFACTU_BLOCKCHAIN_DATE))
			.setHuella(AppParamDAO.fetchValue(ctx, AppParam.VERIFACTU_BLOCKCHAIN_HUELLA));
	}

	public static List<String> history(byte[] responseData, Integer invoiceId) {
		try {
			return VerifactuXMLUtils.parseHistory(responseData, invoiceId);
		} catch (InvoiceCommunicationException e) {
			return AonCollectionUtils.toList("Error al interpretar la respuesta: " + e.getMessage() );
		}
	}
	
}
