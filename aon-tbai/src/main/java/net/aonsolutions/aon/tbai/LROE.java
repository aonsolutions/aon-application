package net.aonsolutions.aon.tbai;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatch;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.watson.util.AonDocumentUtil;

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.EstadoRegistroEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.RegistroAnulacionFacturaConSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.RegistrosAnulacionFacturaConSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_1_1_ingresos_confacturaconsg_anulacionpeticion_v1_0_0.LROEPF140IngresosConFacturaConSGAnulacionPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_1_1_ingresos_confacturaconsg_anulacionrespuesta_v1_0_0.LROEPF140IngresosConFacturaConSGAnulacionRespuesta;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_1_1_facturasemitidas_consg_anulacionpeticion_v1_0_0.LROEPJ240FacturasEmitidasConSGAnulacionPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_1_1_facturasemitidas_consg_anulacionrespuesta_v1_0_0.LROEPJ240FacturasEmitidasConSGAnulacionRespuesta;
import net.aonsolutions.aon.tbai.lroe.LROE140_1_1;
import net.aonsolutions.aon.tbai.lroe.LROE240_1_1;
import net.aonsolutions.aon.tbai.utils.XMLUtils;

public class LROE {
	
	private LROE() {
		
	}
	
	
	// **************************************************************
	// ************************************************ [CANCEL] ****
	// **************************************************************
	
	public static InvoiceCommunicatorContext cancel(AONContext ctx, InvoiceCommunicatorContext context) throws InvoiceCommunicationException {
		/*
		 * PERSONA FISICA - Facturas Emitidas con Software Garante desde AON LROE140_1_1
		 * PERSONA FISICA - Facturas Emitidas sin Software Garante LROE140_1_2
		 * PERSONA FISICA - Facturas Recibidas LROE140_2_1
		 * PERSONA JURIDICA -  Facturas Emitidas con Software Garante desde AON LROE240_1_1
		 * PERSONA JURIDICA - Facturas Emitidas sin Software Garante LROE240_1_2
		 * PERSONA JURIDICA - Facturas Recibidas LROE240_2
		 */
				
		return isPersonaFisica(context)
			? cancel140(ctx, context)
			: cancel240(ctx, context);
	}
	
	public static InvoiceCommunicatorContext cancel140(AONContext ctx, InvoiceCommunicatorContext context) throws InvoiceCommunicationException {
		// TODO: Implementar la anulacion de facturas recibidas (LROE140_2_1).
		return cancel140_1_1(ctx, context);
	}
	
	public static InvoiceCommunicatorContext cancel240(AONContext ctx, InvoiceCommunicatorContext context) throws InvoiceCommunicationException {
		// TODO: Implementar la anulacion de facturas recibidas (LROE240_2).
		return cancel240_1_1(ctx, context);
	}
	
	/**
	 * Anula las facturas emitidas con software garante de una persona fisica
	 * (subcapitulo LROE_PF_140_1_1).
	 */
	public static InvoiceCommunicatorContext cancel140_1_1(AONContext ctx, InvoiceCommunicatorContext context) throws InvoiceCommunicationException {
		try {
			LROEPF140IngresosConFacturaConSGAnulacionPeticion request = LROE140_1_1.buildBaja(context);
			LroeValidation.validateAnulacionConSG(request);
			byte[] requestBytes = XMLUtils.marshal(request, LROEPF140IngresosConFacturaConSGAnulacionPeticion.class);
			byte[] responseBytes = LROE140_1_1.sendCancel(context, requestBytes);
			LROEPF140IngresosConFacturaConSGAnulacionRespuesta response = (LROEPF140IngresosConFacturaConSGAnulacionRespuesta)
					XMLUtils.unmarshal(responseBytes, LROEPF140IngresosConFacturaConSGAnulacionRespuesta.class);
			saveCancel(ctx, context, requestBytes, responseBytes, response.getRegistros());
		} catch (InvoiceCommunicationException | JAXBException | IOException e) {
			e.printStackTrace();
			throw new InvoiceCommunicationException(e);
		}
			
		return context;
	}
	
	/**
	 * Anula las facturas emitidas con software garante de una persona juridica
	 * (subcapitulo LROE_PJ_240_1_1).
	 */
	public static InvoiceCommunicatorContext cancel240_1_1(AONContext ctx, InvoiceCommunicatorContext context) throws InvoiceCommunicationException {
		try {
			LROEPJ240FacturasEmitidasConSGAnulacionPeticion request = LROE240_1_1.buildBaja(context);
			LroeValidation.validateAnulacionConSG(request);
			byte[] requestBytes = XMLUtils.marshal(request, LROEPJ240FacturasEmitidasConSGAnulacionPeticion.class);
			byte[] responseBytes = LROE240_1_1.sendCancel(context, requestBytes);
			LROEPJ240FacturasEmitidasConSGAnulacionRespuesta response = (LROEPJ240FacturasEmitidasConSGAnulacionRespuesta)
					XMLUtils.unmarshal(responseBytes, LROEPJ240FacturasEmitidasConSGAnulacionRespuesta.class);
			saveCancel(ctx, context, requestBytes, responseBytes, response.getRegistros());
		} catch (InvoiceCommunicationException | JAXBException | IOException e) {
			e.printStackTrace();
			throw new InvoiceCommunicationException(e);
		}
			
		return context;
	}
	
	/**
	 * El obligado tributario es persona fisica cuando su documento no es un CIF
	 * valido o cuando se trata de una comunidad de bienes, una comunidad de
	 * propietarios o una sociedad civil.
	 */
	private static boolean isPersonaFisica(InvoiceCommunicatorContext context) {
		String document = context.getCompany() != null ? context.getCompany().getDocument() : null;
		return !AonDocumentUtil.isValidCIF(document) 
			|| AonDocumentUtil.isAssetCommunity(document)
			|| AonDocumentUtil.isOwnerCommunity(document) 
			|| AonDocumentUtil.isCivilSociety(document);
	}
	
	private static InvoiceCommunicatorContext saveCancel(AONContext ctx, InvoiceCommunicatorContext context, byte[] requestBytes, 
			byte[] responseBytes, RegistrosAnulacionFacturaConSGType registros) {
		InvoiceBatch invoiceBatch = InvoiceCommunicationDAO.saveCancel(ctx, context.getDomain(), InvoiceCommunicationType.LROE, requestBytes, responseBytes);
		if (registros == null) return context;
		for (RegistroAnulacionFacturaConSGType reg : registros.getRegistro()) {
			String serie = reg.getIdentificador().getIDFactura().getSerieFactura();
			String number = reg.getIdentificador().getIDFactura().getNumFactura();
			EstadoRegistroEnum status= reg.getSituacionRegistro().getEstadoRegistro();
			context.invoiceStream()
				.filter(i -> i.getSeries().equals(serie) && Integer.toString(i.getNumber()).equals(number))
				.findFirst()
				.ifPresent(invoice -> saveCancelInvoice(ctx, context, invoiceBatch, invoice, EstadoRegistroEnum.ANULADO.equals(status)));
		} 
		return context;
	}
	
	private static void saveCancelInvoice(AONContext ctx, InvoiceCommunicatorContext context, InvoiceBatch invoiceBatch, Invoice invoice, boolean correcto) {
		if(correcto) {
			InvoiceCommunicationDAO.saveInvoice(ctx, context.getDomain(), invoiceBatch, invoice.getId(), InvoiceCommunicationStatus.CANCELLED);
			InvoiceDAO.annul(ctx, invoice.getId());
		} else InvoiceCommunicationDAO.saveInvoiceBatchdetail(ctx, invoiceBatch, invoice.getId(), InvoiceCommunicationStatus.WRONG);
	}
	
}
