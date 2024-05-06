package net.aonsolutions.aon.tbai.lroe;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.CausaExencionEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.ClaveCodigoFacturaRectificativaEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.ClaveTipoRectificativaEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.CountryEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.OperacionEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.SiNoEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.TipoOperacionSujetaNoExentaEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionFacturaEmitidaSinSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionesFacturasEmitidasSinSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.CabeceraFacturaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.ClavesFacturaEmitidaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DatosFacturaEmitidaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DesgloseFacturaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DesgloseIVAType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DesgloseOperacionType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DestinatariosType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DetalleEmitidaSinSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DetalleExentaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DetalleNoExentaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DocumentoPersonaCodigoPostalType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.ExentaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.FacturaRectificativaImporteType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.FacturasEmitidasSinSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.FacturasRectificadasSustituidasType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDClaveFacturaEmitidaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDFacturaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDOtroType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.ImportesDetalleIVAType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.NoExentaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.SujetaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.TipoDesgloseType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_1_2_facturasemitidas_sinsg_altamodifpeticion_v1_0_1.LROEPJ240FacturasEmitidasSinSGAltaModifPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_1_2_facturasemitidas_sinsg_anulacionpeticion_v1_0_0.LROEPJ240FacturasEmitidasSinSGAnulacionPeticion;
import net.aonsolutions.aon.tbai.LroeData;
import net.aonsolutions.aon.tbai.responses.LROEResponse;


public class LROE240_1_2 extends LROE240 {

	private static final long serialVersionUID = 1L;

	private static final String CAPITULO = "1";
	private static final String SUBCAPITULO = "1.2";
	
	private LROEPJ240FacturasEmitidasSinSGAltaModifPeticion build(Company person, Invoice invoice, LROEInfo info) {
		LROEPJ240FacturasEmitidasSinSGAltaModifPeticion lroe = new LROEPJ240FacturasEmitidasSinSGAltaModifPeticion();
		lroe.setCabecera(buildCabecera(person, info));
		FacturasEmitidasSinSGType facturas = new FacturasEmitidasSinSGType();
		facturas.getFacturaEmitida().add(buildFactura(invoice));
		lroe.setFacturasEmitidas(facturas);
		return lroe;
	}
	
	private DetalleEmitidaSinSGType buildFactura(Invoice invoice) {
		DetalleEmitidaSinSGType factura = new DetalleEmitidaSinSGType();
		factura.setCabeceraFactura(buildInvoiceCabecera(invoice));
		factura.setDestinatarios(buildDestinatario(invoice));
		factura.setDatosFactura(buildDatosFactura(invoice));
		factura.setTipoDesglose(buildDesglose(invoice));
		factura.setVariosDestinatarios(SiNoEnum.N);
		return factura;
	}
	
	private CabeceraFacturaType buildInvoiceCabecera(Invoice invoice) {
		CabeceraFacturaType cabecera = new CabeceraFacturaType();
		cabecera.setSerieFactura(invoice.getSeries());
		cabecera.setNumFactura(Integer.toString(invoice.getNumber()));
		cabecera.setFechaExpedicionFactura(AonDateUtils.format(invoice.getIssueDate(), DATE_FORMAT));
		
		if(invoice.isRectified()) {
			FacturaRectificativaImporteType rectificativa = new FacturaRectificativaImporteType(); 
			rectificativa.setCodigo(ClaveCodigoFacturaRectificativaEnum.R_1); 
			rectificativa.setTipo(ClaveTipoRectificativaEnum.I); // por diferencia o por sustitucion
			cabecera.setFacturaRectificativa(rectificativa);
				
			FacturasRectificadasSustituidasType rectificadas = new FacturasRectificadasSustituidasType();
			IDFacturaType rectificada = new IDFacturaType();
			rectificada.setSerieFactura(invoice.getRectificationInvoiceSeries());
			rectificada.setNumFactura(invoice.getRectificationInvoiceNumber().toString());
			rectificada.setFechaExpedicionFactura(AonDateUtils.format(invoice.getRectificationInvoiceDate(), DATE_FORMAT));
			rectificadas.getIDFacturaRectificadaSustituida().add(rectificada);
			cabecera.setFacturasRectificadasSustituidas(rectificadas);
		}
		return cabecera;
	}
	
	private DatosFacturaEmitidaType buildDatosFactura(Invoice invoice) {
		DatosFacturaEmitidaType datos = new DatosFacturaEmitidaType();
		Double total = invoice.getBreakdown().stream().filter(f -> TaxType.VAT.equals(f.getTaxType()))
		.mapToDouble(r -> {
			if(r.getPercentage() > 0 && r.getQuota() == 0.0) {
				r.setQuota(AonMathUtils.round(r.getBase() * r.getPercentage() / 100));
			}
			return AonMathUtils.round(r.getBase() + r.getQuota() + r.getSurchargeQuota());
		}).sum();
		datos.setDescripcionFactura("Factura " + invoice.getReferenceCode());
		datos.setImporteTotalFactura(total.toString());
		
		ClavesFacturaEmitidaType claves = new ClavesFacturaEmitidaType();
		IDClaveFacturaEmitidaType clave = new IDClaveFacturaEmitidaType();
		clave.setClaveRegimenIvaOpTrascendencia("01");
		claves.getIDClave().add(clave);
		datos.setClaves(claves);	
		
		datos.setFechaOperacion(AonDateUtils.format(invoice.getIssueDate(), DATE_FORMAT));
		if(invoice.isWithholding()) {
			double ret = invoice.getBreakdown().stream().filter(f -> TaxType.RETENTION.equals(f.getTaxType()))
				.mapToDouble(r -> r.getQuota()).sum();
			datos.setRetencionSoportada(Double.toString(AonMathUtils.round(ret)));
		} 
		return datos;
	}
	
	private TipoDesgloseType buildDesglose(Invoice invoice) {
		TipoDesgloseType desglose = new TipoDesgloseType();
		
		SujetaType sujeta = new SujetaType();

		NoExentaType noExenta = new NoExentaType();
		DetalleNoExentaType detalleNoExenta = new DetalleNoExentaType();
		detalleNoExenta.setTipoNoExenta(invoice.isIsp() ? TipoOperacionSujetaNoExentaEnum.S_2 : TipoOperacionSujetaNoExentaEnum.S_1);
		DesgloseIVAType desgloseIVA = new DesgloseIVAType();
		invoice.getBreakdown().stream().filter(f -> TaxType.VAT.equals(f.getTaxType()) && (f.getPercentage() > 0 || invoice.isIsp())).forEach(r -> {
			if(r.getPercentage() > 0 && r.getQuota() == 0.0) {
				r.setQuota(AonMathUtils.round(r.getBase() * r.getPercentage() / 100));
			}
			ImportesDetalleIVAType  detalleIVA = new ImportesDetalleIVAType();
			detalleIVA.setBaseImponible(Double.toString(AonMathUtils.round(r.getBase())));
			detalleIVA.setCuotaImpuesto(Double.toString(AonMathUtils.round(r.getQuota())));
			detalleIVA.setCuotaRecargoEquivalencia(Double.toString(AonMathUtils.round(r.getSurchargeQuota())));
			detalleIVA.setTipoImpositivo(Double.toString(r.getPercentage()));
			detalleIVA.setTipoRecargoEquivalencia(Double.toString(AonMathUtils.round(r.getSurcharge())));
			detalleIVA.setOperacionEnRecargoDeEquivalenciaORegimenSimplificado(invoice.isSurcharge() ? SiNoEnum.S : SiNoEnum.N);
			desgloseIVA.getDetalleIVA().add(detalleIVA);
		});
		detalleNoExenta.setDesgloseIVA(desgloseIVA);
		noExenta.getDetalleNoExenta().add(detalleNoExenta);
		if(!noExenta.getDetalleNoExenta().isEmpty())
			sujeta.setNoExenta(noExenta);
		
		ExentaType exenta = new ExentaType();
		invoice.getBreakdown().stream().filter(f -> TaxType.VAT.equals(f.getTaxType()) && f.getPercentage() == 0 && !invoice.isIsp()).forEach(r -> {
			DetalleExentaType detalleExenta = new DetalleExentaType();
			detalleExenta.setBaseImponible(Double.toString(r.getBase()));
			detalleExenta.setCausaExencion(CausaExencionEnum.E_6);
			if(invoice.isIntracommunity())
				detalleExenta.setCausaExencion(CausaExencionEnum.E_5);
			if(invoice.isExtracommunity())
				detalleExenta.setCausaExencion(CausaExencionEnum.E_2);
			exenta.getDetalleExenta().add(detalleExenta);
		});
		if(!exenta.getDetalleExenta().isEmpty())
			sujeta.setExenta(exenta);
		
		if(invoice.isNational() || (invoice.isIsp() && Country.ES.equals(invoice.getRegistryDocumentCountry()))) {
			DesgloseFacturaType desgloseFactura = new DesgloseFacturaType();
			desgloseFactura.setSujeta(sujeta);
			desglose.setDesgloseFactura(desgloseFactura);
		} else if(invoice.isService()){
			DesgloseFacturaType serv = new DesgloseFacturaType();
			serv.setSujeta(sujeta);
			DesgloseOperacionType desgloseFactura = new DesgloseOperacionType();
			desgloseFactura.setPrestacionServicios(serv);
			desglose.setDesgloseTipoOperacion(desgloseFactura);
		} else {
			DesgloseFacturaType entrega = new DesgloseFacturaType();
			entrega.setSujeta(sujeta);
			DesgloseOperacionType desgloseFactura = new DesgloseOperacionType();
			desgloseFactura.setEntrega(entrega);
			desglose.setDesgloseTipoOperacion(desgloseFactura);
		}

		return desglose;
	}
	
	private DestinatariosType buildDestinatario(Invoice invoice) {
		DestinatariosType destinatarios = new DestinatariosType();
		DocumentoPersonaCodigoPostalType destinatario = new DocumentoPersonaCodigoPostalType();
		destinatario.setApellidosNombreRazonSocial(invoice.getRegistryName());
		destinatario.setCodigoPostal(invoice.getAddress().getZip());

		if (invoice.getRegistryDocumentCountry().equals(Country.ES)) {
			destinatario.setNIF(invoice.getRegistryDocument().replace(" ", ""));
		} else if(invoice.isIntracommunity()){
			IDOtroType otro = new IDOtroType();
			if(invoice.getRegistryDocumentCountry().equals(Country.XI)) {
				otro.setCodigoPais(CountryEnum.GB);
			} else otro.setCodigoPais(CountryEnum.valueOf(invoice.getRegistryDocumentCountry().getIso2()));		
			
			String document = invoice.getRegistryDocument().replace(" ", "");
			if(!document.substring(0,2).equals(invoice.getRegistryDocumentCountry().getIso2())) {
				document = invoice.getRegistryDocumentCountry().getIso2() + document;
			}
			otro.setID(document);
			otro.setIDType(IDType.NIF_IVA.getName());
			destinatario.setIDOtro(otro);
		} else {
			IDOtroType otro = new IDOtroType();
			otro.setCodigoPais(CountryEnum.valueOf(invoice.getRegistryDocumentCountry().getIso2()));
			otro.setID(invoice.getRegistryDocument().replace(" ", ""));
			otro.setIDType(IDType.valueOf(invoice.getRegistryDocumentType()).getName());
			destinatario.setIDOtro(otro);
		}
		destinatarios.getIDDestinatario().add(destinatario);
		return destinatarios;
	}
	
	public LROEResponse alta(TbaiConfiguration tbaiConfiguration, Company company, Invoice invoice) {
		try {
			LROEInfo info = buildInfo(OperacionEnum.A_00, getEjercicio(tbaiConfiguration, invoice));
			
			final LROEPJ240FacturasEmitidasSinSGAltaModifPeticion p140 = build(company, invoice, info); 
			final JAXBContext jaxbContext = JAXBContext.newInstance( LROEPJ240FacturasEmitidasSinSGAltaModifPeticion.class );
			final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();	

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( p140, bos );
			byte[] xml = bos.toByteArray();
			DataRequest dataRequest = LroeData.saveRequest(company.getDomain(), new User().setLogin(""), invoice, info, xml);
			byte[] data = toGzip(xml);
			return send(tbaiConfiguration, buildJSON(company, info), data).setDataRequest(dataRequest);
		} catch (Exception e) {
			return error(e);
		}
	}
	
	public LROEInfo buildInfo(OperacionEnum operacion, Integer ejercicio) {
		return new LROEInfo(MODEL_240, CAPITULO, SUBCAPITULO, operacion, ejercicio);
	}
	
	private LROEPJ240FacturasEmitidasSinSGAnulacionPeticion buildBaja(Company company, Invoice invoice, LROEInfo info) {	
		
		LROEPJ240FacturasEmitidasSinSGAnulacionPeticion lroe = new LROEPJ240FacturasEmitidasSinSGAnulacionPeticion();
		lroe.setCabecera(buildCabecera(company, info));
		AnulacionesFacturasEmitidasSinSGType anulaciones = new AnulacionesFacturasEmitidasSinSGType();
		
		AnulacionFacturaEmitidaSinSGType anulacion = new AnulacionFacturaEmitidaSinSGType();
		IDFacturaType factura = new IDFacturaType();
		factura.setFechaExpedicionFactura(AonDateUtils.format(invoice.getIssueDate(), DATE_FORMAT));
		factura.setSerieFactura(invoice.getSeries());
		factura.setNumFactura(Integer.toString(invoice.getNumber()));
		anulacion.setIDFactura(factura);
		anulaciones.getFacturaEmitida().add(anulacion);
		lroe.setFacturasEmitidas(anulaciones);
		return lroe;
	}
	
	public LROEResponse anulacion(TbaiConfiguration tbaiConfiguration, Company company, Invoice invoice)  {
		try {
			LROEInfo info = buildInfo(OperacionEnum.AN_0, getEjercicio(tbaiConfiguration, invoice));
			final LROEPJ240FacturasEmitidasSinSGAnulacionPeticion p140 = buildBaja(company, invoice, info); 
			final JAXBContext jaxbContext = JAXBContext.newInstance( LROEPJ240FacturasEmitidasSinSGAnulacionPeticion.class );
			final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();	

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( p140, bos );
			byte[] xml = bos.toByteArray();
			DataRequest dataRequest = LroeData.saveRequest(company.getDomain(), new User().setLogin(""), invoice, info, xml);
			byte[] data = toGzip(xml);
			return send(tbaiConfiguration, buildJSON(company, info), data).setDataRequest(dataRequest);
		} catch (Exception e) {
			return error(e);
		}
	}
}
