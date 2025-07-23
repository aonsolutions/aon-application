package net.aonsolutions.aon.verifactu;

import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.TaxBreakdown;
import com.esferalia.aon.occam.api.model.finance.VATExemptionCause;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.codec.AonDigestUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonObjectUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CabeceraType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CalificacionOperacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.ClaveTipoFacturaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.ClaveTipoRectificativaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CompletaSinDestinatarioType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CuponType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.DesgloseType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.DetalleType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.EncadenamientoFacturaAnteriorType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.IDFacturaARType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.IDFacturaExpedidaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.MacrodatoType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.OperacionExentaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.PersonaFisicaJuridicaESType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.PersonaFisicaJuridicaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.PrimerRegistroCadenaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RechazoPrevioType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAltaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAltaType.Destinatarios;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAltaType.Encadenamiento;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SiNoType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SimplificadaCualificadaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SistemaInformaticoType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SubsanacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegFactuSistemaFacturacion;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegistroFacturaType;
import net.aonsolutions.aon.verifactu.exceptions.VerifactuError;
import net.aonsolutions.aon.verifactu.exceptions.VerifactuException;

class Invoice2Verifactu {
	
	static final String DATE_FORMAT = "dd-MM-yyyy";
	static final String VERSION  = "1.0";
	static final String SERVICE_DESCRIPTION = "Prestación de servicios";
	static final String NO_SERVICE_DESCRIPTION = "Venta de mercaderías";
	
	private Invoice2Verifactu() {
	
	}
	
	static RegFactuSistemaFacturacion build(VerifactuContext vc) throws VerifactuException {
		RegFactuSistemaFacturacion verifactu = new RegFactuSistemaFacturacion();
		verifactu.setCabecera(getCabecera(vc));

		for (Invoice invoice : vc.getInvoices()) {
			RegistroFacturaType factura = getFactura(vc, invoice);
			verifactu.getRegistroFactura().add(factura);
			vc.setBlockchain(new VerifactuBlockchain()
				.setDate(factura.getRegistroAlta().getIDFactura().getFechaExpedicionFactura())
				.setDocument(factura.getRegistroAlta().getIDFactura().getIDEmisorFactura())
				.setReference(factura.getRegistroAlta().getIDFactura().getNumSerieFactura())
				.setHuella(factura.getRegistroAlta().getHuella())
			);			
		}

		return verifactu;
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
	
	private static RegistroFacturaType getFactura(VerifactuContext vc, Invoice invoice) throws VerifactuException {
		RegistroFacturaType factura = new RegistroFacturaType();
		RegistroFacturacionAltaType alta = new RegistroFacturacionAltaType();
		
		alta.setIDVersion(VERSION);
		
		// ID FACTURA
		IDFacturaExpedidaType idFactura = new IDFacturaExpedidaType();
		idFactura.setIDEmisorFactura(vc.getCompany().getDocument());
		idFactura.setNumSerieFactura(invoice.getReferenceCode());
		idFactura.setFechaExpedicionFactura( dateToString(invoice.getExpDate()) );
		alta.setIDFactura(idFactura);
		
		// Referencia Externa InvoiceId
		alta.setRefExterna(AonNumberUtils.toString(invoice.getId()));
		
		alta.setNombreRazonEmisor(vc.getCompany().getName());
		alta.setSubsanacion(SubsanacionType.N);
		alta.setRechazoPrevio(RechazoPrevioType.N);
		alta.setTipoFactura(invoice.isSimplified() ? ClaveTipoFacturaType.F_2 : ClaveTipoFacturaType.F_1);
		
		if(invoice.isRectifier()) {
			alta.setTipoFactura(invoice.isSimplified() ? ClaveTipoFacturaType.R_5 : ClaveTipoFacturaType.R_1);
			alta.setTipoRectificativa(ClaveTipoRectificativaType.I);// por diferencia (I) o por sustitucion (S)

			IDFacturaARType rectified = new IDFacturaARType();
			rectified.setIDEmisorFactura(vc.getCompany().getDocument());
			rectified.setNumSerieFactura(invoice.getRectificationInvoiceReference());
			rectified.setFechaExpedicionFactura( dateToString(invoice.getRectificationInvoiceDate()));
			
			alta.getFacturasRectificadas().getIDFacturaRectificada().add(rectified);
			// SI FUERA FACTURA RECTIFICATIVO POR SUSTITUCIÓN.
			// alta.getFacturasSustituidas().getIDFacturaSustituida().add(rectified);
			
			// ????????????????????????????????
			alta.getImporteRectificacion().setBaseRectificada(null);
			alta.getImporteRectificacion().setCuotaRecargoRectificado(null);
			alta.getImporteRectificacion().setCuotaRectificada(null);			
		}

		alta.setFechaOperacion( dateToString(invoice.getIssueDate() ));
		
		String opDescription = invoice.isService() ? SERVICE_DESCRIPTION : NO_SERVICE_DESCRIPTION;
		alta.setDescripcionOperacion(opDescription);
		
		alta.setFacturaSimplificadaArt7273(SimplificadaCualificadaType.N);

		alta.setFacturaSinIdentifDestinatarioArt61D(invoice.isSimplified() ?  CompletaSinDestinatarioType.S : CompletaSinDestinatarioType.N);

		alta.setMacrodato(MacrodatoType.N);

		// Facturas emitidas por terceros. AUTOFACTURA!
		// if(invoice.isThirdPart()) { 
		//	alta.getTercero().setIDOtro(new IDOtroType());
		//	alta.getTercero().setNIF("");
		//	alta.getTercero().setNombreRazon("");
		// }

		PersonaFisicaJuridicaType destinatario = new PersonaFisicaJuridicaType();
		// destinatario.setIDOtro(new IDOtroType());
		destinatario.setNIF(invoice.getRegistryDocument());
		destinatario.setNombreRazon(invoice.getRegistryName());
		
		Destinatarios destinatarios = new Destinatarios();
		destinatarios.getIDDestinatario().add(destinatario);
		alta.setDestinatarios(destinatarios);
		
		alta.setCupon(CuponType.N);

		alta.setCuotaTotal( doubleToString( invoice.getTaxBreakdown().map(b -> b.getVatQuota()).orElse(0.0)));
		alta.setImporteTotal(doubleToString(invoice.getGrossTotal()));
		
		alta.setDesglose(getDesglose(vc, invoice, invoice.getGrossTotal()));

		alta.setEncadenamiento(getEncadenamiento(vc.getBlockchain()));
		
		alta.setSistemaInformatico(getSistemaInformatico(vc.getCompany()));
       
		alta.setFechaHoraHusoGenRegistro(getXmlDate());
		alta.setNumRegistroAcuerdoFacturacion(null);
		alta.setIdAcuerdoSistemaInformatico(null);
		alta.setTipoHuella("01");
		alta.setHuella(calculateHuella(alta, vc.getBlockchain()));
		
		
		factura.setRegistroAlta(alta); //getSignedAlta(verifactuConfiguration, alta));
		return factura;
	}
	
//	private static RegistroFacturacionAltaType getSignedAlta(VerifactuConfiguration verifactuConfiguration, RegistroFacturacionAltaType alta) {
//		try {
//			byte[] data = XMLUtils.marshal(alta, RegistroFacturacionAltaType.class);
//			byte[] xml = VerifactuSigner.getInstance().sign(verifactuConfiguration, data);
//			return (RegistroFacturacionAltaType) XMLUtils.unmarshal(xml, RegistroFacturacionAltaType.class);
//		} catch (AonSignerException | JAXBException e) {
//			e.printStackTrace();
//		}
//		return alta;
//	}
	
	private static Encadenamiento getEncadenamiento(VerifactuBlockchain blockchain) {
		Encadenamiento encadenamiento = new Encadenamiento();
		if(blockchain == null || blockchain.isEmpty()) 
			encadenamiento.setPrimerRegistro(PrimerRegistroCadenaType.S);
		else {
			EncadenamientoFacturaAnteriorType cadena = new EncadenamientoFacturaAnteriorType();
			cadena.setFechaExpedicionFactura(blockchain.getDate());
			cadena.setHuella(blockchain.getHuella());
			cadena.setIDEmisorFactura(blockchain.getDocument());
			cadena.setNumSerieFactura(blockchain.getReference());
			encadenamiento.setRegistroAnterior(cadena);
		}
		return encadenamiento;		
	}
	
	private static String calculateHuella(RegistroFacturacionAltaType alta, VerifactuBlockchain previousBlockchain) {
		String previousHuella = AonStringUtils.defaultIfBlank(AonObjectUtils.ifNotNullGet(previousBlockchain, VerifactuBlockchain::getHuella ));
		String huella = "IDEmisorFactura=" + alta.getIDFactura().getIDEmisorFactura() 
			+ "&NumSerieFactura=" + alta.getIDFactura().getNumSerieFactura()
			+ "&FechaExpedicionFactura=" + alta.getIDFactura().getFechaExpedicionFactura()
			+ "&TipoFactura=" + alta.getTipoFactura().value()
			+ "&CuotaTotal=" + alta.getCuotaTotal()
			+ "&ImporteTotal=" + alta.getImporteTotal()
			+ "&Huella=" + previousHuella
			+ "&FechaHoraHusoGenRegistro=" + alta.getFechaHoraHusoGenRegistro();
		huella = AonDigestUtils.sha256Hex(huella);
		return huella.toUpperCase();	
	}
	
	private static XMLGregorianCalendar getXmlDate() {
		 try {
			 XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar( new GregorianCalendar());
			 xmlGregorianCalendar.setFractionalSecond(null);
			 return xmlGregorianCalendar;
		 } catch (Exception e) {
			 e.printStackTrace();
		}
		return null;
	}
	
	static String doubleToString(double d) {
		String ds = AonNumberUtils.toString( AonMathUtils.round(d) );
		return new BigDecimal(ds).stripTrailingZeros().toPlainString();
	}
	static String dateToString(Date d) {
		return AonDateUtils.format(d, DATE_FORMAT );
	}
	
	private static DesgloseType getDesglose(VerifactuContext vc, Invoice invoice, double total) throws VerifactuException {
		try {
			DesgloseType desglose = new DesgloseType();
			Optional<TaxBreakdown> optTb = invoice.getTaxBreakdown();
			if (optTb.isPresent()) {
				TaxBreakdown tb = optTb.get();
				if (AonCollectionUtils.isEmpty(tb.getVats())) {
					throw new VerifactuException(VerifactuError.AON_9003);
				}
				for (InvoiceBreakdown ib : tb.getVats()) {
					DetalleType detalle = ClaveRegimen.get(vc, invoice, ib);
					desglose.getDetalleDesglose().add(detalle);
				}
			} else {
				throw new VerifactuException(VerifactuError.AON_9003);
			}
			return desglose;
		} catch (AonCoreException e) {
			if ( e.getCause() != null && VerifactuException.class.isAssignableFrom( e.getCause().getClass()) ) {
				throw (VerifactuException) e.getCause(); 
			}
			throw new VerifactuException( e );
		}
	}

	private static DesgloseType _getDesglose(Invoice invoice, double total) {
		DesgloseType desglose = new DesgloseType();		
		if(invoice.isIntracommunity() && invoice.isService()) { // NO SUJETA - INTRACOMUNITARIO Y PRESTACIÓN DE SERVICIOS
			DetalleType detalle = new DetalleType();
			detalle.setClaveRegimen("01");
			detalle.setBaseImponibleOimporteNoSujeto(doubleToString(total));
			detalle.setCalificacionOperacion(CalificacionOperacionType.N_2);
			desglose.getDetalleDesglose().add(detalle);
		} else {
			boolean exempt = invoice.getActivity().getVatRegime().isExempt() || invoice.isIntracommunity() 
					|| invoice.isExtracommunity() || invoice.isCanCeuMel();
			
//			invoice.getBreakdown().stream().filter(f -> TaxType.VAT.equals(f.getTaxType()) 
//					&& ((!exempt && f.getPercentage() > 0) || (exempt && f.getPercentage() > 0) || invoice.isIsp())).forEach(r -> {
//				if(r.getPercentage() > 0 && r.getQuota() == 0.0) {
//					r.setQuota(AonMathUtils.round(r.getBase() * r.getPercentage() / 100));
//				}
//			
//				if(r.getSurcharge() > 0 && r.getSurchargeQuota() == 0.0) {
//					r.setSurchargeQuota(AonMathUtils.round(r.getBase() * r.getSurcharge() / 100));
//				}
//				
//				DetalleType detalle = new DetalleType();
//				detalle.setClaveRegimen("01");
//				detalle.setBaseImponibleOimporteNoSujeto(doubleToString(r.getBase()));
//				detalle.setTipoImpositivo(invoice.isIsp() ? "0.0" : doubleToString(r.getPercentage()));
//				detalle.setCuotaRepercutida(invoice.isIsp() ? "0.0" : doubleToString(r.getQuota()));
//				if(!invoice.isIsp() && !"0.0".equals(detalle.getTipoImpositivo()) && r.getSurcharge() > 0.0) {
//					detalle.setTipoRecargoEquivalencia(doubleToString(r.getSurcharge()));
//					detalle.setCuotaRecargoEquivalencia(doubleToString(r.getSurchargeQuota()));
//				}
//
//				detalle.setCalificacionOperacion(invoice.isIsp() ? CalificacionOperacionType.S_2 : CalificacionOperacionType.S_1);
//
//				if(r.getBase() != 0.0)
//					desglose.getDetalleDesglose().add(detalle);
//			});
			
			invoice.getBreakdown().stream().filter(f -> TaxType.VAT.equals(f.getTaxType()) 
					&&  exempt && f.getPercentage() == 0 && !invoice.isIsp()).forEach(r -> {
				DetalleType detalle = new DetalleType();
				detalle.setClaveRegimen("01");
				detalle.setBaseImponibleOimporteNoSujeto(doubleToString(r.getBase()));
						
				detalle.setOperacionExenta(OperacionExentaType.E_6);
				if(invoice.getActivity().getVatExemptionCause() != null) {
					VATExemptionCause cause = invoice.getActivity().getVatExemptionCause();
					detalle.setOperacionExenta(OperacionExentaType.fromValue(cause.name()));
				}
			
				if(invoice.isNational()) {
					detalle.setOperacionExenta(OperacionExentaType.E_1);
				} else if(invoice.isIntracommunity()) {
					detalle.setOperacionExenta(OperacionExentaType.E_5);
				} else if(invoice.isExtracommunity() || invoice.isCanCeuMel()) {
					detalle.setOperacionExenta(OperacionExentaType.E_2);
				}
				desglose.getDetalleDesglose().add(detalle);
			});
						
			Double totalSuplidos = invoice.getDetails().stream()
				.filter(f -> f.isPrepayment() || f.getInvoiceTaxes().isEmpty())
				.mapToDouble(r -> r.getQuantity() * r.getPrice()).sum();
			if(totalSuplidos != 0) {
				DetalleType detalle = new DetalleType();
				detalle.setClaveRegimen("01");
				detalle.setBaseImponibleOimporteNoSujeto(doubleToString(totalSuplidos));
				detalle.setCalificacionOperacion(CalificacionOperacionType.N_1);
				desglose.getDetalleDesglose().add(detalle);
			}
			
			double noSujetaOtros = invoice.getBreakdown().stream().filter(f -> TaxType.VAT.equals(f.getTaxType()) 
						&&  !exempt && f.getPercentage() == 0 && !invoice.isIsp())
				.mapToDouble(InvoiceBreakdown::getBase).sum();
			if(noSujetaOtros != 0) {
				DetalleType detalle = new DetalleType();
				detalle.setClaveRegimen("01");
				detalle.setBaseImponibleOimporteNoSujeto(doubleToString(noSujetaOtros));
				detalle.setCalificacionOperacion(CalificacionOperacionType.N_1);
				desglose.getDetalleDesglose().add(detalle);
			}
		}
				
		return desglose;
	}
	
	private static SistemaInformaticoType getSistemaInformatico(Company company) {
		SistemaInformaticoType sys = new SistemaInformaticoType();
		sys.setNIF("B01487271");
		sys.setNombreRazon("AON SOLUTIONS SL");
		sys.setIdSistemaInformatico("01"); // ???
		sys.setNombreSistemaInformatico("aonSolutions");
		sys.setVersion("9.23");
		sys.setNumeroInstalacion(company.getDocument() + "-" + company.getDomain().getId()); // CONCATENA EL NIF Y DOMAIN ID.
		sys.setTipoUsoPosibleSoloVerifactu(SiNoType.N);
		sys.setTipoUsoPosibleMultiOT(SiNoType.S);
		sys.setIndicadorMultiplesOT(SiNoType.S);
		return sys;		
	}
	
	static enum TipoImpuesto {
		IVA("01"), // Impuesto sobre el Valor Añadido (IVA).
		IPSI("02"), // Impuesto sobre la Producción, los Servicios y la Importación (IPSI) de Ceuta y Melilla.
		IGIC("03"), // Impuesto General Indirecto Canario (IGIC).
		OTRO("05")  // Otros.
		;

		private String value;
		private TipoImpuesto(String value) {
			this.value = value; 
		}
		
		public String getValue() {
			return value;
		}
	}
	
}

