package net.aonsolutions.aon.verifactu;

import java.util.Date;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.TaxBreakdown;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonObjectUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CuponType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.DesgloseType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.DetalleType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.IDFacturaExpedidaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.MacrodatoType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.PrimerRegistroCadenaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RechazoPrevioType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAltaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAltaType.Encadenamiento;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SubsanacionType;

class Invoice2VerifactuAlta {
	
	static final String SERVICE_DESCRIPTION = "Prestacion de servicios";
	static final String NO_SERVICE_DESCRIPTION = "Venta de mercaderias";
	
	private Invoice2VerifactuAlta() {
	
	}
	
	static RegistroFacturacionAltaType get(VerifactuContext vc, Invoice invoice) throws InvoiceCommunicationException {
		invoice.refreshTaxBreakdown();
		
		RegistroFacturacionAltaType alta = new RegistroFacturacionAltaType();
		
		alta.setIDVersion(Invoice2Verifactu.VERSION);
		
		// ID FACTURA
		IDFacturaExpedidaType idFactura = new IDFacturaExpedidaType();
		idFactura.setIDEmisorFactura(vc.getCompany().getDocument());
		idFactura.setNumSerieFactura(invoice.getReferenceCode());
		Date expDate = invoice.getExpDate() != null ? invoice.getExpDate() : new Date();
		idFactura.setFechaExpedicionFactura( VerifactuUtils.toString(expDate) );
		alta.setIDFactura(idFactura);
		
		// Referencia Externa InvoiceId
		alta.setRefExterna(AonNumberUtils.toString(invoice.getId()));
		
		alta.setNombreRazonEmisor(vc.getCompany().getName());
		alta.setSubsanacion(SubsanacionType.N);
		alta.setRechazoPrevio(RechazoPrevioType.N);
		
		ClaveTipoFactura.fill(vc, invoice, alta);

		alta.setFechaOperacion( VerifactuUtils.toString(invoice.getIssueDate() ));
		
		// ¡AVISO! SI DESCRIPCION OPERACION LLEVA TILDES DEVUELVE UN ERRROR!!
		String opDescription = invoice.isService() ? SERVICE_DESCRIPTION : NO_SERVICE_DESCRIPTION;
		alta.setDescripcionOperacion(opDescription);

		alta.setMacrodato(MacrodatoType.N);

		alta.setCupon(CuponType.N);

		alta.setCuotaTotal( VerifactuUtils.toString( invoice.getTaxBreakdown().map(b -> (b.getVatQuota(invoice) + b.getSurchargeQuota(invoice)) ).orElse(0.0)));
		
		alta.setImporteTotal(VerifactuUtils.toString(invoice.getGrossTotal(false)));
		
		alta.setDesglose(getDesglose(vc, invoice));

		alta.setEncadenamiento(getEncadenamientoAlta(vc.getBlockchain()));
		
		alta.setSistemaInformatico(Invoice2Verifactu.getSistemaInformatico(vc.getCompany()));
       
		alta.setFechaHoraHusoGenRegistro(Invoice2Verifactu.getXmlDate());
		alta.setNumRegistroAcuerdoFacturacion(null);
		alta.setIdAcuerdoSistemaInformatico(null);
		alta.setTipoHuella("01");
		alta.setHuella(calculateAltaHuella(alta, vc.getBlockchain()));
		return alta;
	}
	
	private static Encadenamiento getEncadenamientoAlta(VerifactuBlockchain blockchain) {
		Encadenamiento encadenamiento = new Encadenamiento();
		if(blockchain == null || blockchain.isEmpty()) 
			encadenamiento.setPrimerRegistro(PrimerRegistroCadenaType.S);
		else {
			encadenamiento.setRegistroAnterior(Invoice2Verifactu.getEncadenamientoFacturaAnterior(blockchain));
		}
		return encadenamiento;		
	}
	
	private static DesgloseType getDesglose(VerifactuContext vc, Invoice invoice) throws InvoiceCommunicationException {
		try {
			DesgloseType desglose = new DesgloseType();
			Optional<TaxBreakdown> optTb = invoice.getTaxBreakdown();
			if (optTb.isPresent()) {
				TaxBreakdown tb = optTb.get();
				if (AonCollectionUtils.isEmpty(tb.getVats())) {
					throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_9003);
				}
				for (InvoiceBreakdown ib : tb.getVats()) {
					DetalleType detalle = ClaveRegimen.get(vc, invoice, ib);
					desglose.getDetalleDesglose().add(detalle);
				}
			} else {
				throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_9003);
			}
			return desglose;
		} catch (AonCoreException e) {
			if ( e.getCause() != null && InvoiceCommunicationException.class.isAssignableFrom( e.getCause().getClass()) ) {
				throw (InvoiceCommunicationException) e.getCause(); 
			}
			throw new InvoiceCommunicationException( e );
		}
	}
	
	private static String calculateAltaHuella(RegistroFacturacionAltaType alta, VerifactuBlockchain previousBlockchain) {
		return new VerifactuAltaHuella()
			.setIDEmisorFactura(alta.getIDFactura().getIDEmisorFactura())
			.setNumSerieFactura(alta.getIDFactura().getNumSerieFactura())
			.setFechaExpedicionFactura(alta.getIDFactura().getFechaExpedicionFactura())
			.setTipoFactura(alta.getTipoFactura().value())
			.setCuotaTotal(alta.getCuotaTotal())
			.setImporteTotal(alta.getImporteTotal())
			.setPreviousHuella(AonStringUtils.defaultIfBlank(AonObjectUtils.ifNotNullGet(previousBlockchain, VerifactuBlockchain::getHuella )))
			.setFechaHoraHusoGenRegistro(alta.getFechaHoraHusoGenRegistro().toString())
			.digest();
	}

	public static VerifactuBlockchain newBlockchain(RegistroFacturacionAltaType registroAlta) {
		return new VerifactuBlockchain()
			.setDate(registroAlta.getIDFactura().getFechaExpedicionFactura())
			.setDocument(registroAlta.getIDFactura().getIDEmisorFactura())
			.setReference(registroAlta.getIDFactura().getNumSerieFactura())
			.setHuella(registroAlta.getHuella());
	}
	
}

