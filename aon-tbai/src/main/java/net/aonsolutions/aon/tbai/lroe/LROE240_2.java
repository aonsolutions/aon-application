package net.aonsolutions.aon.tbai.lroe;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

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
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_2_facturasrecibidas_consultarespuesta_v1_0_0.LROEPJ240FacturasRecibidasConsultaRespuesta;
import net.aonsolutions.aon.tbai.LroeData;
import net.aonsolutions.aon.tbai.exceptions.TBAIError;
import net.aonsolutions.aon.tbai.exceptions.TbaiException;
import net.aonsolutions.aon.tbai.exceptions.http.StatusCodeException;
import net.aonsolutions.aon.tbai.responses.LROEResponse;

public class LROE240_2 extends LROE240 {
	
	private static final long serialVersionUID = 1L;
	
	private static final String CAPITULO = "2";
	
	private LROEPJ240FacturasRecibidasAltaModifPeticion build(TbaiConfiguration tbaiConfiguration, Company company, List<Invoice> invoices, LROEInfo info) {
		LROEPJ240FacturasRecibidasAltaModifPeticion lroe =  new LROEPJ240FacturasRecibidasAltaModifPeticion();
		lroe.setCabecera(buildCabecera(company, info));

		FacturasRecibidasType facturas = new FacturasRecibidasType();
		invoices.stream().forEach(invoice -> {
			facturas.getFacturaRecibida().add(buildFacturaRecibida(tbaiConfiguration, invoice));
		});
		lroe.setFacturasRecibidas(facturas);
		return lroe;
	}
	
	private FacturaRecibidaType buildFacturaRecibida(TbaiConfiguration tbaiConfiguration, Invoice invoice) {
		FacturaRecibidaType facturaRecibida = new FacturaRecibidaType();
		facturaRecibida.setEmisorFacturaRecibida(buildEmisor(invoice));
		facturaRecibida.setCabeceraFactura(buildInvoiceCabecera(tbaiConfiguration, invoice));
		facturaRecibida.setDatosFactura(buildFactura(invoice));
		facturaRecibida.setIVA(buildIVA(invoice));
		// TODO facturaRecibida.setOtraInformacionTrascendenciaTributaria(buildOtraInformacion(invoice));
		return facturaRecibida;
	}
	
	private DocumentoPersonaType buildEmisor(Invoice invoice) {
		DocumentoPersonaType emisor = new DocumentoPersonaType();
		emisor.setApellidosNombreRazonSocial(invoice.getRegistryName());
		
		if (invoice.getRegistryDocumentCountry().equals(Country.ES)) {
			emisor.setNIF(invoice.getRegistryDocument().replace(" ", ""));
		} else if(invoice.isIntracommunity()){
			IDOtroType otro = new IDOtroType();
			if(invoice.getRegistryDocumentCountry().equals(Country.XI)) {
				otro.setCodigoPais(CountryEnum.GB);
			} else otro.setCodigoPais(CountryEnum.valueOf(invoice.getRegistryDocumentCountry().getIso2()));
		
			String document = invoice.getRegistryDocument().replace(" ", "");
			if(!document.substring(0,2).equals(invoice.getRegistryDocumentCountry().getIso2())) {
				boolean isGrecia = Country.GR.equals(invoice.getRegistryDocumentCountry());
				String countryDocument = isGrecia ? "EL" : invoice.getRegistryDocumentCountry().getIso2();
				document = countryDocument + document;
			}
			otro.setID(document);
			otro.setIDType(IDType.NIF_IVA.getName());
			emisor.setIDOtro(otro);
		} else {
			IDOtroType otro = new IDOtroType();
			otro.setCodigoPais(CountryEnum.valueOf(invoice.getRegistryDocumentCountry().getIso2()));
			otro.setID(invoice.getRegistryDocument().replace(" ", ""));
			otro.setIDType(IDType.valueOf(invoice.getRegistryDocumentType()).getName());
			emisor.setIDOtro(otro);
		}
		return emisor;
	}
	
	private DocumentoType buildEmisorAnulacion(Invoice invoice) {
		DocumentoType emisor = new DocumentoType();
		
		if (invoice.getRegistryDocumentCountry().equals(Country.ES)) {
			emisor.setNIF(invoice.getRegistryDocument().replace(" ", ""));
		} else if(invoice.isIntracommunity()){
			IDOtroType otro = new IDOtroType();
			
			if(invoice.getRegistryDocumentCountry().equals(Country.XI)) {
				otro.setCodigoPais(CountryEnum.GB);
			} else otro.setCodigoPais(CountryEnum.valueOf(invoice.getRegistryDocumentCountry().getIso2()));

			String document = invoice.getRegistryDocument().replace(" ", "");
			if(!document.substring(0,2).equals(invoice.getRegistryDocumentCountry().getIso2())) {
				boolean isGrecia = Country.GR.equals(invoice.getRegistryDocumentCountry());
				String countryDocument = isGrecia ? "EL" : invoice.getRegistryDocumentCountry().getIso2();
				document = countryDocument + document;
			}
			otro.setID(document);
			otro.setIDType(IDType.NIF_IVA.getName());
			emisor.setIDOtro(otro);
		} else {
			IDOtroType otro = new IDOtroType();
			otro.setCodigoPais(CountryEnum.valueOf(invoice.getRegistryDocumentCountry().getIso2()));
			otro.setID(invoice.getRegistryDocument().replace(" ", ""));
			otro.setIDType(IDType.valueOf(invoice.getRegistryDocumentType()).getName());
			emisor.setIDOtro(otro);
		}
		return emisor;
	}
	
	private CabeceraFacturaGastosRecibidasType buildInvoiceCabecera(TbaiConfiguration tbaiConfiguration, Invoice invoice) {
		CabeceraFacturaGastosRecibidasType cabecera = new CabeceraFacturaGastosRecibidasType();
		cabecera.setTipoFactura(ClaveTipoFacturaGastosEnum.F_1);
		String reference = invoice.getReferenceCode().length() > 20 ? invoice.getReferenceCode().substring(0, 20) : invoice.getReferenceCode();
		cabecera.setNumFactura(reference);
		cabecera.setFechaExpedicionFactura(AonDateUtils.format(invoice.getIssueDate(), DATE_FORMAT));
		Date receptionDate = tbaiConfiguration.isRegistryTaxDate()
				? invoice.getTaxDate() : invoice.getCreationDate();
		if(receptionDate.before(invoice.getIssueDate()))
			receptionDate = invoice.getIssueDate();
		cabecera.setFechaRecepcion(AonDateUtils.format(receptionDate, DATE_FORMAT));
		if(invoice.isRectifier()) {
			cabecera.setSerieFactura(reference.substring(0, 1));
			cabecera.setNumFactura(reference.substring(1));

			FacturaRectificativaImporteType rectificativa = new FacturaRectificativaImporteType(); 
			rectificativa.setCodigo(ClaveCodigoFacturaRectificativaEnum.R_1); 
			rectificativa.setTipo(ClaveTipoRectificativaEnum.I); // por diferencia o por sustitucion
			cabecera.setFacturaRectificativa(rectificativa);
				
			FacturasRectificadasSustituidasType rectificadas = new FacturasRectificadasSustituidasType();
			IDFacturaType rectificada = new IDFacturaType();
			// rectificada.setSerieFactura(invoice.getRectificationInvoiceSeries());
			rectificada.setNumFactura(invoice.getRectificationInvoiceReference());
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
			r.setBase(AonMathUtils.round(r.getBase()));
			if(invoice.isExtracommunity()) {
				r.setPercentage(0.0);
				r.setQuota(0.0);
				r.setSurchargeQuota(0.0);
			}
 			if(r.getPercentage() > 0 && r.getQuota() == 0.0) {
				r.setQuota(AonMathUtils.round(r.getBase() * r.getPercentage() / 100));
			}
			return AonMathUtils.round(r.getBase() + r.getQuota() + r.getSurchargeQuota());
		}).sum();
		
		factura.setDescripcionOperacion("Factura " + invoice.getReferenceCode());
		factura.setImporteTotalFactura(Double.toString(AonMathUtils.round(total)));
		ClavesFacturaRecibidaType claves = new ClavesFacturaRecibidaType();
		IDClaveFacturaRecibidaType clave = new IDClaveFacturaRecibidaType();
		
		String key = "01";
		if(invoice.isWithholdingFarmer() && !invoice.isIsp()) key = "02";
		if(invoice.isVatAccrualPayment()) key = "07";
		if(invoice.isIntracommunity()) key = "09";
		if(invoice.isExtracommunity()) key = "13";
		
		clave.setClaveRegimenIvaOpTrascendencia(key);
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
				
				tax.setBase(AonMathUtils.round(tax.getBase()));
				if(invoice.isExtracommunity()) {
					tax.setPercentage(0.0);
					tax.setQuota(0.0);
					tax.setDeductiblePercent(0.0);
					tax.setDeductibleQuota(0.0);
				}
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
				r.setCuotaIVADeducible(Double.toString(AonMathUtils.round(tax.getDeductibleQuota())));
				r.setCuotaIVASoportada(Double.toString(AonMathUtils.round(tax.getQuota())));
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
		if(AonStringUtils.isBlank(invoice.getRegistryDocument())) {
			return error(new TbaiException(TBAIError.AON_001));
		} 
		LinkedList<Invoice> invoices = new LinkedList<>();
		invoices.add(invoice);
		boolean mod = invoice.getInvoiceInfo().getStatus().isAccepted() || invoice.getInvoiceInfo().getStatus().isAcceptedWithErrors();
		return alta(tbaiConfiguration, company, invoices, mod);
	}
	
	public LROEResponse alta(TbaiConfiguration tbaiConfiguration, Company company, List<Invoice> invoices, boolean mod) {
		try {
			LROEInfo info = buildInfo(mod ? OperacionEnum.M_00 : OperacionEnum.A_00, getEjercicio(tbaiConfiguration, invoices.get(0)));
			final LROEPJ240FacturasRecibidasAltaModifPeticion p240 = build(tbaiConfiguration, company, invoices, info); 
			final JAXBContext jaxbContext = JAXBContext.newInstance( LROEPJ240FacturasRecibidasAltaModifPeticion.class );
			final Marshaller jaxbMarshaller   = jaxbContext.createMarshaller();	

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( p240, bos );
			
			byte[] xml = bos.toByteArray();

			Document doc = getDocument(xml);
			System.out.println(toString(doc));

			DataRequest dataRequest = LroeData.saveRequest(company.getDomain(), new User().setLogin(""), invoices, info, xml);
			byte[] data = toGzip(xml);
			return send(tbaiConfiguration, buildJSON(company, info), data).setDataRequest(dataRequest);
		} catch (Exception e) {
			return error(e);
		}
	}
	
	public LROEInfo buildInfo(OperacionEnum operacion, Integer ejercicio) {
		return new LROEInfo(MODEL_240, CAPITULO, null, operacion, ejercicio);
	}
	
	private LROEPJ240FacturasRecibidasAnulacionPeticion buildBaja(Company company, Invoice invoice, LROEInfo info) {	
		LROEPJ240FacturasRecibidasAnulacionPeticion lroe = new LROEPJ240FacturasRecibidasAnulacionPeticion();
		lroe.setCabecera(buildCabecera(company, info));
		AnulacionesFacturasRecibidasType anulaciones = new AnulacionesFacturasRecibidasType();
		AnulacionFacturaRecibidaType anulacion = new AnulacionFacturaRecibidaType();

		IDFacturaConEmisorType factura = new IDFacturaConEmisorType();

		factura.setEmisorFacturaRecibida(buildEmisorAnulacion(invoice));
		factura.setFechaExpedicionFactura(AonDateUtils.format(invoice.getIssueDate(), DATE_FORMAT));
//		factura.setSerieFactura(invoice.getSeries());
		String reference = invoice.getReferenceCode().length() > 20 ? invoice.getReferenceCode().substring(0, 20) : invoice.getReferenceCode();
		if(invoice.isRectifier()) {
			factura.setSerieFactura(reference.substring(0, 1));
			factura.setNumFactura(reference.substring(1));
		} else factura.setNumFactura(reference);
		anulacion.setIDRecibida(factura);
		
		anulaciones.getFacturaRecibida().add(anulacion);
		lroe.setFacturasRecibidas(anulaciones);
		return lroe;
	}
	
	public LROEResponse anulacion(Company company, TbaiConfiguration tbaiConfiguration, Invoice invoice) throws StatusCodeException {
		try {
			LROEInfo info = buildInfo(OperacionEnum.AN_0, getEjercicio(tbaiConfiguration, invoice));
			final LROEPJ240FacturasRecibidasAnulacionPeticion p240 = buildBaja(company, invoice, info); 
			final JAXBContext jaxbContext = JAXBContext.newInstance( LROEPJ240FacturasRecibidasAnulacionPeticion.class );
			final Marshaller jaxbMarshaller   = jaxbContext.createMarshaller();	

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( p240, bos );
			byte[] xml = bos.toByteArray();
			
			Document doc = getDocument(xml);
			System.out.println(toString(doc));
			
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
		fecha.setHasta(AonDateUtils.format(new Date(), DATE_FORMAT));
		cabecera.setFechaExpedicionFactura(fecha);
		String reference = invoice.getReferenceCode().length() > 20 ? invoice.getReferenceCode().substring(0, 20) : invoice.getReferenceCode();
		if(invoice.isRectifier()) {
			cabecera.setSerieFactura(reference.substring(0, 1));
			cabecera.setNumFactura(reference.substring(1));
		} else cabecera.setNumFactura(reference);
		return cabecera;
	}
	
	public boolean consulta(TbaiConfiguration tbaiConfiguration, Company company, Invoice invoice) {
		try {
			LROEInfo info = buildInfo(OperacionEnum.C_00, getEjercicio(tbaiConfiguration, invoice));
			LROEPJ240FacturasRecibidasConsultaPeticion lroe = buildConsulta(company, invoice, info);
			final JAXBContext jaxbContext = JAXBContext.newInstance( LROEPJ240FacturasRecibidasConsultaPeticion.class );
			final Marshaller jaxbMarshaller   = jaxbContext.createMarshaller();	

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( lroe, bos );
			byte[] xml = bos.toByteArray();
			
			Document doc = getDocument(xml);
			System.out.println(toString(doc));
			
			byte[] data = toGzip(xml);
			LROEResponse response = sendConsulta(tbaiConfiguration, buildJSON(company, info), data);

			LROEPJ240FacturasRecibidasConsultaRespuesta resp = (LROEPJ240FacturasRecibidasConsultaRespuesta) 
                    unmarshall(LROEPJ240FacturasRecibidasConsultaRespuesta.class, response.getResponseDataStr());
            
            if(SiNoEnum.S.equals(resp.getResultadoConsulta().getExistenRegistros())) {
                DataRequest request = LroeData.saveRequest(company.getDomain(), new User().setLogin(""), invoice, info, data);
                response.setDataRequest(request);
                LroeData.saveResponse(company.getDomain(), new User().setLogin(""), invoice, response, info);
            }
            
            return SiNoEnum.S.equals(resp.getResultadoConsulta().getExistenRegistros());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Document getDocument(byte[] data) throws ParserConfigurationException, SAXException, IOException {
		InputStream is = new ByteArrayInputStream(data);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(is);
		return doc;
	}
	
	public String toString(Document doc) {
		try {
			java.io.StringWriter sw = new java.io.StringWriter();
			javax.xml.transform.TransformerFactory tf = javax.xml.transform.TransformerFactory.newInstance();
			javax.xml.transform.Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			transformer.transform(new javax.xml.transform.dom.DOMSource(doc),
					new javax.xml.transform.stream.StreamResult(sw));
			return sw.toString();
		} catch (Exception ex) {
			throw new RuntimeException("Error converting to String", ex);
		}
	}
}
