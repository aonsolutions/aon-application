package net.aonsolutions.aon.tbai;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddressFilter;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import ticketbai.emision.Cabecera;
import ticketbai.emision.CabeceraFacturaType;
import ticketbai.emision.ClaveTipoFacturaType;
import ticketbai.emision.ClaveTipoRectificativaType;
import ticketbai.emision.ClavesType;
import ticketbai.emision.DatosFacturaType;
import ticketbai.emision.DesgloseFacturaType;
import ticketbai.emision.DesgloseIVAType;
import ticketbai.emision.DesgloseTipoOperacionType;
import ticketbai.emision.Destinatarios;
import ticketbai.emision.DetalleIVAType;
import ticketbai.emision.DetalleNoExentaType;
import ticketbai.emision.DetallesFacturaType;
import ticketbai.emision.Emisor;
import ticketbai.emision.EmitidaPorTercerosType;
import ticketbai.emision.EncadenamientoFacturaAnteriorType;
import ticketbai.emision.EntidadDesarrolladoraType;
import ticketbai.emision.Factura;
import ticketbai.emision.FacturaRectificativaType;
import ticketbai.emision.FacturasRectificadasSustituidasType;
import ticketbai.emision.HuellaTBAI;
import ticketbai.emision.IDClaveType;
import ticketbai.emision.IDDestinatario;
import ticketbai.emision.IDDetalleFacturaType;
import ticketbai.emision.IDFacturaRectificadaSustituidaType;
import ticketbai.emision.NoExentaType;
import ticketbai.emision.SiNoType;
import ticketbai.emision.SoftwareFacturacionType;
import ticketbai.emision.SujetaType;
import ticketbai.emision.Sujetos;
import ticketbai.emision.TicketBai;
import ticketbai.emision.TipoDesgloseType;
import ticketbai.emision.TipoOperacionSujetaNoExentaType;

public class Invoice2tbai {

	private Invoice2tbai() {
	
	}
	
	private static final String TBAI_VERSION = "1.2";
	private static final String DEVICE_NUMBER = "TBAIGIPRE00000000131";
	private static final String DEVICE_NUMBER_ARABA_TEST = "TBAIARbjlCHFMFK00416";
	private static final String SOFTWARE_NAME = "aonSolutions";
	private static final String SOFTWARE_VERSION = "9.23" ;

	public static TicketBai build(Company company, Invoice invoice, TbaiConfiguration config, TbaiBlockchain blockchain) {
		TicketBai tbai = new TicketBai();
		tbai.setCabecera(getCabecera());
		tbai.setSujetos(getSujetos(company, invoice));
		tbai.setFactura(getFactura(invoice));
		tbai.setHuellaTBAI(getHuella(config, blockchain));
		return tbai;
	}
	
	private static Cabecera getCabecera() { 
		final Cabecera c = new Cabecera();
		c.setIDVersionTBAI(TBAI_VERSION);
		return c; 
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
		huella.setSoftware(software);
		return huella;
	}
	
	private static Sujetos getSujetos(Company company, Invoice invoice) {
		Sujetos entities = new Sujetos();
			
		Emisor sender = new Emisor();
		sender.setApellidosNombreRazonSocial(company.getName());
		sender.setNIF(company.getDocument());
		entities.setEmisor(sender);
			
		Destinatarios receivers = new Destinatarios();
		if(AonStringUtils.isBlank(invoice.getAddressZIP())) {
			RegistryAddressFilter filter = f -> f.getIdProperty().eq(invoice.getRegistryAddress());
			RegistryAddress a = AON.get(company.getDomain().getName(), company.getDomain().getId(), "", filter);
			invoice.setAddressZIP(a.getZip());
			invoice.setAddress(a.getFullAddress());
		}
		IDDestinatario receiver = new IDDestinatario();
		receiver.setApellidosNombreRazonSocial(invoice.getRegistryName());
		receiver.setCodigoPostal(invoice.getAddressZIP());
		receiver.setDireccion(invoice.getAddress()); // TODO
		receiver.setNIF(invoice.getRegistryDocument());
		
		// TODO if(not spain!)
//		IDOtro other = new IDOtro();
//		other.setCodigoPais(countryToCountryType2(invoice.getRegistryDocumentCountry()));
//		other.setIDType(IDtype.OTHER.getCode());
//		other.setID(invoice.getRegistryDocument());
//		receiver.setIDOtro(other);
			
		receivers.getIDDestinatario().add(receiver);
		entities.setDestinatarios(receivers);
		entities.setVariosDestinatarios(SiNoType.N);
		entities.setEmitidaPorTercerosODestinatario(EmitidaPorTercerosType.N);
		return entities;
	}
	
	private static Factura getFactura(Invoice invoice) {
		Factura factura = new Factura();
		CabeceraFacturaType cabecera = new CabeceraFacturaType();
		cabecera.setSerieFactura(invoice.getSeries());
		cabecera.setNumFactura(Integer.toString(invoice.getNumber()));
		cabecera.setFechaExpedicionFactura(AonDateUtils.format(invoice.getModificationDate(), "dd-MM-yyyy"));
		cabecera.setHoraExpedicionFactura(AonDateUtils.format(invoice.getModificationDate(), "HH:mm:ss"));
			
		cabecera.setFacturaSimplificada(SiNoType.N);
		cabecera.setFacturaEmitidaSustitucionSimplificada(SiNoType.N);

		if(invoice.isRectified()) {
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
		datos.setFechaOperacion(AonDateUtils.format(invoice.getCreationDate(), "dd-MM-yyyy"));
		datos.setDescripcionFactura("FACTURA " + invoice.getReferenceCode());
		
		DetallesFacturaType detalles = new DetallesFacturaType();
		invoice.getDetails().stream().filter(f -> !f.isPrepayment()).forEach(detail -> {
			IDDetalleFacturaType detalle = new IDDetalleFacturaType();
			detalle.setCantidad(Double.toString(detail.getQuantity()));
			detalle.setDescripcionDetalle(detail.getDescription());
			detalle.setDescuento(AonStringUtils.isBlank(detail.getDiscountExpression()) ? "0.0" : detail.getDiscountExpression());
			detalle.setImporteUnitario(Double.toString(detail.getPrice()));
			detalles.getIDDetalleFactura().add(detalle);
			InvoiceTax tax = detail.getInvoiceTaxes().stream().filter(e -> TaxType.VAT.equals(e.getTaxType())).findFirst().get();
			if(tax.getPercentage() > 0 && tax.getQuota() == 0.0) {
				tax.setQuota(AonMathUtils.round(tax.getBase() * tax.getPercentage() / 100));
			}
			double total =  AonMathUtils.round(tax.getBase() + tax.getQuota());
			detalle.setImporteTotal(Double.toString(total));
		});
		
		Double totalAmount = detalles.getIDDetalleFactura().stream().mapToDouble(r -> Double.parseDouble(r.getImporteTotal())).sum();
		
		datos.setDetallesFactura(detalles);
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
		if(invoice.isNational()) {
			DesgloseFacturaType desgloseFactura = new DesgloseFacturaType();

			SujetaType sujeta = new SujetaType();
			NoExentaType noExenta = new NoExentaType();
			DetalleNoExentaType detalleNoExenta = new DetalleNoExentaType();
			detalleNoExenta.setTipoNoExenta(TipoOperacionSujetaNoExentaType.S_1); // TODO ISP O NO
			DesgloseIVAType desgloseIVA = new DesgloseIVAType();
			invoice.getBreakdown().stream().filter(f -> TaxType.VAT.equals(f.getTaxType()) && f.getPercentage() > 0).forEach(r -> {
				if(r.getPercentage() > 0 && r.getQuota() == 0.0) {
					r.setQuota(AonMathUtils.round(r.getBase() * r.getPercentage() / 100));
				}
				DetalleIVAType  detalleIVA = new DetalleIVAType();
				detalleIVA.setBaseImponible(Double.toString(r.getBase()));
				detalleIVA.setCuotaImpuesto(Double.toString(r.getQuota()));
				detalleIVA.setCuotaRecargoEquivalencia(Double.toString(r.getSurchargeQuota()));
				detalleIVA.setTipoImpositivo(Double.toString(r.getPercentage()));
				detalleIVA.setTipoRecargoEquivalencia(Double.toString(r.getSurcharge()));
				detalleIVA.setOperacionEnRecargoDeEquivalenciaORegimenSimplificado(SiNoType.N); // TODO SURCHARGE O SIMP SI O NO.
				desgloseIVA.getDetalleIVA().add(detalleIVA);
			});
			
			detalleNoExenta.setDesgloseIVA(desgloseIVA);
			noExenta.getDetalleNoExenta().add(detalleNoExenta);

			sujeta.setNoExenta(noExenta);
			
			desgloseFactura.setSujeta(sujeta);
			desglose.setDesgloseFactura(desgloseFactura);
		} else {
			DesgloseTipoOperacionType desgloseTipoOperacion = new DesgloseTipoOperacionType();
			desglose.setDesgloseTipoOperacion(desgloseTipoOperacion);
		}
		factura.setTipoDesglose(desglose);
			
		return factura; 	
	}
	
}
