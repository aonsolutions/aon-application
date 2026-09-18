package net.aonsolutions.aon.sii;

import java.util.Objects;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.Invoice;
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
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestasuministro.RespuestaLRBajaFEmitidasType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestasuministro.RespuestaLRBajaFRecibidasType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.BajaLRFacturasEmitidas;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.BajaLRFacturasRecibidas;
import net.aonsolutions.aon.sii.aeat.FacturasEmitidasBaja;
import net.aonsolutions.aon.sii.aeat.FacturasRecibidasBaja;
import net.aonsolutions.aon.sii.aeat.SIIAeatPost;
import net.aonsolutions.aon.sii.aeat.SiiValidation;

public class SII {

	// **************************************************************
	// ************************************************ [CANCEL] ****
	// **************************************************************

	/**
	 * Da de baja en el SII las facturas del contexto.
	 *
	 * Cada libro registro tiene su propio servicio, por lo que las facturas
	 * emitidas y las recibidas se envian en dos comunicaciones distintas. Si el
	 * contexto solo tiene facturas de un tipo, solo se hace ese envio.
	 *
	 * @param ctx contexto de acceso a datos
	 * @param context contexto de comunicacion con el SII
	 * @return InvoiceCommunicatorContext
	 * @throws InvoiceCommunicationException si la baja no cumple las validaciones o falla el envio
	 */
	public static InvoiceCommunicatorContext cancel(AONContext ctx, InvoiceCommunicatorContext context) throws InvoiceCommunicationException {
		cancelEmitidas(ctx, context);
		cancelRecibidas(ctx, context);
		return context;
	}

	private static void cancelEmitidas(AONContext ctx, InvoiceCommunicatorContext context) throws InvoiceCommunicationException {
		if (context.invoiceStream().noneMatch(Invoice::isSales)) return;
    	BajaLRFacturasEmitidas request = FacturasEmitidasBaja.getInstance().bajaFacturasEmitidas(context);
    	SiiValidation.validateFacturasEmitidasBaja(request);
    	RespuestaLRBajaFEmitidasType response = SIIAeatPost.getInstance(context.getConfig()).bajaFacturasEmitidas(context, request);
		saveCancelEmitidas(ctx, context, request, response);
	}

	private static void cancelRecibidas(AONContext ctx, InvoiceCommunicatorContext context) throws InvoiceCommunicationException {
		if (context.invoiceStream().noneMatch(Invoice::isNotSales)) return;
    	BajaLRFacturasRecibidas request = FacturasRecibidasBaja.getInstance().bajaFacturasRecibidas(context);
    	SiiValidation.validateFacturasRecibidasBaja(request);
    	RespuestaLRBajaFRecibidasType response = SIIAeatPost.getInstance(context.getConfig()).bajaFacturasRecibidas(context, request);
		saveCancelRecibidas(ctx, context, request, response);
	}

	private static void saveCancelEmitidas(AONContext ctx, InvoiceCommunicatorContext context, BajaLRFacturasEmitidas request, RespuestaLRBajaFEmitidasType response) {
		byte[] requestBytes = FacturasEmitidasBaja.getInstance().getBajaFacturasEmitidas(request);
		byte[] responseBytes = FacturasEmitidasBaja.getInstance().getRespuestaBajaFacturasEmitidas(response);
		InvoiceBatch invoiceBatch = InvoiceCommunicationDAO.saveCancel(ctx, context.getDomain(), InvoiceCommunicationType.SII, requestBytes, responseBytes);
		AonCollectionUtils.stream(response.getRespuestaLinea())
			.filter(Objects::nonNull)
			.forEach(r -> saveCancelInvoice( ctx, context, invoiceBatch, r.getRefExterna(), r.getEstadoRegistro()));
	}

	private static void saveCancelRecibidas(AONContext ctx, InvoiceCommunicatorContext context, BajaLRFacturasRecibidas request, RespuestaLRBajaFRecibidasType response) {
		byte[] requestBytes = FacturasRecibidasBaja.getInstance().getBajaFacturasRecibidas(request);
		byte[] responseBytes = FacturasRecibidasBaja.getInstance().getRespuestaBajaFacturasRecibidas(response);
		InvoiceBatch invoiceBatch = InvoiceCommunicationDAO.saveCancel(ctx, context.getDomain(), InvoiceCommunicationType.SII, requestBytes, responseBytes);
		AonCollectionUtils.stream(response.getRespuestaLinea())
			.filter(Objects::nonNull)
			.forEach(r -> saveCancelInvoice( ctx, context, invoiceBatch, r.getRefExterna(), r.getEstadoRegistro()));
	}

	/**
	 * Guarda el resultado de la baja de una factura. La factura se identifica con
	 * la referencia externa que se informo en la baja: el id de la factura.
	 *
	 * @param ctx contexto de acceso a datos
	 * @param context contexto de comunicacion con el SII
	 * @param invoiceBatch comunicacion en la que se guarda el resultado
	 * @param refExterna referencia externa devuelta por la AEAT
	 * @param estadoRegistro estado del registro devuelto por la AEAT
	 */
	private static void saveCancelInvoice(AONContext ctx, InvoiceCommunicatorContext context, InvoiceBatch invoiceBatch, String refExterna, EstadoRegistroType estadoRegistro) {
		Integer invoiceId = AonNumberUtils.toInteger(refExterna);
		if ( invoiceId == null) return;
		if ( AonMathUtils.isZero(invoiceId)) return;
		boolean correcto = AonEnumUtils.in(estadoRegistro, EstadoRegistroType.CORRECTO, EstadoRegistroType.ACEPTADO_CON_ERRORES);
		if(correcto) {
			InvoiceCommunicationDAO.saveInvoice(ctx, context.getDomain(), invoiceBatch, invoiceId, InvoiceCommunicationStatus.CANCELLED);
			InvoiceDAO.annul(ctx, invoiceId);
		} else InvoiceCommunicationDAO.saveInvoiceBatchdetail(ctx, invoiceBatch, invoiceId, InvoiceCommunicationStatus.WRONG);
	}
}
