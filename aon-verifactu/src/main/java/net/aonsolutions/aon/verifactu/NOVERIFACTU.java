package net.aonsolutions.aon.verifactu;

import org.w3c.dom.Document;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.AonSecret;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationPhaseListener;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;

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
	
	public static VerifactuContext accept(AONContext ctx, InvoiceCommunicatorContext invoiceCommunicatorContext, InvoiceCommunicationPhaseListener phase) throws InvoiceCommunicationException {
		VerifactuContext vc = new VerifactuContext( invoiceCommunicatorContext )
			.setBlockchain( VERIFACTU.getBlockchain(ctx) )
			.setOperation(InvoiceCommunicationOperation.REGISTER)
		;
		return accept(ctx, vc, phase);
	}
	
	private static VerifactuContext accept(AONContext ctx, VerifactuContext vc, InvoiceCommunicationPhaseListener phase) throws InvoiceCommunicationException {
		try {
			check(vc);
			RegFactuSistemaFacturacion request = Invoice2Verifactu.build(ctx,vc, phase);
			vc.setRequest( request );
			Document document = VerifactuXMLUtils.toDocument(request, RegFactuSistemaFacturacion.class);
			byte[] requestBytes = VerifactuXMLUtils.toBytes(document);
			Certificate cert = AonSecret.getAonCert();
			requestBytes = VerifactuSigner.getInstance().sign( cert, requestBytes );
			vc.setRequestBytes(requestBytes);
			return saveAccept( ctx, vc );
		} catch (AonSignerException e) {
			throw new InvoiceCommunicationException( InvoiceCommunicationError.AON_0034, e.getMessage());
		}
	}
	

	private static VerifactuContext saveAccept(AONContext ctx, VerifactuContext vc) throws InvoiceCommunicationException {
		VERIFACTU.saveRequest(ctx, vc.getDomain(), vc.getRequestBytes());
		for ( RegistroFacturaType req : vc.getRequest().getRegistroFactura()) {		
			VERIFACTU.saveInvoiceData(ctx, vc, req.getRegistroAlta());
		}
		VERIFACTU.saveVerifactuBlockchain(ctx, vc.getDomain(), vc.getBlockchain());
		vc.invoiceStream()
			.forEach(i -> VERIFACTU.saveInvoiceInfo(ctx, vc.getDomainId(), i.getId(), InvoiceCommunicationStatus.ACCEPTED));
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
		check(vc);
		RegFactuSistemaFacturacion request = Invoice2Verifactu.build(ctx, vc, null);
		vc.setRequest( request );
		Document document = VerifactuXMLUtils.toDocument(request, RegFactuSistemaFacturacion.class);
		vc.setRequestBytes(VerifactuXMLUtils.toBytes(document));
		return saveCancel( ctx, vc );
	}
	
	private static VerifactuContext saveCancel(AONContext ctx, VerifactuContext vc) {
		VERIFACTU.saveRequest(ctx, vc.getDomain(), vc.getRequestBytes());
		vc.invoiceStream()
			.forEach(i -> InvoiceDAO.delete(ctx, i.getId(), vc.isPreserveRawdocOnDeletion()));
		return vc;
	}
	
	// **************************************************************
	// ************************************************ [PRIVATE] ***
	// **************************************************************
	
	private static void check(VerifactuContext vc) throws InvoiceCommunicationException {
		VERIFACTU.checkCompany(vc);
		VERIFACTU.checkConfig(vc);
		VERIFACTU.checkInvoices(vc);
	}

	static DataRequest saveRequest(AONContext ctx, Domain domain, byte[] request) {
		return InvoiceCommunicationDAO.saveRequest(ctx, domain, InvoiceCommunicationType.NO_VERIFACTU, request);
	}

	static InvoiceInfo saveInvoiceInfo(AONContext ctx, Integer domainId, Integer invoiceId, InvoiceCommunicationStatus status) {
		return InvoiceCommunicationDAO.saveInvoiceInfo(ctx, domainId, invoiceId, InvoiceCommunicationType.NO_VERIFACTU, status);
	}
	
}
