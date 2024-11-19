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

import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.Person;
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

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.BienAfectoIRPFYOIVAEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.ClaveCodigoFacturaRectificativaEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.ClaveTipoFacturaGastosEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.ClaveTipoRectificativaEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.CountryEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.EstadoRegistroConsultaEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.OperacionEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.OperacionRecargoEquivalenciaORegimenSimplificadoEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.SiNoEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionGastoConFacturaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionesGastosConFacturaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.CabeceraFacturaGastosRecibidasType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.ClavesGastoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DatosFacturaGastoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DetalleRentaIVAGastoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DocumentoPersonaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DocumentoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.FacturaRectificativaImporteType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.FacturasRectificadasSustituidasType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.GastoConFacturaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.GastosConFacturaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDClaveGastoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDFacturaConEmisorType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDFacturaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDOtroType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.RentaIVAGastoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposconsulta.CabeceraGastosConsultaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposconsulta.FechaDesdeHastaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposconsulta.FiltroConsultaGastosConFacturaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_1_1_ingresos_confacturaconsg_consultarespuesta_v1_0_1.LROEPF140IngresosConFacturaConSGConsultaRespuesta;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_2_1_gastos_confactura_altamodifpeticion_v1_0_2.LROEPF140GastosConFacturaAltaModifPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_2_1_gastos_confactura_anulacionpeticion_v1_0_0.LROEPF140GastosConFacturaAnulacionPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_2_1_gastos_confactura_consultapeticion_v1_0_0.LROEPF140GastosConFacturaConsultaPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_2_1_gastos_confactura_consultarespuesta_v1_0_0.LROEPF140GastosConFacturaConsultaRespuesta;
import net.aonsolutions.aon.tbai.LroeData;
import net.aonsolutions.aon.tbai.exceptions.http.StatusCodeException;
import net.aonsolutions.aon.tbai.responses.LROEResponse;

public class LROE140_2_1 extends LROE140 {

	private static final long serialVersionUID = 1L;

	private static final String CAPITULO = "2";
	private static final String SUBCAPITULO = "2.1";
	
	private LROEPF140GastosConFacturaAltaModifPeticion build(TbaiConfiguration tbaiConfiguration, Person person, List<Invoice> invoices, LROEInfo info) {
		LROEPF140GastosConFacturaAltaModifPeticion lroe =  new LROEPF140GastosConFacturaAltaModifPeticion();
		lroe.setCabecera(buildCabecera(person, info));

		GastosConFacturaType gastos = new GastosConFacturaType();
		invoices.stream().forEach(invoice -> {
			gastos.getGasto().add(buildGasto(tbaiConfiguration, invoice));
		});
		lroe.setGastos(gastos);
		return lroe;
	}
		
	private GastoConFacturaType buildGasto(TbaiConfiguration tbaiConfiguration, Invoice invoice) {
		GastoConFacturaType gasto = new GastoConFacturaType();
		gasto.setEmisorFacturaRecibida(buildEmisor(invoice));
		gasto.setCabeceraFactura(buildInvoiceCabecera(tbaiConfiguration, invoice));
		gasto.setDatosFactura(buildFactura(invoice));
		gasto.setRentaIVA(buildRenta(invoice));
		return gasto;
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
	
	private DatosFacturaGastoType buildFactura(Invoice invoice) {
		DatosFacturaGastoType factura = new DatosFacturaGastoType();
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
		ClavesGastoType claves = new ClavesGastoType();
		IDClaveGastoType clave = new IDClaveGastoType();
		
		String key = "01";
		if(invoice.isWithholdingFarmer()) key = "02";
		if(invoice.isVatAccrualPayment()) key = "07";
		if(invoice.isIntracommunity()) key = "09";
		if(invoice.isExtracommunity()) key = "13";
		clave.setClaveRegimenIvaOpTrascendencia(key);
		
		claves.getIDClave().add(clave);
		factura.setClaves(claves);
		return factura;
	}
	
	private RentaIVAGastoType buildRenta(Invoice invoice) {
		RentaIVAGastoType renta = new RentaIVAGastoType();
		for (InvoiceDetail detail : invoice.getDetails()) {
			InvoiceTax tax = detail.getInvoiceTaxes().stream().filter(e -> TaxType.VAT.equals(e.getTaxType())).findFirst().orElse(new InvoiceTax());
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
			DetalleRentaIVAGastoType r = new DetalleRentaIVAGastoType();
			if(invoice.getEpigraph().equals("183320")) invoice.setEpigraph("183321");
			if(invoice.getEpigraph().equals("183310")) invoice.setEpigraph("183311");
			if(invoice.getEpigraph().equals("184950")) invoice.setEpigraph("1849501"); // o 1849502 ??
			r.setEpigrafe(invoice.getEpigraph());

			r.setBaseImponible(Double.toString(tax.getBase()));	
			r.setTipoImpositivo(Double.toString(tax.getPercentage()));
			
			if(tax.getDeductiblePercent() > 0 && tax.getDeductibleQuota() == 0.0) {
				tax.setDeductibleQuota(AonMathUtils.round(tax.getQuota() * tax.getDeductiblePercent() / 100));
			}
			r.setCuotaIVADeducible(Double.toString(AonMathUtils.round(tax.getDeductibleQuota())));
			r.setCuotaIVASoportada(Double.toString(AonMathUtils.round(tax.getQuota())));

			r.setCriterioCobrosYPagos(invoice.isVatAccrualPayment() ? SiNoEnum.S : SiNoEnum.N);

			// BIEN AFECTO !!
			if(invoice.getInvestAsset() != null) { 
				Integer ia = detail.getInvestAssetData() != null && detail.getInvestAssetData().getId() != null 
					? detail.getInvestAssetData().getId() : invoice.getInvestAsset();
				r.setBienAfectoIRPFYOIVA(BienAfectoIRPFYOIVAEnum.I);
				r.setReferenciaBien(Integer.toString(ia));
			}
			
			if(r.getBienAfectoIRPFYOIVA() == null && !AonStringUtils.isBlank(detail.getAccountCode()) && detail.getAccountCode().length() >= 3) {
				r.setConcepto(getConcept(detail.getAccountCode()));
				double importeGastoIRPF = AonMathUtils.round(tax.getBase() * tax.getDeductiblePercent() / 100);
				r.setImporteGastoIRPF(Double.toString(importeGastoIRPF));
			}
					
			r.setInversionSujetoPasivo(invoice.isIsp() ? SiNoEnum.S : SiNoEnum.N);
			if(invoice.isSurcharge()) {
				r.setOperacionEnRecargoDeEquivalenciaORegimenSimplificado(OperacionRecargoEquivalenciaORegimenSimplificadoEnum.E);
				r.setTipoRecargoEquivalencia(Double.toString(tax.getSurcharge()));
				r.setCuotaRecargoEquivalencia(Double.toString(tax.getSurchargeQuota()));				
			}

//			r.setPorcentajeCompensacionREAGYP("");
//			r.setImporteCompensacionREAGYP("");
			
			renta.getDetalleRentaIVA().add(r);
		}

		return renta;
	}
	
	private String getConcept(String account) {
		String concept = account.substring(0,2);
		if(!"65".equals(concept) && !"66".equals(concept) && !"67".equals(concept) && !"69".equals(concept)) {
			concept = account.substring(0, 3);
			if("642".equals(concept)) {
				String aux = account.substring(0,4);
				concept = "6421".equals(aux) ? "64201" : "64202";
			}			
		}
		return concept;
	}

	public LROEResponse alta(TbaiConfiguration tbaiConfiguration, Person person, Invoice invoice) {
		LinkedList<Invoice> invoices = new LinkedList<>();
		invoices.add(invoice);
		boolean mod = invoice.getInvoiceInfo().getStatus().isAccepted() || invoice.getInvoiceInfo().getStatus().isAcceptedWithErrors();
		return alta(tbaiConfiguration, person, invoices, mod);
	}
	
	public LROEResponse alta(TbaiConfiguration tbaiConfiguration, Person person, List<Invoice> invoices, boolean mod) {
		try {
			LROEInfo info = new LROEInfo(MODEL_140, CAPITULO, SUBCAPITULO, 
					mod ? OperacionEnum.M_00 : OperacionEnum.A_00);
			info.setEjercicio(getEjercicio(tbaiConfiguration, invoices.get(0)));
			final LROEPF140GastosConFacturaAltaModifPeticion p140 = build(tbaiConfiguration, person, invoices, info); 
			final JAXBContext jaxbContext = JAXBContext.newInstance( LROEPF140GastosConFacturaAltaModifPeticion.class );
			final Marshaller jaxbMarshaller   = jaxbContext.createMarshaller();	

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( p140, bos );
			byte[] xml = bos.toByteArray();
			DataRequest dataRequest = LroeData.saveRequest(person.getDomain(), new User().setLogin(""), invoices.get(0), info, xml);
			Document doc = getDocument(xml);
			System.out.println(toString(doc));
			byte[] data = toGzip(xml);
			return send(tbaiConfiguration, buildJSON(person, info), data).setDataRequest(dataRequest);
		} catch (Exception e) {
			return error(e);
		}
	}
	
	private LROEPF140GastosConFacturaAnulacionPeticion buildBaja(Person person, Invoice invoice, LROEInfo info) {	
		LROEPF140GastosConFacturaAnulacionPeticion lroe = new LROEPF140GastosConFacturaAnulacionPeticion();
		lroe.setCabecera(buildCabecera(person, info));
		
		AnulacionesGastosConFacturaType anulaciones = new AnulacionesGastosConFacturaType();
		AnulacionGastoConFacturaType anulacion = new AnulacionGastoConFacturaType();

		IDFacturaConEmisorType factura = new IDFacturaConEmisorType();

		factura.setEmisorFacturaRecibida(buildEmisorAnulacion(invoice));
		factura.setFechaExpedicionFactura(AonDateUtils.format(invoice.getIssueDate(), DATE_FORMAT));
//		factura.setSerieFactura(invoice.getSeries());
		String reference = invoice.getReferenceCode().length() > 20 ? invoice.getReferenceCode().substring(0, 20) : invoice.getReferenceCode();
		if(invoice.isRectifier()) {
			factura.setSerieFactura(reference.substring(0, 1));
			factura.setNumFactura(reference.substring(1));
		} else factura.setNumFactura(reference);
		anulacion.setIDGasto(factura);
		
		anulaciones.getGasto().add(anulacion);
		lroe.setGastos(anulaciones);
		return lroe;
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
	
	public LROEInfo buildInfo(OperacionEnum operacion) {
		return new LROEInfo(MODEL_140, CAPITULO, SUBCAPITULO, operacion);
	}
	
	public LROEResponse anulacion(Person person, TbaiConfiguration tbaiConfiguration, Invoice invoice) throws StatusCodeException {
		try {
			LROEInfo info = new LROEInfo(MODEL_140, CAPITULO, SUBCAPITULO, OperacionEnum.AN_0);
			info.setEjercicio(getEjercicio(tbaiConfiguration, invoice));

			final LROEPF140GastosConFacturaAnulacionPeticion p240 = buildBaja(person, invoice, info); 
			final JAXBContext jaxbContext = JAXBContext.newInstance( LROEPF140GastosConFacturaAnulacionPeticion.class );
			final Marshaller jaxbMarshaller   = jaxbContext.createMarshaller();	

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( p240, bos );
			byte[] xml = bos.toByteArray();
			DataRequest dataRequest = LroeData.saveRequest(person.getDomain(), new User().setLogin(""), invoice, info, xml);
			byte[] data = toGzip(xml);
			return send(tbaiConfiguration, buildJSON(person, info), data).setDataRequest(dataRequest);
		} catch (Exception e) {
			return error(e);
		}
	}

	private LROEPF140GastosConFacturaConsultaPeticion buildConsulta(Person person, Invoice invoice, LROEInfo info) {
		LROEPF140GastosConFacturaConsultaPeticion lroe = new LROEPF140GastosConFacturaConsultaPeticion();
		lroe.setCabecera(buildCabecera(person, info));
		FiltroConsultaGastosConFacturaType filtro = new FiltroConsultaGastosConFacturaType(); 
		filtro.setCabeceraFactura(buildCabeceraFactura(invoice));
		filtro.setEmisorFacturaRecibida(buildEmisorAnulacion(invoice));
		if(invoice.getEpigraph().equals("183320")) invoice.setEpigraph("183321");
		if(invoice.getEpigraph().equals("183310")) invoice.setEpigraph("183311");
		if(invoice.getEpigraph().equals("184950")) invoice.setEpigraph("1849501"); // o 1849502 ??
		filtro.setEpigrafe(invoice.getEpigraph());
		filtro.setEstado(EstadoRegistroConsultaEnum.CORRECTO);
		filtro.setNumPaginaConsulta(1);
		lroe.setFiltroConsultaGastosConFactura(filtro);
		return lroe;
	}
	
	private CabeceraGastosConsultaType buildCabeceraFactura(Invoice invoice) {
		CabeceraGastosConsultaType cabecera = new CabeceraGastosConsultaType();
		FechaDesdeHastaType fecha = new FechaDesdeHastaType();
		fecha.setDesde(AonDateUtils.format(invoice.getIssueDate(), DATE_FORMAT));
		fecha.setDesde(AonDateUtils.format(new Date(), DATE_FORMAT));
		cabecera.setFechaExpedicionFactura(fecha);
		
		FechaDesdeHastaType fechaRec = new FechaDesdeHastaType();
		boolean tax = invoice.getTaxDate().before(invoice.getCreationDate());
		fechaRec.setDesde(AonDateUtils.format(tax ? invoice.getTaxDate() : invoice.getCreationDate(), DATE_FORMAT));
		fechaRec.setHasta(AonDateUtils.format(new Date(), DATE_FORMAT));
		cabecera.setFechaRecepcion(fechaRec);
		String reference = invoice.getReferenceCode().length() > 20 ? invoice.getReferenceCode().substring(0, 20) : invoice.getReferenceCode();
		if(invoice.isRectifier()) {
			cabecera.setSerieFactura(reference.substring(0, 1));
			cabecera.setNumFactura(reference.substring(1));
		} else cabecera.setNumFactura(reference);
		return cabecera;
	}
	
	
	public boolean consulta(TbaiConfiguration tbaiConfiguration, Person person, Invoice invoice) {
		try {
			LROEInfo info = buildInfo(OperacionEnum.C_00);
			LROEPF140GastosConFacturaConsultaPeticion lroe = buildConsulta(person, invoice, info);
			final JAXBContext jaxbContext = JAXBContext.newInstance( LROEPF140GastosConFacturaConsultaPeticion.class );
			final Marshaller jaxbMarshaller   = jaxbContext.createMarshaller();	

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( lroe, bos );
			byte[] xml = bos.toByteArray();
			byte[] data = toGzip(xml);
			LROEResponse response = sendConsulta(tbaiConfiguration, buildJSON(person, info), data);
			
			LROEPF140GastosConFacturaConsultaRespuesta resp = (LROEPF140GastosConFacturaConsultaRespuesta) 
                    unmarshall(LROEPF140IngresosConFacturaConSGConsultaRespuesta.class, response.getResponseDataStr());
            
            if(SiNoEnum.S.equals(resp.getResultadoConsulta().getExistenRegistros())) {
                DataRequest request = LroeData.saveRequest(person.getDomain(), new User().setLogin(""), invoice, info, data);
                response.setDataRequest(request);
                LroeData.saveResponse(person.getDomain(), new User().setLogin(""), invoice, response, info);
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
