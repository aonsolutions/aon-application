package net.aonsolutions.aon.tbai;

import java.util.Date;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.tbai.lroe.IDType;
import ticketbai.anulacion.AnulaTicketBai;
import ticketbai.anulacion.IDFactura;
import ticketbai.emision.Cabecera;
import ticketbai.emision.CabeceraFacturaType;
import ticketbai.emision.CausaExencionType;
import ticketbai.emision.ClaveTipoFacturaType;
import ticketbai.emision.ClaveTipoRectificativaType;
import ticketbai.emision.ClavesType;
import ticketbai.emision.CountryType2;
import ticketbai.emision.DatosFacturaType;
import ticketbai.emision.DesgloseFacturaType;
import ticketbai.emision.DesgloseIVAType;
import ticketbai.emision.DesgloseTipoOperacionType;
import ticketbai.emision.Destinatarios;
import ticketbai.emision.DetalleExentaType;
import ticketbai.emision.DetalleIVAType;
import ticketbai.emision.DetalleNoExentaType;
import ticketbai.emision.DetallesFacturaType;
import ticketbai.emision.Emisor;
import ticketbai.emision.EmitidaPorTercerosType;
import ticketbai.emision.EncadenamientoFacturaAnteriorType;
import ticketbai.emision.EntidadDesarrolladoraType;
import ticketbai.emision.Entrega;
import ticketbai.emision.ExentaType;
import ticketbai.emision.Factura;
import ticketbai.emision.FacturaRectificativaType;
import ticketbai.emision.FacturasRectificadasSustituidasType;
import ticketbai.emision.HuellaTBAI;
import ticketbai.emision.IDClaveType;
import ticketbai.emision.IDDestinatario;
import ticketbai.emision.IDDetalleFacturaType;
import ticketbai.emision.IDFacturaRectificadaSustituidaType;
import ticketbai.emision.IDOtro;
import ticketbai.emision.NoExentaType;
import ticketbai.emision.PrestacionServicios;
import ticketbai.emision.SiNoType;
import ticketbai.emision.SoftwareFacturacionType;
import ticketbai.emision.SujetaType;
import ticketbai.emision.Sujetos;
import ticketbai.emision.TicketBai;
import ticketbai.emision.TipoDesgloseType;
import ticketbai.emision.TipoOperacionSujetaNoExentaType;
import ticketbai.zuzendu_alta.AccionType;
import ticketbai.zuzendu_alta.SubsanacionModificacionTicketBAI;

public class Invoice2tbai {

	private Invoice2tbai() {
	
	}
	
	private static final String TBAI_VERSION = "1.2";
	private static final String DEVICE_NUMBER = "TBAIGI447FC22512252C";
	private static final String DEVICE_NUMBER_GIPUZKOA_TEST = "TBAIGIPRE00000000131";
	private static final String DEVICE_NUMBER_ARABA_TEST = "TBAIARbjlCHFMFK00416";
	private static final String SOFTWARE_NAME = "aonSolutions";
	private static final String SOFTWARE_VERSION = "9.23" ;

	private static final String DEVICE_NUMBER_BIZKAIA_TEST = "TBAIBI00000000PRUEBA";
	private static final String NIF_BIZKAIA_TEST = "A99800005";
	private static final String SOFTWARE_NAME_BIZKAIA_TEST = "SOFTWARE GARANTE TICKETBAI PRUEBA";
	private static final String SOFTWARE_VERSION_BIZKAIA_TEST = "1.0";
	
//	private final static String TEST_NIF_140 = "99980200M";
//	private final static String TEST_NAME_140 = "8FVCxNbMNm"; 
//	private final static String TEST_SURNAME1_140 = "Vux9anjAES"; 
//	private final static String TEST_SURNAME2_140 = "EMPTmw3fmi";
//
//	private final static String TEST_NIF_240 = "A99802019";
//	private final static String TEST_NAME_240 = "4wbLGzaHUvHzMkJm9Z5knRPBKpLKr7"; 
	
	public static TicketBai build(Company company, Invoice invoice, TbaiConfiguration config, TbaiBlockchain blockchain) {
		TicketBai tbai = new TicketBai();
		tbai.setCabecera(getCabecera());
		tbai.setSujetos(getSujetos(company, invoice, config));
		tbai.setFactura(getFactura(invoice));
		tbai.setHuellaTBAI(getHuella(config, blockchain));
		return tbai;
	}
	
	public static SubsanacionModificacionTicketBAI buildZuzendu(Company company, Invoice invoice, TbaiConfiguration config, TbaiBlockchain blockchain) {
		SubsanacionModificacionTicketBAI tbai = new SubsanacionModificacionTicketBAI();
		tbai.setCabecera(buildCabeceraZuzendu(AccionType.MODIFICAR));
		tbai.setSujetos(getSujetos(company, invoice, config));
		tbai.setFactura(getFactura(invoice));
		tbai.setHuellaTBAI(getHuella(config, blockchain));
		return tbai;
	}
	
	public static AnulaTicketBai buildBaja(Company company, Invoice invoice, TbaiConfiguration config) {
		AnulaTicketBai tbai = new AnulaTicketBai();
		tbai.setCabecera(getCabeceraAnulacion());
		tbai.setIDFactura(getFacturaAnulacion(company, invoice));
		tbai.setHuellaTBAI(getHuellaAnulacion(config));
		return tbai;
	}
	
	private static Cabecera getCabecera() { 
		final Cabecera c = new Cabecera();
		c.setIDVersionTBAI(TBAI_VERSION);
		
		return c; 
	}
	
	private static ticketbai.zuzendu_alta.Cabecera buildCabeceraZuzendu(AccionType actionType) { 
		final ticketbai.zuzendu_alta.Cabecera c = new ticketbai.zuzendu_alta.Cabecera();
		c.setIDVersion(TBAI_VERSION);
		c.setAccion(actionType != null ? actionType : AccionType.MODIFICAR);
		return c; 
	}
		
	private static ticketbai.anulacion.Cabecera getCabeceraAnulacion() { 
		final ticketbai.anulacion.Cabecera c = new ticketbai.anulacion.Cabecera();
		c.setIDVersionTBAI(TBAI_VERSION);
		return c; 
	}
	
	private static IDFactura getFacturaAnulacion(Company company, Invoice invoice) {
		IDFactura factura = new IDFactura();
		factura.setCabeceraFactura(buildCabeceraInvoiceAnulacion(invoice));
		factura.setEmisor(buildEmisorInvoiceAnulacion(company));
		return factura;
	}
	
	private static ticketbai.anulacion.CabeceraFacturaType buildCabeceraInvoiceAnulacion(Invoice invoice) {
		ticketbai.anulacion.CabeceraFacturaType cabecera = new ticketbai.anulacion.CabeceraFacturaType();
		if(!AonStringUtils.isBlank(invoice.getSeries()))
			cabecera.setSerieFactura(invoice.getSeries());
		cabecera.setNumFactura(Integer.toString(invoice.getNumber()));
		cabecera.setFechaExpedicionFactura(AonDateUtils.format(invoice.getFiscal().getExpDate(), "dd-MM-yyyy"));
		return cabecera;
	}
	
	private static ticketbai.anulacion.Emisor buildEmisorInvoiceAnulacion(Company company) {
		ticketbai.anulacion.Emisor emisor = new ticketbai.anulacion.Emisor();
		emisor.setApellidosNombreRazonSocial(company.getName());
		emisor.setNIF(company.getDocument().replace(" ", ""));
		return emisor;
	}

	private static ticketbai.anulacion.HuellaTBAI getHuellaAnulacion(TbaiConfiguration tbai) {
		ticketbai.anulacion.HuellaTBAI huella = new ticketbai.anulacion.HuellaTBAI();

		ticketbai.anulacion.SoftwareFacturacionType software = new ticketbai.anulacion.SoftwareFacturacionType();
		ticketbai.anulacion.EntidadDesarrolladoraType entidad = new ticketbai.anulacion.EntidadDesarrolladoraType();
		entidad.setNIF("B01487271");
		software.setEntidadDesarrolladora(entidad);
		software.setLicenciaTBAI(DEVICE_NUMBER);
		software.setNombre(SOFTWARE_NAME);
		software.setVersion(SOFTWARE_VERSION);
		if(tbai.isAraba() && tbai.isTest()) {
			software.setLicenciaTBAI(DEVICE_NUMBER_ARABA_TEST);
		} else if(tbai.isGipuzkoa() && tbai.isTest()) {
			software.setLicenciaTBAI(DEVICE_NUMBER_GIPUZKOA_TEST);
		} else if(tbai.isBizkaia() && tbai.isTest()) {
			entidad = new ticketbai.anulacion.EntidadDesarrolladoraType();
			entidad.setNIF(NIF_BIZKAIA_TEST);
			software.setEntidadDesarrolladora(entidad);
			software.setLicenciaTBAI(DEVICE_NUMBER_BIZKAIA_TEST);
			software.setNombre(SOFTWARE_NAME_BIZKAIA_TEST);
			software.setVersion(SOFTWARE_VERSION_BIZKAIA_TEST);
		}
		huella.setSoftware(software);
		return huella;
	}
	
	private static HuellaTBAI getHuella(TbaiConfiguration tbai, TbaiBlockchain blockchain) {
		HuellaTBAI huella = new HuellaTBAI();
		
		if(!blockchain.isEmpty()) {
			EncadenamientoFacturaAnteriorType encadenamiento = new EncadenamientoFacturaAnteriorType();
			encadenamiento.setFechaExpedicionFacturaAnterior(blockchain.getDate());
			encadenamiento.setSerieFacturaAnterior(blockchain.getSerie());
			encadenamiento.setNumFacturaAnterior(blockchain.getNumber());
			encadenamiento.setSignatureValueFirmaFacturaAnterior(blockchain.getSignature());
			huella.setEncadenamientoFacturaAnterior(encadenamiento);
		}
		
		SoftwareFacturacionType software = new SoftwareFacturacionType();
		EntidadDesarrolladoraType entidad = new EntidadDesarrolladoraType();
		entidad.setNIF("B01487271");
		software.setEntidadDesarrolladora(entidad);
		if(tbai.isAraba() && tbai.isTest())
			software.setLicenciaTBAI(DEVICE_NUMBER_ARABA_TEST);
		else software.setLicenciaTBAI(DEVICE_NUMBER);
		software.setNombre(SOFTWARE_NAME);
		software.setVersion(SOFTWARE_VERSION);
	
		if(tbai.isBizkaia() && tbai.isTest()) {
			entidad = new EntidadDesarrolladoraType();
			entidad.setNIF(NIF_BIZKAIA_TEST);
			software.setEntidadDesarrolladora(entidad);
			software.setLicenciaTBAI(DEVICE_NUMBER_BIZKAIA_TEST);
			software.setNombre(SOFTWARE_NAME_BIZKAIA_TEST);
			software.setVersion(SOFTWARE_VERSION_BIZKAIA_TEST);
		}
		huella.setSoftware(software);
		return huella;
	}
	
	private static Sujetos getSujetos(Company company, Invoice invoice, TbaiConfiguration config) {
		Sujetos entities = new Sujetos();
		company.isLegalPerson();
		String document = company.getDocument().replace(" ", "");
		String name = company.getName();
		
		Emisor sender = new Emisor();
		sender.setApellidosNombreRazonSocial(name);
		sender.setNIF(document);
		entities.setEmisor(sender);			
		if(!AonStringUtils.isBlank(invoice.getRegistryDocument())) {
			Destinatarios receivers = new Destinatarios();
		
			IDDestinatario receiver = new IDDestinatario();
			receiver.setApellidosNombreRazonSocial(invoice.getRegistryName());
			receiver.setCodigoPostal(invoice.getAddress().getZip());
			receiver.setDireccion(invoice.getAddress().getFullAddress()); 

			if(invoice.isNational() || (invoice.isIsp() && Country.ES.equals(invoice.getRegistryDocumentCountry()))) {
				receiver.setNIF(invoice.getRegistryDocument().replace(" ", ""));			
			} else {
				IDOtro other = new IDOtro();
				other.setCodigoPais(CountryType2.valueOf(invoice.getRegistryDocumentCountry().getIso2()));
				other.setIDType(IDType.OTRO.getName());
				other.setID(invoice.getRegistryDocument().replace(" ", ""));
				receiver.setIDOtro(other);
			}
			
			receivers.getIDDestinatario().add(receiver);
			entities.setDestinatarios(receivers);
			entities.setVariosDestinatarios(SiNoType.N);
			entities.setEmitidaPorTercerosODestinatario(EmitidaPorTercerosType.N);
		}
		return entities;
	}
	
	private static Factura getFactura(Invoice invoice) {
		Date expeditionDate = new Date();
		Factura factura = new Factura();
		CabeceraFacturaType cabecera = new CabeceraFacturaType();
		if(!AonStringUtils.isBlank(invoice.getSeries()))
			cabecera.setSerieFactura(invoice.getSeries());
		cabecera.setNumFactura(Integer.toString(invoice.getNumber()));
		cabecera.setFechaExpedicionFactura(AonDateUtils.format(expeditionDate, "dd-MM-yyyy"));
		cabecera.setHoraExpedicionFactura(AonDateUtils.format(expeditionDate, "HH:mm:ss"));
			
		cabecera.setFacturaSimplificada(invoice.isSimplified() ? SiNoType.S : SiNoType.N);
		cabecera.setFacturaEmitidaSustitucionSimplificada(SiNoType.N);

		if(invoice.isRectifier()) {
			FacturaRectificativaType rectificativa = new FacturaRectificativaType(); 
			rectificativa.setCodigo(ClaveTipoFacturaType.R_1);
			rectificativa.setTipo(ClaveTipoRectificativaType.I); // por diferencia o por sustitucion
			cabecera.setFacturaRectificativa(rectificativa);
			
			FacturasRectificadasSustituidasType rectificadas = new FacturasRectificadasSustituidasType();
			IDFacturaRectificadaSustituidaType rectificada = new IDFacturaRectificadaSustituidaType();
			rectificada.setSerieFactura(invoice.getRectificationInvoiceSeries());
			rectificada.setNumFactura(invoice.getRectificationInvoiceNumber().toString());
			rectificada.setFechaExpedicionFactura(AonDateUtils.format(invoice.getRectificationInvoiceDate(), "dd-MM-yyyy"));
			rectificadas.getIDFacturaRectificadaSustituida().add(rectificada);
			cabecera.setFacturasRectificadasSustituidas(rectificadas);
		}
		factura.setCabeceraFactura(cabecera);

		
		DatosFacturaType datos = new DatosFacturaType();
		datos.setFechaOperacion(AonDateUtils.format(invoice.getIssueDate(), "dd-MM-yyyy"));
		datos.setDescripcionFactura("FACTURA " + invoice.getReferenceCode());
		
		DetallesFacturaType detalles = new DetallesFacturaType();
		invoice.getDetails().stream().filter(f -> !f.isPrepayment()).forEach(detail -> {
			IDDetalleFacturaType detalle = new IDDetalleFacturaType();
			detalle.setCantidad(Double.toString(detail.getQuantity()));
			String description = detail.getDescription().replace("\n", " ");
			if(description.length() > 249) {
				description = description.substring(0, 249);
			}			
			detalle.setDescripcionDetalle(description);
			detalle.setImporteUnitario(Double.toString(AonMathUtils.round(detail.getPrice(), 4)));

			InvoiceTax tax = detail.getInvoiceTaxes().stream().filter(e -> TaxType.VAT.equals(e.getTaxType())).findFirst().get();
			
			double descuento = 0.0;
			if(!AonStringUtils.isBlank(detail.getDiscountExpression())) {
				descuento = AonMathUtils.round((detail.getQuantity() * detail.getPrice()) - tax.getBase());
			}
			detalle.setDescuento(Double.toString(descuento));
			
			if(tax.getPercentage() > 0 && tax.getQuota() == 0.0) {
				tax.setQuota(AonMathUtils.round(tax.getBase() * tax.getPercentage() / 100));
			}
			double total =  AonMathUtils.round(tax.getBase() + tax.getQuota() + tax.getSurchargeQuota());
			detalle.setImporteTotal(Double.toString(total));
			if(total != 0.0)
				detalles.getIDDetalleFactura().add(detalle);
		});
		
		Double totalAmount = detalles.getIDDetalleFactura().stream().mapToDouble(r -> Double.parseDouble(r.getImporteTotal())).sum();
		
		datos.setDetallesFactura(detalles);
		if(invoice.isWithholding()) {
			double ret = invoice.getBreakdown().stream().filter(f -> TaxType.RETENTION.equals(f.getTaxType()))
				.mapToDouble(r -> r.getQuota()).sum();
			datos.setRetencionSoportada(Double.toString(AonMathUtils.round(ret)));
		} 
		datos.setImporteTotalFactura(Double.toString(AonMathUtils.round(totalAmount)));

//		datos.setRetencionSoportada("");
//		datos.setBaseImponibleACoste("");
	
		ClavesType claves = new ClavesType();
		IDClaveType clave = new IDClaveType();
		clave.setClaveRegimenIvaOpTrascendencia("01");
		claves.getIDClave().add(clave);
		datos.setClaves(claves);
		factura.setDatosFactura(datos);
		
		TipoDesgloseType desglose = new TipoDesgloseType();

		SujetaType sujeta = new SujetaType();
		
		NoExentaType noExenta = new NoExentaType();
		DetalleNoExentaType detalleNoExenta = new DetalleNoExentaType();
		detalleNoExenta.setTipoNoExenta(invoice.isIsp() ? TipoOperacionSujetaNoExentaType.S_2 : TipoOperacionSujetaNoExentaType.S_1);
		DesgloseIVAType desgloseIVA = new DesgloseIVAType();
		invoice.getBreakdown().stream().filter(f -> TaxType.VAT.equals(f.getTaxType()) && (f.getPercentage() > 0 || invoice.isIsp())).forEach(r -> {
			if(r.getPercentage() > 0 && r.getQuota() == 0.0) {
				r.setQuota(AonMathUtils.round(r.getBase() * r.getPercentage() / 100));
			}
			DetalleIVAType  detalleIVA = new DetalleIVAType();
			detalleIVA.setBaseImponible(Double.toString(AonMathUtils.round(r.getBase())));
			detalleIVA.setCuotaImpuesto(invoice.isIsp() ? "0.0" : Double.toString(AonMathUtils.round(r.getQuota())));
			detalleIVA.setCuotaRecargoEquivalencia(invoice.isIsp() ? "0.0" : Double.toString(AonMathUtils.round(r.getSurchargeQuota())));
			detalleIVA.setTipoImpositivo(invoice.isIsp() ? "0.0" : Double.toString(r.getPercentage()));
			detalleIVA.setTipoRecargoEquivalencia(invoice.isIsp() ? "0.0" : Double.toString(AonMathUtils.round(r.getSurcharge())));
			detalleIVA.setOperacionEnRecargoDeEquivalenciaORegimenSimplificado(invoice.isSurcharge() ? SiNoType.S : SiNoType.N);
			desgloseIVA.getDetalleIVA().add(detalleIVA);
		});
		
		if(!desgloseIVA.getDetalleIVA().isEmpty()) {
			detalleNoExenta.setDesgloseIVA(desgloseIVA);
			noExenta.getDetalleNoExenta().add(detalleNoExenta);
			if(!noExenta.getDetalleNoExenta().isEmpty())
				sujeta.setNoExenta(noExenta);
		}
		
		ExentaType exenta = new ExentaType();
		invoice.getBreakdown().stream().filter(f -> TaxType.VAT.equals(f.getTaxType()) && f.getPercentage() == 0 && !invoice.isIsp()).forEach(r -> {
			DetalleExentaType detalleExenta = new DetalleExentaType();
			detalleExenta.setBaseImponible(Double.toString(AonMathUtils.round(r.getBase())));
			detalleExenta.setCausaExencion(CausaExencionType.E_6);
			if(invoice.isIntracommunity())
				detalleExenta.setCausaExencion(CausaExencionType.E_5);
			if(invoice.isExtracommunity())
				detalleExenta.setCausaExencion(CausaExencionType.E_2);
			exenta.getDetalleExenta().add(detalleExenta);
		});
		if(!exenta.getDetalleExenta().isEmpty())
			sujeta.setExenta(exenta);
		
		if(invoice.isNational() || (invoice.isIsp() && Country.ES.equals(invoice.getRegistryDocumentCountry()))) {
			DesgloseFacturaType desgloseFactura = new DesgloseFacturaType();
			desgloseFactura.setSujeta(sujeta);
			desglose.setDesgloseFactura(desgloseFactura);
		} else if(invoice.isService()){
			PrestacionServicios serv = new PrestacionServicios();
			serv.setSujeta(sujeta);
			DesgloseTipoOperacionType desgloseFactura = new DesgloseTipoOperacionType();
			desgloseFactura.setPrestacionServicios(serv);
			desglose.setDesgloseTipoOperacion(desgloseFactura);
		} else {
			Entrega entrega = new Entrega();
			entrega.setSujeta(sujeta);
			DesgloseTipoOperacionType desgloseFactura = new DesgloseTipoOperacionType();
			desgloseFactura.setEntrega(entrega);
			desglose.setDesgloseTipoOperacion(desgloseFactura);
		}

		factura.setTipoDesglose(desglose);
			
		return factura; 	
	}

}
