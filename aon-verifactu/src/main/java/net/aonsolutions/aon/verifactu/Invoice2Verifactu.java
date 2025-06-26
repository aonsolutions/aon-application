package net.aonsolutions.aon.verifactu;

import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.VATExemptionCause;
import com.esferalia.aon.occam.api.model.finance.VerifactuConfiguration;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.codec.AonDigestUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

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
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.IDOtroType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.MacrodatoType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.OperacionExentaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.PersonaFisicaJuridicaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.PrimerRegistroCadenaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RechazoPrevioType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAltaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAltaType.Encadenamiento;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SiNoType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SimplificadaCualificadaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SistemaInformaticoType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SubsanacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegFactuSistemaFacturacion;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegistroFacturaType;
import net.aonsolutions.aon.sign.VerifactuSigner;
import net.aonsolutions.aon.sign.exception.AonSignerException;
import net.aonsolutions.aon.verifactu.utils.XMLUtils;

public class Invoice2Verifactu {

	private Invoice2Verifactu() {
	
	}
	
	public static RegFactuSistemaFacturacion build(VerifactuConfiguration verifactuConfiguration, Company company, List<Invoice> invoices, VerifactuBlockchain blockchain) {
		RegFactuSistemaFacturacion verifactu = new RegFactuSistemaFacturacion();
		verifactu.setCabecera(getCabecera(company));

		for (Invoice invoice : invoices) {
			RegistroFacturaType factura = getFactura(verifactuConfiguration, company, invoice, blockchain);
			verifactu.getRegistroFactura().add(factura);
			blockchain = new VerifactuBlockchain()
					.setDate(factura.getRegistroAlta().getIDFactura().getFechaExpedicionFactura())
					.setDocument(factura.getRegistroAlta().getIDFactura().getIDEmisorFactura())
					.setReference(factura.getRegistroAlta().getIDFactura().getNumSerieFactura())
					.setHuella(factura.getRegistroAlta().getHuella());			
		}

		return verifactu;
	}
	
	private static CabeceraType getCabecera(Company company) { 
		final CabeceraType c = new CabeceraType();
		c.getObligadoEmision().setNIF(company.getDocument());
		c.getObligadoEmision().setNombreRazon(company.getName());
		
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
	
	private static RegistroFacturaType getFactura(VerifactuConfiguration verifactuConfiguration, Company company, Invoice invoice, VerifactuBlockchain blockchain) {
		RegistroFacturaType factura = new RegistroFacturaType();
		RegistroFacturacionAltaType alta = new RegistroFacturacionAltaType();
		
		alta.setIDVersion("1.0");
		
		// ID FACTURA
		Date expDate = invoice.ensureFiscal().getExpDate() != null
				? invoice.getFiscal().getExpDate()
				: invoice.getIssueDate();
		
		alta.getIDFactura().setIDEmisorFactura(company.getDocument());
		alta.getIDFactura().setNumSerieFactura(invoice.getReferenceCode());
		alta.getIDFactura().setFechaExpedicionFactura(AonDateUtils.format(expDate, "dd-MM-yyyy"));

		// Referencia Externa ??
		alta.setRefExterna(invoice.getId().toString());
		
		alta.setNombreRazonEmisor(company.getName());

		alta.setSubsanacion(SubsanacionType.N);
		
		alta.setRechazoPrevio(RechazoPrevioType.N);
		
		alta.setTipoFactura(invoice.isSimplified() ? ClaveTipoFacturaType.F_2 : ClaveTipoFacturaType.F_1);
		
		if(invoice.isRectifier()) {
			alta.setTipoFactura(invoice.isSimplified() ? ClaveTipoFacturaType.R_5 : ClaveTipoFacturaType.R_1);
			alta.setTipoRectificativa(ClaveTipoRectificativaType.I);// por diferencia (I) o por sustitucion (S)

			IDFacturaARType rectified = new IDFacturaARType();
			rectified.setIDEmisorFactura(company.getDocument());
			rectified.setNumSerieFactura(invoice.getRectificationInvoiceReference());
			rectified.setFechaExpedicionFactura(AonDateUtils.format(invoice.getRectificationInvoiceDate(), "dd-MM-yyyy"));
			
			alta.getFacturasRectificadas().getIDFacturaRectificada().add(rectified);
			// SI FUERA FACTURA RECTIFICATIVO POR SUSTITUCIÓN.
			// alta.getFacturasSustituidas().getIDFacturaSustituida().add(rectified);
			
			// ????????????????????????????????
			alta.getImporteRectificacion().setBaseRectificada(null);
			alta.getImporteRectificacion().setCuotaRecargoRectificado(null);
			alta.getImporteRectificacion().setCuotaRectificada(null);			
		}

		alta.setFechaOperacion(AonDateUtils.format(invoice.getIssueDate(), "dd-MM-yyyy"));
		
		alta.setDescripcionOperacion("DESCRIPCIÓN OPERACIÓN");
		
		alta.setFacturaSimplificadaArt7273(SimplificadaCualificadaType.N);

		alta.setFacturaSinIdentifDestinatarioArt61D(invoice.isSimplified() ?  CompletaSinDestinatarioType.S : CompletaSinDestinatarioType.N);

		// ??????????????????????
		alta.setMacrodato(MacrodatoType.N);

		// Facturas emitidas por terceros.
		if(invoice.isThirdPart()) { 
			alta.getTercero().setIDOtro(new IDOtroType());
			alta.getTercero().setNIF("");
			alta.getTercero().setNombreRazon("");
		}

		PersonaFisicaJuridicaType destinatario = new PersonaFisicaJuridicaType();
		destinatario.setIDOtro(new IDOtroType());
		destinatario.setNIF("");
		destinatario.setNombreRazon("");
		alta.getDestinatarios().getIDDestinatario().add(destinatario);
		
		// ?????????????????????
		alta.setCupon(CuponType.N);

		Double total = AonMathUtils.round(invoice.getTotal());
		if(invoice.isWithholding()) {
			double ret = invoice.getBreakdown().stream().filter(f -> TaxType.RETENTION.equals(f.getTaxType()))
				.mapToDouble(InvoiceBreakdown::getQuota).sum();
			total = AonMathUtils.round(total + ret);
		} 
		alta.setCuotaTotal(getCuotaTotal(invoice));
		
		alta.setImporteTotal(doubleToString(invoice.getTotal()));
		
		alta.setDesglose(getDesglose(invoice, total));

		alta.setEncadenamiento(getEncadenamiento(blockchain));
		
		alta.setSistemaInformatico(getSistemaInformatico());
       
		alta.setFechaHoraHusoGenRegistro(getXmlDate());
		alta.setNumRegistroAcuerdoFacturacion(null);
		alta.setIdAcuerdoSistemaInformatico(null);
		alta.setTipoHuella("01");
		alta.setHuella(calculateHuella(alta, blockchain.getHuella()));
		
		
		factura.setRegistroAlta(getSignedAlta(verifactuConfiguration, alta));
		return factura;
	}
	
	private static RegistroFacturacionAltaType getSignedAlta(VerifactuConfiguration verifactuConfiguration, RegistroFacturacionAltaType alta) {
		try {
			byte[] data = XMLUtils.marshal(alta, RegistroFacturacionAltaType.class);
			byte[] xml = VerifactuSigner.getInstance().sign(verifactuConfiguration, data);
			return (RegistroFacturacionAltaType) XMLUtils.unmarshal(xml, RegistroFacturacionAltaType.class);
		} catch (AonSignerException | JAXBException e) {
			e.printStackTrace();
		}
		return alta;
	}
	
	private static String getCuotaTotal(Invoice invoice) {
		double quota = invoice.getVatQuota();
		if(quota == 0.0) {
			
		}
		return doubleToString(quota);
	}
	
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
	
	private static String calculateHuella(RegistroFacturacionAltaType alta, String huellaAnterior) {
		String huella = "IDEmisorFactura=" + alta.getIDFactura().getIDEmisorFactura() 
				+ "&NumSerieFactura=" + alta.getIDFactura().getNumSerieFactura()
				+ "&FechaExpedicionFactura=" + alta.getIDFactura().getFechaExpedicionFactura()
				+ "&TipoFactura=" + alta.getTipoFactura().name()
				+ "&CuotaTotal=" + alta.getCuotaTotal()
				+ "&ImporteTotal=" + alta.getImporteTotal()
				+ "&Huella=" + huellaAnterior
				+ "&FechaHoraHusoGenRegistro=" + alta.getFechaHoraHusoGenRegistro();
		
		return AonDigestUtils.sha256Hex(huella);
	}
	
	private static XMLGregorianCalendar getXmlDate() {
		 try {
	        return DatatypeFactory.newInstance().newXMLGregorianCalendar( new GregorianCalendar());
		 } catch (Exception e) {
			 e.printStackTrace();
		}
		return null;
	}
	
	private static DesgloseType getDesglose(Invoice invoice, double total) {
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
			
			invoice.getBreakdown().stream().filter(f -> TaxType.VAT.equals(f.getTaxType()) 
					&& ((!exempt && f.getPercentage() > 0) || (exempt && f.getPercentage() > 0) || invoice.isIsp())).forEach(r -> {
				if(r.getPercentage() > 0 && r.getQuota() == 0.0) {
					r.setQuota(AonMathUtils.round(r.getBase() * r.getPercentage() / 100));
				}
			
				if(r.getSurcharge() > 0 && r.getSurchargeQuota() == 0.0) {
					r.setSurchargeQuota(AonMathUtils.round(r.getBase() * r.getSurcharge() / 100));
				}
				
				DetalleType detalle = new DetalleType();
				detalle.setClaveRegimen("01");
				detalle.setBaseImponibleOimporteNoSujeto(doubleToString(AonMathUtils.round(r.getBase())));
				detalle.setTipoImpositivo(invoice.isIsp() ? "0.0" : doubleToString(r.getPercentage()));
				detalle.setCuotaRepercutida(invoice.isIsp() ? "0.0" : doubleToString(AonMathUtils.round(r.getQuota())));
				detalle.setTipoRecargoEquivalencia(invoice.isIsp() || "0.0".equals(detalle.getTipoImpositivo())
					? "0.0" : doubleToString(AonMathUtils.round(r.getSurcharge())));
				detalle.setCuotaRecargoEquivalencia(invoice.isIsp() ? "0.0" : doubleToString(AonMathUtils.round(r.getSurchargeQuota())));
				detalle.setCalificacionOperacion(invoice.isIsp() ? CalificacionOperacionType.S_2 : CalificacionOperacionType.S_1);

				if(r.getBase() != 0.0)
					desglose.getDetalleDesglose().add(detalle);
			});
			
			invoice.getBreakdown().stream().filter(f -> TaxType.VAT.equals(f.getTaxType()) 
					&&  exempt && f.getPercentage() == 0 && !invoice.isIsp()).forEach(r -> {
				DetalleType detalle = new DetalleType();
				detalle.setClaveRegimen("01");
				detalle.setBaseImponibleOimporteNoSujeto(doubleToString(AonMathUtils.round(r.getBase())));
						
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
				detalle.setBaseImponibleOimporteNoSujeto(doubleToString(AonMathUtils.round(noSujetaOtros)));
				detalle.setCalificacionOperacion(CalificacionOperacionType.N_1);
				desglose.getDetalleDesglose().add(detalle);
			}
		}
				
		return desglose;
	}
	
	private static SistemaInformaticoType getSistemaInformatico() {
		SistemaInformaticoType sys = new SistemaInformaticoType();
		sys.setNIF("B01487271");
		sys.setNombreRazon("AON SOLUTIONS SL");
		sys.setIdSistemaInformatico(""); // ???
		sys.setNombreSistemaInformatico("aonSolutions");
		sys.setVersion("9.23");
		sys.setNumeroInstalacion(""); //???
		sys.setTipoUsoPosibleSoloVerifactu(SiNoType.N);
		sys.setTipoUsoPosibleMultiOT(SiNoType.S);
		sys.setIndicadorMultiplesOT(SiNoType.S);
		return sys;		
	}
	
	private static String doubleToString(double d) {
		return new BigDecimal(Double.toString(d)).stripTrailingZeros().toPlainString();
	}

}
