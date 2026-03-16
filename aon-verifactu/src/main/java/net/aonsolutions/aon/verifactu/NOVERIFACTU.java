package net.aonsolutions.aon.verifactu;

import java.util.Date;

import org.w3c.dom.Document;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.AonSecret;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatch;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatchDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationPhaseListener;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceBatchDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceBatchDetailDAO;
import com.esferalia.aon.watson.util.AonMathUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegFactuSistemaFacturacion;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegistroFacturaType;
import net.aonsolutions.aon.sign.VerifactuSigner;
import net.aonsolutions.aon.sign.exception.AonSignerException;

public class NOVERIFACTU {

	private NOVERIFACTU() {
		
	}

	// **************************************************************
	// ************************************************ [ACCEPT] ****
	// **************************************************************
	
	public static VerifactuContext accept(AONContext ctx, InvoiceCommunicatorContext invoiceCommunicatorContext, CommunicationData enablerData, InvoiceCommunicationPhaseListener phase) throws InvoiceCommunicationException {
		VerifactuContext vc = new VerifactuContext( invoiceCommunicatorContext, enablerData )
			.setBlockchain( VERIFACTU.getBlockchain(ctx) )
			.setOperation(InvoiceCommunicationOperation.REGISTER)
		;
		return accept(ctx, vc, phase);
	}
	
	private static VerifactuContext accept(AONContext ctx, VerifactuContext vc, InvoiceCommunicationPhaseListener phase) throws InvoiceCommunicationException {
		try {
			check(vc);
			RegFactuSistemaFacturacion request = Invoice2Verifactu.build(ctx, InvoiceCommunicationType.NO_VERIFACTU,vc, phase);
			vc.setRequest( request );
			Document document = VerifactuXMLUtils.toDocument(request, RegFactuSistemaFacturacion.class);
			byte[] requestBytes = VerifactuXMLUtils.toBytes(document);
			Certificate cert = AonSecret.getAonCert();
			requestBytes = VerifactuSigner.getInstance().signWithoutTransform( cert, requestBytes );
			vc.setRequestBytes(requestBytes);
			return saveAccept( ctx, vc );
		} catch (AonSignerException e) {
			e.printStackTrace();
			throw new InvoiceCommunicationException( InvoiceCommunicationError.AON_0034, e.getMessage());
		}
	}
	

	private static VerifactuContext saveAccept(AONContext ctx, VerifactuContext vc) throws InvoiceCommunicationException {
		DataRequest dataRequest = saveRequest(ctx, vc.getDomain(), vc.getRequestBytes());	// save DATA REQUEST
		DataResponse dataResponse = saveResponse(ctx, vc.getDomain(), dataRequest, null );	// save DATA RESPONSE - NO DATA
		for ( RegistroFacturaType req : vc.getRequest().getRegistroFactura()) {		
			VERIFACTU.saveInvoiceData(ctx, vc, req.getRegistroAlta());	// save INVOICE DATA
		}
		VERIFACTU.saveVerifactuBlockchain(ctx, vc.getDomain(), vc.getBlockchain());	// save BLOCKCHAIN DATA		
		InvoiceBatch invoiceBatch = saveInvoiceBatch(ctx, vc.getDomain()	// save INVOICE BATCH
			, dataResponse	
			, InvoiceCommunicationOperation.REGISTER);								
		vc.invoiceStream()
			.filter(i -> !i.isProforma() )
			.forEach(i -> acceptInAON( ctx, vc, invoiceBatch, i) )
		;
		return vc.setDataResponse(dataResponse);
	}
	
	private static void acceptInAON(AONContext ctx, VerifactuContext vc, InvoiceBatch invoiceBatch, Invoice i) {
		if ( i == null) return; 
		Integer invoiceId = i.getId();
		if ( invoiceId == null) return;
		if ( AonMathUtils.isZero(invoiceId)) return;
		saveInvoiceCommunication(ctx, vc, invoiceBatch, invoiceId, InvoiceCommunicationStatus.PENDING);
		InvoiceDAO.saveInvoiceExpDate(ctx, invoiceId, new Date());
	}

	// **************************************************************
	// ************************************************ [CANCEL] ****
	// **************************************************************
	public static VerifactuContext cancel(AONContext ctx, InvoiceCommunicatorContext invoiceCommunicatorContext, CommunicationData enablerData) throws InvoiceCommunicationException {
		VerifactuContext vc = new VerifactuContext( invoiceCommunicatorContext, enablerData )
			.setBlockchain( VERIFACTU.getBlockchain(ctx) )
			.setOperation(InvoiceCommunicationOperation.ANNULMENT);
		return cancel(ctx, vc);
	}
	
	private static VerifactuContext cancel(AONContext ctx, VerifactuContext vc) throws InvoiceCommunicationException {
		check(vc);
		RegFactuSistemaFacturacion request = Invoice2Verifactu.build(ctx, InvoiceCommunicationType.NO_VERIFACTU, vc, null);
		vc.setRequest( request );
		Document document = VerifactuXMLUtils.toDocument(request, RegFactuSistemaFacturacion.class);
		vc.setRequestBytes(VerifactuXMLUtils.toBytes(document));
		return saveCancel( ctx, vc );
	}
	
	private static VerifactuContext saveCancel(AONContext ctx, VerifactuContext vc) {
		DataRequest dataRequest = saveRequest(ctx, vc.getDomain(), vc.getRequestBytes());	// save DATA REQUEST
		DataResponse dataResponse = saveResponse(ctx, vc.getDomain(), dataRequest, null );	// save DATA RESPONSE - NO DATA
		InvoiceBatch invoiceBatch =  saveInvoiceBatch(ctx, vc.getDomain(), dataResponse, InvoiceCommunicationOperation.ANNULMENT);
		vc.invoiceStream()
			.forEach(i -> cancelInAON( ctx, vc, invoiceBatch, i) )
		;
		return vc.setDataResponse(dataResponse);
	}
	
	private static void cancelInAON(AONContext ctx, VerifactuContext vc, InvoiceBatch invoiceBatch, Invoice i) {
		if ( i == null) return; 
		Integer invoiceId = i.getId();
		if ( invoiceId == null) return;
		if ( AonMathUtils.isZero(invoiceId)) return;
		InvoiceDAO.delete(ctx, invoiceId, vc.isPreserveRawdocOnDeletion());
	}
	
	// **************************************************************
	// ************************************************ [PRIVATE] ***
	// **************************************************************
	
	private static void check(VerifactuContext vc) throws InvoiceCommunicationException {
		VERIFACTU.checkCompany(vc);
		VERIFACTU.checkConfig(vc);
		VERIFACTU.checkInvoices(vc);
	}

	private static void saveInvoiceCommunication(AONContext ctx
		, VerifactuContext vc
		, InvoiceBatch invoiceBatch
		, Integer invoiceId
		, InvoiceCommunicationStatus status) {
		saveInvoiceInfo(ctx, vc.getDomainId(), invoiceId, status);
		saveInvoiceBatchdetail(ctx, invoiceBatch, invoiceId, status);			
	}
	
	static DataRequest saveRequest(AONContext ctx, Domain domain, byte[] request) {
		return InvoiceCommunicationDAO.saveRequest(ctx, domain, InvoiceCommunicationType.NO_VERIFACTU, request);
	}

	private static DataResponse saveResponse(AONContext ctx, Domain domain, DataRequest dataRequest, byte[] response) {
		return InvoiceCommunicationDAO.saveResponse(ctx, domain, InvoiceCommunicationType.NO_VERIFACTU, dataRequest, response);		
	}

	private static InvoiceBatch saveInvoiceBatch(AONContext ctx, Domain domain, DataResponse dataResponse, InvoiceCommunicationOperation operation) {
		InvoiceBatch invoiceBatch = new InvoiceBatch()
			.setDomain(domain.getId())
			.setType(InvoiceCommunicationType.NO_VERIFACTU)
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
		return InvoiceCommunicationDAO.saveInvoiceInfo(ctx, domainId, invoiceId, InvoiceCommunicationType.NO_VERIFACTU, status);
	}
	
}
