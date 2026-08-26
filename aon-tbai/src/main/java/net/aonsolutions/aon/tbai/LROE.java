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

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.EstadoRegistroEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.RegistroAnulacionFacturaConSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_1_1_ingresos_confacturaconsg_anulacionpeticion_v1_0_0.LROEPF140IngresosConFacturaConSGAnulacionPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_1_1_ingresos_confacturaconsg_anulacionrespuesta_v1_0_0.LROEPF140IngresosConFacturaConSGAnulacionRespuesta;
import net.aonsolutions.aon.tbai.lroe.LROE140_1_1;
import net.aonsolutions.aon.tbai.utils.XMLUtils;

public class LROE {
	
	private LROE() {
		
	}
	
	
	// **************************************************************
	// ************************************************ [CANCEL] ****
	// **************************************************************
	
	public static InvoiceCommunicatorContext cancel(AONContext ctx, InvoiceCommunicatorContext context) throws InvoiceCommunicationException {
		try {
			LROEPF140IngresosConFacturaConSGAnulacionPeticion request = LROE140_1_1.buildBaja(context);
			LroeValidation.validateAnulacionConSG(request);
			byte[] requestBytes = XMLUtils.marshal(request, LROEPF140IngresosConFacturaConSGAnulacionPeticion.class);
			byte[] responseBytes = LROE140_1_1.sendCancel(context, requestBytes);
			saveCancel(ctx, context, requestBytes, responseBytes);
		} catch (InvoiceCommunicationException | JAXBException | IOException e) {
			e.printStackTrace();
			throw new InvoiceCommunicationException(e);
		}
			
		return context;
	}
	
	private static InvoiceCommunicatorContext saveCancel(AONContext ctx, InvoiceCommunicatorContext context, byte[] requestBytes, byte[] responseBytes) throws JAXBException {
		InvoiceBatch invoiceBatch = InvoiceCommunicationDAO.saveCancel(ctx, context.getDomain(), InvoiceCommunicationType.LROE, requestBytes, responseBytes);
		LROEPF140IngresosConFacturaConSGAnulacionRespuesta response = (LROEPF140IngresosConFacturaConSGAnulacionRespuesta)
				XMLUtils.unmarshal(responseBytes, LROEPF140IngresosConFacturaConSGAnulacionRespuesta.class);
		response.getRegistros().getRegistro();
		for (RegistroAnulacionFacturaConSGType reg : response.getRegistros().getRegistro()) {
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
