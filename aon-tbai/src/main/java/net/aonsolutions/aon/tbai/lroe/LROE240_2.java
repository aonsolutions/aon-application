package net.aonsolutions.aon.tbai.lroe;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.ClaveCodigoFacturaRectificativaEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.ClaveTipoFacturaGastosEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.ClaveTipoRectificativaEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.CountryEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.EstadoRegistroConsultaEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.OperacionEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.SiNoEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.TipoCompraGastoBienEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionFacturaRecibidaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionesFacturasRecibidasType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.CabeceraFacturaGastosRecibidasType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.ClavesFacturaRecibidaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DatosFacturaRecibidaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DetalleIVAFacturaRecibidaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DocumentoPersonaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DocumentoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.EntidadSucedidaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.FacturaRecibidaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.FacturaRectificativaImporteType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.FacturasRecibidasType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.FacturasRectificadasSustituidasType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDClaveFacturaRecibidaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDFacturaConEmisorType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDFacturaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDOtroType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IVAFacturaRecibidaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.OtraInformacionTrascendenciaTributariaRecibidaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposconsulta.CabeceraFacturaConsultaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposconsulta.FechaDesdeHastaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposconsulta.FiltroConsultaFacturasRecibidasType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_2_facturasrecibidas_altamodifpeticion_v1_0_1.LROEPJ240FacturasRecibidasAltaModifPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_2_facturasrecibidas_anulacionpeticion_v1_0_0.LROEPJ240FacturasRecibidasAnulacionPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_2_facturasrecibidas_consultapeticion_v1_0_0.LROEPJ240FacturasRecibidasConsultaPeticion;
import net.aonsolutions.aon.tbai.LroeData;
import net.aonsolutions.aon.tbai.exceptions.http.StatusCodeException;
import net.aonsolutions.aon.tbai.responses.LROEResponse;

public class LROE240_2 extends LROE240 {
	
	private static final long serialVersionUID = 1L;
	
	private static final String CAPITULO = "2";
	
	private LROEPJ240FacturasRecibidasAltaModifPeticion build(Company company, List<Invoice> invoices, LROEInfo info) {
		LROEPJ240FacturasRecibidasAltaModifPeticion lroe =  new LROEPJ240FacturasRecibidasAltaModifPeticion();
		lroe.setCabecera(buildCabecera(company, info));

		FacturasRecibidasType facturas = new FacturasRecibidasType();
		invoices.stream().forEach(invoice -> {
			facturas.getFacturaRecibida().add(buildFacturaRecibida(invoice));
		});
		lroe.setFacturasRecibidas(facturas);
		return lroe;
	}
	
	private FacturaRecibidaType buildFacturaRecibida(Invoice invoice) {
		FacturaRecibidaType facturaRecibida = new FacturaRecibidaType();
		facturaRecibida.setEmisorFacturaRecibida(buildEmisor(invoice));
		facturaRecibida.setCabeceraFactura(buildInvoiceCabecera(invoice));
		facturaRecibida.setDatosFactura(buildFactura(invoice));
		facturaRecibida.setIVA(buildIVA(invoice));
		// TODO facturaRecibida.setOtraInformacionTrascendenciaTributaria(buildOtraInformacion(invoice));
		return facturaRecibida;
	}
	
	private DocumentoPersonaType buildEmisor(Invoice invoice) {
		DocumentoPersonaType emisor = new DocumentoPersonaType();
		emisor.setApellidosNombreRazonSocial(invoice.getRegistryName());
		
		if (invoice.getRegistryDocumentCountry().equals(Country.ES)) {
			emisor.setNIF(invoice.getRegistryDocument());
		} else if(invoice.isIntracommunity()){
			IDOtroType otro = new IDOtroType();
			otro.setCodigoPais(CountryEnum.valueOf(invoice.getRegistryDocumentCountry().getIso2()));
			String document = invoice.getRegistryDocument();
			if(!document.substring(0,2).equals(invoice.getRegistryDocumentCountry().getIso2())) {
				document = invoice.getRegistryDocumentCountry().getIso2() + document;
			}
			otro.setID(document);
			otro.setIDType(IDType.NIF_IVA.getName());
			emisor.setIDOtro(otro);
		} else {
			IDOtroType otro = new IDOtroType();
			otro.setCodigoPais(CountryEnum.valueOf(invoice.getRegistryDocumentCountry().getIso2()));
			otro.setID(invoice.getRegistryDocument());
			otro.setIDType(IDType.valueOf(invoice.getRegistryDocumentType()).getName());
			emisor.setIDOtro(otro);
		}
		return emisor;
	}
	
	private DocumentoType buildEmisorAnulacion(Invoice invoice) {
		DocumentoType emisor = new DocumentoType();
		
		if (invoice.getRegistryDocumentCountry().equals(Country.ES)) {
			emisor.setNIF(invoice.getRegistryDocument());
		} else if(invoice.isIntracommunity()){
			IDOtroType otro = new IDOtroType();
			otro.setCodigoPais(CountryEnum.valueOf(invoice.getRegistryDocumentCountry().getIso2()));
			String document = invoice.getRegistryDocument();
			if(!document.substring(0,2).equals(invoice.getRegistryDocumentCountry().getIso2())) {
				document = invoice.getRegistryDocumentCountry().getIso2() + document;
			}
			otro.setID(document);
			otro.setIDType(IDType.NIF_IVA.getName());
			emisor.setIDOtro(otro);
		} else {
			IDOtroType otro = new IDOtroType();
			otro.setCodigoPais(CountryEnum.valueOf(invoice.getRegistryDocumentCountry().getIso2()));
			otro.setID(invoice.getRegistryDocument());
			otro.setIDType(IDType.valueOf(invoice.getRegistryDocumentType()).getName());
			emisor.setIDOtro(otro);
		}
		return emisor;
	}
	
	private CabeceraFacturaGastosRecibidasType buildInvoiceCabecera(Invoice invoice) {
		CabeceraFacturaGastosRecibidasType cabecera = new CabeceraFacturaGastosRecibidasType();
		cabecera.setTipoFactura(ClaveTipoFacturaGastosEnum.F_1);
		cabecera.setNumFactura(invoice.getReferenceCode());
		cabecera.setFechaExpedicionFactura(AonDateUtils.format(invoice.getIssueDate(), DATE_FORMAT));
		cabecera.setFechaRecepcion(AonDateUtils.format(invoice.getCreationDate(), DATE_FORMAT));
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
	
	private DatosFacturaRecibidaType buildFactura(Invoice invoice) {
		DatosFacturaRecibidaType factura = new DatosFacturaRecibidaType();
		Double total = invoice.getBreakdown().stream().filter(f -> TaxType.VAT.equals(f.getTaxType()))
		.mapToDouble(r -> {
			if(r.getPercentage() > 0 && r.getQuota() == 0.0) {
				r.setQuota(AonMathUtils.round(r.getBase() * r.getPercentage() / 100));
			}
			return AonMathUtils.round(r.getBase() + r.getQuota() + r.getSurchargeQuota());
		}).sum();
		
		factura.setDescripcionOperacion("Factura " + invoice.getReferenceCode());
		factura.setImporteTotalFactura(Double.toString(AonMathUtils.round(total)));
		ClavesFacturaRecibidaType claves = new ClavesFacturaRecibidaType();
		IDClaveFacturaRecibidaType clave = new IDClaveFacturaRecibidaType();
		clave.setClaveRegimenIvaOpTrascendencia("01");
		claves.getIDClave().add(clave);
		factura.setClaves(claves);
		return factura;
	}

	private IVAFacturaRecibidaType buildIVA(Invoice invoice) {
		IVAFacturaRecibidaType iva = new IVAFacturaRecibidaType();

		for (InvoiceDetail detail : invoice.getDetails()) {
			if(!detail.isPrepayment()) {
				InvoiceTax tax = detail.getInvoiceTaxes().stream().filter(e -> TaxType.VAT.equals(e.getTaxType())).findFirst().orElse(new InvoiceTax());
//				InvoiceTax irpf = detail.getInvoiceTaxes().stream().filter(e -> TaxType.RETENTION.equals(e.getTaxType())).findFirst().orElse(new InvoiceTax());
				if(tax.getPercentage() > 0 && tax.getQuota() == 0.0) {
					tax.setQuota(AonMathUtils.round(tax.getBase() * tax.getPercentage() / 100));
				}
				DetalleIVAFacturaRecibidaType r = new DetalleIVAFacturaRecibidaType();
				r.setCompraBienesCorrientesGastosBienesInversion(invoice.isPurchase() ? TipoCompraGastoBienEnum.C : TipoCompraGastoBienEnum.G);
				if(detail.getInvestAsset() != null && invoice.isInvestment()) {
					r.setCompraBienesCorrientesGastosBienesInversion(TipoCompraGastoBienEnum.I);
				}
				r.setInversionSujetoPasivo(invoice.isIsp() ? SiNoEnum.S : SiNoEnum.N);
				
				r.setBaseImponible(Double.toString(tax.getBase()));	
				r.setTipoImpositivo(Double.toString(tax.getPercentage()));
				if(tax.getDeductiblePercent() > 0 && tax.getDeductibleQuota() == 0.0) {
					tax.setDeductibleQuota(AonMathUtils.round(tax.getQuota() * tax.getDeductiblePercent() / 100));
				}
				r.setCuotaIVADeducible(Double.toString(tax.getDeductibleQuota()));
				r.setCuotaIVASoportada(Double.toString(tax.getQuota()));
				
				// r.setPorcentajeCompensacionREAGYP("");
				// r.setImporteCompensacionREAGYP("");
			
				iva.getDetalleIVA().add(r);
			}
		}
		return iva;
	}
	
	// TODO
	private OtraInformacionTrascendenciaTributariaRecibidaType buildOtraInformacion(Invoice invoice) {
		OtraInformacionTrascendenciaTributariaRecibidaType otra = new OtraInformacionTrascendenciaTributariaRecibidaType();
		otra.setFechaRegistroContable("");
		otra.setNumRegistroAcuerdoFacturacion("");
		otra.setReferenciaExterna("");
		EntidadSucedidaType entidad = new EntidadSucedidaType();
		entidad.setNIF("");
		entidad.setNombreRazon("");
		otra.setEntidadSucedida(entidad);
		return otra;
	}
	
	public LROEResponse alta(TbaiConfiguration tbaiConfiguration, Company company, Invoice invoice) {
		LinkedList<Invoice> invoices = new LinkedList<>();
		invoices.add(invoice);
		boolean mod = invoice.getInvoiceInfo().getStatus().isAccepted() || invoice.getInvoiceInfo().getStatus().isAcceptedWithErrors();
		return alta(tbaiConfiguration, company, invoices, mod);
	}
	
	public LROEResponse alta(TbaiConfiguration tbaiConfiguration, Company company, List<Invoice> invoices, boolean mod) {
		try {
			LROEInfo info = buildInfo(mod ? OperacionEnum.M_00 : OperacionEnum.A_00);
			final LROEPJ240FacturasRecibidasAltaModifPeticion p240 = build(company, invoices, info); 
			final JAXBContext jaxbContext = JAXBContext.newInstance( LROEPJ240FacturasRecibidasAltaModifPeticion.class );
			final Marshaller jaxbMarshaller   = jaxbContext.createMarshaller();	

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( p240, bos );
			
			byte[] xml = bos.toByteArray();
			DataRequest dataRequest = LroeData.saveRequest(company.getDomain(), new User().setLogin(""), invoices, info, xml);
			byte[] data = toGzip(xml);
			return send(tbaiConfiguration, buildJSON(company, info), data).setDataRequest(dataRequest);
		} catch (Exception e) {
			return error(e);
		}
	}
	
	public LROEInfo buildInfo(OperacionEnum operacion) {
		return new LROEInfo(MODEL_240, CAPITULO, null, operacion);
	}
	
	private LROEPJ240FacturasRecibidasAnulacionPeticion buildBaja(Company company, Invoice invoice, LROEInfo info) {	
		LROEPJ240FacturasRecibidasAnulacionPeticion lroe = new LROEPJ240FacturasRecibidasAnulacionPeticion();
		lroe.setCabecera(buildCabecera(company, info));
		AnulacionesFacturasRecibidasType anulaciones = new AnulacionesFacturasRecibidasType();
		AnulacionFacturaRecibidaType anulacion = new AnulacionFacturaRecibidaType();

		IDFacturaConEmisorType factura = new IDFacturaConEmisorType();

		factura.setEmisorFacturaRecibida(buildEmisorAnulacion(invoice));
		factura.setFechaExpedicionFactura(AonDateUtils.format(invoice.getIssueDate(), DATE_FORMAT));
		factura.setSerieFactura(invoice.getSeries());
		factura.setNumFactura(invoice.getReferenceCode());
		anulacion.setIDRecibida(factura);
		
		anulaciones.getFacturaRecibida().add(anulacion);
		lroe.setFacturasRecibidas(anulaciones);
		return lroe;
	}
	
	public LROEResponse anulacion(Company company, TbaiConfiguration tbaiConfiguration, Invoice invoice) throws StatusCodeException {
		try {
			LROEInfo info = buildInfo(OperacionEnum.AN_0);
			final LROEPJ240FacturasRecibidasAnulacionPeticion p240 = buildBaja(company, invoice, info); 
			final JAXBContext jaxbContext = JAXBContext.newInstance( LROEPJ240FacturasRecibidasAnulacionPeticion.class );
			final Marshaller jaxbMarshaller   = jaxbContext.createMarshaller();	

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( p240, bos );
			byte[] xml = bos.toByteArray();
			DataRequest dataRequest = LroeData.saveRequest(company.getDomain(), new User().setLogin(""), invoice, info, xml);
			byte[] data = toGzip(xml);
			return send(tbaiConfiguration, buildJSON(company, info), data).setDataRequest(dataRequest);
		} catch (Exception e) {
			return error(e);
		}
	}
	
	private LROEPJ240FacturasRecibidasConsultaPeticion buildConsulta(Company company, Invoice invoice, LROEInfo info) {
		LROEPJ240FacturasRecibidasConsultaPeticion lroe = new LROEPJ240FacturasRecibidasConsultaPeticion();
		lroe.setCabecera(buildCabecera(company, info));
		FiltroConsultaFacturasRecibidasType filtro = new FiltroConsultaFacturasRecibidasType(); 
		filtro.setCabeceraFactura(buildCabeceraFactura(invoice));
		filtro.setEmisorFacturaRecibida(buildEmisorAnulacion(invoice));
		filtro.setEstado(EstadoRegistroConsultaEnum.CORRECTO);
		filtro.setNumPaginaConsulta(1);
		lroe.setFiltroConsultaFacturasRecibidas(filtro);
		return lroe;
	}
	
	private CabeceraFacturaConsultaType buildCabeceraFactura(Invoice invoice) {
		CabeceraFacturaConsultaType cabecera = new CabeceraFacturaConsultaType();
		FechaDesdeHastaType fecha = new FechaDesdeHastaType();
		fecha.setDesde(AonDateUtils.format(invoice.getIssueDate(), DATE_FORMAT));
		fecha.setDesde(AonDateUtils.format(invoice.getIssueDate(), DATE_FORMAT));
		cabecera.setFechaExpedicionFactura(fecha);

		if(!AonStringUtils.isBlank(invoice.getSeries()))
			cabecera.setSerieFactura(invoice.getSeries());
		cabecera.setNumFactura(Integer.toString(invoice.getNumber()));
		return cabecera;
	}
	
	
	public void consulta(TbaiConfiguration tbaiConfiguration, Company company, Invoice invoice) {
		try {
			LROEInfo info = buildInfo(OperacionEnum.C_00);
			LROEPJ240FacturasRecibidasConsultaPeticion lroe = buildConsulta(company, invoice, info);
			final JAXBContext jaxbContext = JAXBContext.newInstance( LROEPJ240FacturasRecibidasConsultaPeticion.class );
			final Marshaller jaxbMarshaller   = jaxbContext.createMarshaller();	

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( lroe, bos );
			byte[] xml = bos.toByteArray();
			byte[] data = toGzip(xml);
			sendConsulta(tbaiConfiguration, buildJSON(company, info), data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
