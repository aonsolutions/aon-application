package net.aonsolutions.aon.sii;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatch;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestasuministro.EstadoRegistroType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestasuministro.RespuestaExpedidaBajaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestasuministro.RespuestaLRBajaFEmitidasType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.BajaLRFacturasEmitidas;
import net.aonsolutions.aon.sii.aeat.FacturasEmitidasBaja;
import net.aonsolutions.aon.sii.aeat.SIIAeatPost;
import net.aonsolutions.aon.sii.aeat.SiiValidation;

public class SII {

	// **************************************************************
	// ************************************************ [CANCEL] ****
	// **************************************************************
	
	public static InvoiceCommunicatorContext cancel(AONContext ctx, InvoiceCommunicatorContext context) throws InvoiceCommunicationException {
    	BajaLRFacturasEmitidas request = FacturasEmitidasBaja.getInstance().bajaFacturasEmitidas(context);
    	SiiValidation.validateFacturasEmitidasBaja(request);
    	RespuestaLRBajaFEmitidasType response = SIIAeatPost.getInstance(context.getConfig()).bajaFacturasEmitidas(context, request);
		return saveCancel(ctx, context, request, response);
	}
	
	private static InvoiceCommunicatorContext saveCancel(AONContext ctx, InvoiceCommunicatorContext context, BajaLRFacturasEmitidas request, RespuestaLRBajaFEmitidasType response) {
		byte[] requestBytes = FacturasEmitidasBaja.getInstance().getBajaFacturasEmitidas(request);
		byte[] responseBytes = FacturasEmitidasBaja.getInstance().getRespuestaBajaFacturasEmitidas(response);
		InvoiceBatch invoiceBatch = InvoiceCommunicationDAO.saveCancel(ctx, context.getDomain(), InvoiceCommunicationType.SII, requestBytes, responseBytes);
		AonCollectionUtils.stream(response.getRespuestaLinea())
			.forEach(r -> saveCancelInvoice( ctx, context, invoiceBatch, r));
		return context;
	}
	
	private static void saveCancelInvoice(AONContext ctx, InvoiceCommunicatorContext context, InvoiceBatch invoiceBatch, RespuestaExpedidaBajaType r) {
		if ( r == null) return; 
		Integer invoiceId = AonNumberUtils.toInteger(r.getRefExterna());
		if ( invoiceId == null) return;
		if ( AonMathUtils.isZero(invoiceId)) return;
		boolean correcto = AonEnumUtils.in(r.getEstadoRegistro(), EstadoRegistroType.CORRECTO, EstadoRegistroType.ACEPTADO_CON_ERRORES);
		InvoiceCommunicationDAO.saveInvoice(ctx, context.getDomain(), invoiceBatch, invoiceId, correcto 
				? InvoiceCommunicationStatus.CANCELLED : InvoiceCommunicationStatus.WRONG);
		InvoiceDAO.annul(ctx, invoiceId);
	}
}
