package net.aonsolutions.aon.verifactu;

import java.text.MessageFormat;
import java.util.GregorianCalendar;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.finance.FinanceUtil;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationPhaseListener;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.watson.error.AonCoreException;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CabeceraType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.EncadenamientoFacturaAnteriorType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.PersonaFisicaJuridicaESType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SiNoType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SistemaInformaticoType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegFactuSistemaFacturacion;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegistroFacturaType;

class Invoice2Verifactu {
	
	private static final String VFM = "VFM";
	static final String VERSION  = "1.0";
	static final String SERVICE_DESCRIPTION = "Prestacion de servicios";
	static final String NO_SERVICE_DESCRIPTION = "Venta de mercaderias";
	
	private Invoice2Verifactu() {
	
	}
	
	static RegFactuSistemaFacturacion build(AONContext ctx, InvoiceCommunicationType type, VerifactuContext vc, InvoiceCommunicationPhaseListener phase) throws InvoiceCommunicationException {
		try {
			RegFactuSistemaFacturacion regFactu = new RegFactuSistemaFacturacion();
			regFactu.setCabecera(getCabecera(vc));
			VerifactuValidation.validateHeader(regFactu);
			boolean phaseEnabled = phase != null && !vc.isAnnulment();
			AtomicInteger progress = new AtomicInteger(0);
			int count = vc.invoiceCount();
			vc.invoiceStream()
				.forEach(invoice -> {
					try {
						if (phaseEnabled) {
							phase.beforeInvoice(ctx, vc.getInvoiceCommunicatorContext(), invoice);
						}
						
						RegistroFacturaType factura = getFactura(vc, invoice);
						VerifactuValidation.validate(regFactu, factura, invoice);
						
						if (invoice.hasERRMessages()) {
							if (phaseEnabled) {
								phase.afterWrongInvoice(ctx, vc.getInvoiceCommunicatorContext(), invoice);
							}
						} else {
							if (phaseEnabled) {
								phase.afterRightInvoice(ctx, vc.getInvoiceCommunicatorContext(), invoice);
							}
							;
							vc.getLogger().progress(VFM
								, count
								, progress.incrementAndGet() 
								,MessageFormat.format("Agregando factura a lote de {0}: {1}",
									type.getDescription(),
									FinanceUtil.getDocumentNumber(invoice) ));
							regFactu.getRegistroFactura().add(factura);
							vc.setBlockchain( newBlockchain( factura) );
						}
					} catch (InvoiceCommunicationException e) {
						e.printStackTrace();
						throw new AonCoreException( e );
					}
			});
			if (phaseEnabled) {
				phase.afterAll(ctx, vc.getInvoiceCommunicatorContext());
			}
			return regFactu;
		} catch (AonCoreException e) {
			if (e.getCause() instanceof InvoiceCommunicationException ice) {
				throw ice;
			}
			throw e;
		}
	}
	
	
	private static VerifactuBlockchain newBlockchain(RegistroFacturaType factura) throws InvoiceCommunicationException {	
		if (factura.getRegistroAlta() != null) {
			return Invoice2VerifactuAlta.newBlockchain( factura.getRegistroAlta() );
		}
		if (factura.getRegistroAnulacion() != null) {
			return Invoice2VerifactuAnulacion.newBlockchain( factura.getRegistroAnulacion() );
		}
		throw new InvoiceCommunicationException( InvoiceCommunicationError.AON_0009 ); 
	}

	private static CabeceraType getCabecera(VerifactuContext vc) { 
		final CabeceraType c = new CabeceraType();
		PersonaFisicaJuridicaESType obligado = new PersonaFisicaJuridicaESType();
		obligado.setNIF(vc.getCompany().getDocument());
		obligado.setNombreRazon(vc.getCompany().getName());
		c.setObligadoEmision(obligado);
		
		// REPRESENTANTE/ASESOR SI LO TUVIERA 
		// c.getRepresentante().setNIF(null);
		// c.getRepresentante().setNombreRazon(null);		
		
		// REMISION VOLUNTARIA
		// c.getRemisionVoluntaria().setFechaFinVeriFactu("");
		// c.getRemisionVoluntaria().setIncidencia(IncidenciaType.S);
		
		// REMISION REQUERIMIENTO
		// c.getRemisionRequerimiento().setFinRequerimiento(null);
		// c.getRemisionRequerimiento().setRefRequerimiento("");	
		
		return c; 
	}
	
	private static RegistroFacturaType getFactura(VerifactuContext vc, Invoice invoice) throws InvoiceCommunicationException {
		RegistroFacturaType factura = new RegistroFacturaType();
		if (vc.isAnnulment()) {
			factura.setRegistroAnulacion( Invoice2VerifactuAnulacion.get(vc, invoice) );
		} else {
			factura.setRegistroAlta( Invoice2VerifactuAlta.get(vc, invoice) );
		}
		return factura;
	}
	
	static EncadenamientoFacturaAnteriorType getEncadenamientoFacturaAnterior(VerifactuBlockchain blockchain) {
		EncadenamientoFacturaAnteriorType cadena = new EncadenamientoFacturaAnteriorType();
		cadena.setFechaExpedicionFactura(blockchain.getDate());
		cadena.setHuella(blockchain.getHuella());
		cadena.setIDEmisorFactura(blockchain.getDocument());
		cadena.setNumSerieFactura(blockchain.getReference());
		return cadena;
	}


	static XMLGregorianCalendar getXmlDate() throws InvoiceCommunicationException {
		 try {
			 XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar( new GregorianCalendar());
			 xmlGregorianCalendar.setFractionalSecond(null);
			 return xmlGregorianCalendar;
		 } catch (DatatypeConfigurationException e) {
			 throw new InvoiceCommunicationException( e );
		}
	}
	
	static SistemaInformaticoType getSistemaInformatico(Company company) {
		SistemaInformaticoType sys = new SistemaInformaticoType();
		sys.setNIF("B01487271");
		sys.setNombreRazon("AON SOLUTIONS SL");
		sys.setIdSistemaInformatico("01");
		sys.setNombreSistemaInformatico("aonSolutions");
		sys.setVersion("9.23");
		sys.setNumeroInstalacion(company.getDocument() + "-" + company.getDomain().getId()); // CONCATENA EL NIF Y DOMAIN ID.
		sys.setTipoUsoPosibleSoloVerifactu(SiNoType.N);
		sys.setTipoUsoPosibleMultiOT(SiNoType.S);
		sys.setIndicadorMultiplesOT(SiNoType.S);
		return sys;		
	}
	
}

