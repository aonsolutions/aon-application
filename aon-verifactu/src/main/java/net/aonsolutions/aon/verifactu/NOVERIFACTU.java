package net.aonsolutions.aon.verifactu;

import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Document;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegFactuSistemaFacturacion;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegistroFacturaType;

public class NOVERIFACTU {

	private NOVERIFACTU() {
		
	}

	// **************************************************************
	// ************************************************ [ACCEPT] ****
	// **************************************************************
	
	public static VerifactuContext accept(AONContext ctx, InvoiceCommunicatorContext invoiceCommunicatorContext) throws InvoiceCommunicationException {
		VerifactuContext vc = new VerifactuContext( invoiceCommunicatorContext )
			.setBlockchain( VERIFACTU.getBlockchain(ctx) )
			.setOperation(InvoiceCommunicationOperation.REGISTER)
		;
		return accept(ctx, vc);
	}
	
	private static VerifactuContext accept(AONContext ctx, VerifactuContext vc) throws InvoiceCommunicationException {
		VERIFACTU.check(vc);
		RegFactuSistemaFacturacion request = Invoice2Verifactu.build(vc);
		vc.setRequest( request );
		Document document = VerifactuXMLUtils.toDocument(request, RegFactuSistemaFacturacion.class);
		vc.setRequestBytes(VerifactuXMLUtils.toBytes(document));
		return saveAccept( ctx, vc );
	}
	

	private static VerifactuContext saveAccept(AONContext ctx, VerifactuContext vc) throws InvoiceCommunicationException {
		VERIFACTU.saveRequest(ctx, vc.getDomain(), vc.getRequestBytes());
		for ( RegistroFacturaType req : vc.getRequest().getRegistroFactura()) {		
			VERIFACTU.saveInvoiceData(ctx, vc, req.getRegistroAlta());
		}
		VERIFACTU.saveVerifactuBlockchain(ctx, vc.getDomain(), vc.getBlockchain());
		vc.invoiceStream()
			.forEach(i -> VERIFACTU.saveInvoiceInfo(ctx, vc.getDomain(), i.getId(), InvoiceCommunicationStatus.ACCEPTED));
		return vc;
	}
	
	// **************************************************************
	// ************************************************ [CANCEL] ****
	// **************************************************************
	public static VerifactuContext cancel(AONContext ctx, InvoiceCommunicatorContext invoiceCommunicatorContext) throws InvoiceCommunicationException {
		VerifactuContext vc = new VerifactuContext( invoiceCommunicatorContext )
			.setBlockchain( VERIFACTU.getBlockchain(ctx) )
			.setOperation(InvoiceCommunicationOperation.ANNULMENT);
		return cancel(ctx, vc);
	}
	
	private static VerifactuContext cancel(AONContext ctx, VerifactuContext vc) throws InvoiceCommunicationException {
		VERIFACTU.check(vc);
		RegFactuSistemaFacturacion request = Invoice2Verifactu.build(vc);
		vc.setRequest( request );
		Document document = VerifactuXMLUtils.toDocument(request, RegFactuSistemaFacturacion.class);
		vc.setRequestBytes(VerifactuXMLUtils.toBytes(document));
		SOAPMessage requestMessage = VerifactuXMLUtils.soapMarshal(document);
		vc.setResponse( VerifactuXMLUtils.post(vc.getConfig().getCertificate(), VerifactuUri.getUrlEmision(vc.isVerifactuTest()),requestMessage));
		return saveCancel( ctx, vc );
	}
	
	private static VerifactuContext saveCancel(AONContext ctx, VerifactuContext vc) {
		VERIFACTU.saveRequest(ctx, vc.getDomain(), vc.getRequestBytes());
		vc.invoiceStream()
			.forEach(i -> InvoiceDAO.delete(ctx, i.getId(), vc.isPreserveRawdocOnDeletion()));
		return vc;
	}
	
}
