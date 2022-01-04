package net.aonsolutions.aon.tbai.lroe;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.json.JSONObject;
import org.w3c.dom.Document;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.BienAfectoIRPFYOIVAEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.ClaveCodigoFacturaRectificativaEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.ClaveTipoFacturaGastosEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.ClaveTipoRectificativaEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.CountryEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.OperacionEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.OperacionRecargoEquivalenciaORegimenSimplificadoEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.SiNoEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.Cabecera140Type;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.CabeceraFacturaGastosRecibidasType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.ClavesGastoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DatosFacturaGastoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DetalleRentaIVAGastoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DetalleRentaIngresosType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DocumentoPersonaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.FacturaRectificativaImporteType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.FacturasRectificadasSustituidasType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.GastoConFacturaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.GastosConFacturaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDClaveGastoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDFacturaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDOtroType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IngresoConSGCodificadoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IngresosConSGCodificadoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.NIFPersonaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.RentaIVAGastoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.RentaIngresosType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_1_1_ingresos_confacturaconsg_altapeticion_v1_0_2.LROEPF140IngresosConFacturaConSGAltaPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_2_1_gastos_confactura_altamodifpeticion_v1_0_2.LROEPF140GastosConFacturaAltaModifPeticion;
import net.aonsolutions.aon.tbai.exceptions.http.StatusCodeException;
import net.aonsolutions.aon.tbai.responses.TbaiResponse;
import net.aonsolutions.aon.tbai.utils.XMLUtils;

public class LROE140_2 {

	private final static String MODEL_140 = "140";
	private final static String TEST_NIF_140 = "99980200M";
	private final static String TEST_NAME_140 = "8FVCxNbMNm"; 
	private final static String TEST_SURNAME1_140 = "Vux9anjAES"; 
	private final static String TEST_SURNAME2_140 = "EMPTmw3fmi";
	
	private static LROEPF140GastosConFacturaAltaModifPeticion build(Person person, Invoice invoice, byte[] data) {
		LROEPF140GastosConFacturaAltaModifPeticion lroe =  new LROEPF140GastosConFacturaAltaModifPeticion();
		Cabecera140Type cabecera = new Cabecera140Type();
		cabecera.setModelo(MODEL_140);
		NIFPersonaType nif = new NIFPersonaType();
		nif.setNIF(person.getDocument());
		nif.setApellidosNombreRazonSocial(person.getName());
		cabecera.setObligadoTributario(nif);
		cabecera.setEjercicio(2021);
		cabecera.setCapitulo("2");
		cabecera.setSubcapitulo("2.1");
		cabecera.setOperacion(OperacionEnum.A_00);
		cabecera.setVersion("1.0");
		lroe.setCabecera(cabecera);
		
		GastosConFacturaType gastos = new GastosConFacturaType();
		GastoConFacturaType gasto = new GastoConFacturaType();
		gasto.setEmisorFacturaRecibida(buildEmisor(invoice));
		gasto.setCabeceraFactura(buildCabecera(invoice));
		gasto.setDatosFactura(buildFactura(invoice));
		gasto.setRentaIVA(buildRenta(invoice));
		gastos.getGasto().add(gasto);
		lroe.setGastos(gastos);

		return lroe;
	}
	
	private static DocumentoPersonaType buildEmisor(Invoice invoice) {
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
	
	private static CabeceraFacturaGastosRecibidasType buildCabecera(Invoice invoice) {
		CabeceraFacturaGastosRecibidasType cabecera = new CabeceraFacturaGastosRecibidasType();
		cabecera.setTipoFactura(ClaveTipoFacturaGastosEnum.F_1);
		cabecera.setNumFactura(invoice.getReferenceCode());
		cabecera.setFechaExpedicionFactura(AonDateUtils.format(invoice.getIssueDate(), "dd-MM-yyyy"));
		cabecera.setFechaRecepcion(AonDateUtils.format(invoice.getCreationDate(), "dd-MM-yyyy"));
		if(invoice.isRectified()) {
			FacturaRectificativaImporteType rectificativa = new FacturaRectificativaImporteType(); 
			rectificativa.setCodigo(ClaveCodigoFacturaRectificativaEnum.R_1); 
			rectificativa.setTipo(ClaveTipoRectificativaEnum.I); // por diferencia o por sustitucion
			cabecera.setFacturaRectificativa(rectificativa);
				
			FacturasRectificadasSustituidasType rectificadas = new FacturasRectificadasSustituidasType();
			IDFacturaType rectificada = new IDFacturaType();
			rectificada.setSerieFactura(invoice.getRectificationInvoiceSeries());
			rectificada.setNumFactura(invoice.getRectificationInvoiceNumber().toString());
			rectificada.setFechaExpedicionFactura(AonDateUtils.format(invoice.getRectificationInvoiceDate(), "dd-MM-yyyy"));
			rectificadas.getIDFacturaRectificadaSustituida().add(rectificada);
			cabecera.setFacturasRectificadasSustituidas(rectificadas);
		}
		return cabecera;
	}
	
	private static DatosFacturaGastoType buildFactura(Invoice invoice) {
		DatosFacturaGastoType factura = new DatosFacturaGastoType();
		Double total = invoice.getBreakdown().stream().filter(f -> TaxType.VAT.equals(f.getTaxType()))
		.mapToDouble(r -> {
			if(r.getPercentage() > 0 && r.getQuota() == 0.0) {
				r.setQuota(AonMathUtils.round(r.getBase() * r.getPercentage() / 100));
			}
			return AonMathUtils.round(r.getBase() + r.getQuota() + r.getSurchargeQuota());
		}).sum();
		
		factura.setDescripcionOperacion("Factura " + invoice.getReferenceCode());
		factura.setImporteTotalFactura(total.toString());
		ClavesGastoType claves = new ClavesGastoType();
		IDClaveGastoType clave = new IDClaveGastoType();
		clave.setClaveRegimenIvaOpTrascendencia("01");
		claves.getIDClave().add(clave);
		factura.setClaves(claves);
		return factura;
	}
	
	private static RentaIVAGastoType buildRenta(Invoice invoice) {
		RentaIVAGastoType renta = new RentaIVAGastoType();
		for (InvoiceDetail detail : invoice.getDetails()) {
			InvoiceTax tax = detail.getInvoiceTaxes().stream().filter(e -> TaxType.VAT.equals(e.getTaxType())).findFirst().get();
			InvoiceTax irpf = detail.getInvoiceTaxes().stream().filter(e -> TaxType.RETENTION.equals(e.getTaxType())).findFirst().get();
			DetalleRentaIVAGastoType r = new DetalleRentaIVAGastoType();
			r.setEpigrafe(invoice.getEpigraph());
			r.setConcepto(detail.getDescription());
			r.setBaseImponible(Double.toString(tax.getBase()));	
			r.setTipoImpositivo(Double.toString(tax.getPercentage()));
			r.setCuotaIVADeducible(Double.toString(tax.getDeductibleQuota()));
			r.setCuotaIVASoportada(Double.toString(tax.getQuota()));

			r.setCriterioCobrosYPagos(invoice.isVatAccrualPayment() ? SiNoEnum.S : SiNoEnum.N);
			if(irpf != null)
				r.setImporteGastoIRPF(Double.toString(irpf.getQuota()));
			
			r.setInversionSujetoPasivo(invoice.isIsp() ? SiNoEnum.S : SiNoEnum.N);
			if(invoice.isSurcharge()) {
				r.setOperacionEnRecargoDeEquivalenciaORegimenSimplificado(OperacionRecargoEquivalenciaORegimenSimplificadoEnum.E);
				r.setTipoRecargoEquivalencia(Double.toString(tax.getSurcharge()));
				r.setCuotaRecargoEquivalencia(Double.toString(tax.getSurchargeQuota()));				
			}

//			r.setPorcentajeCompensacionREAGYP("");
//			r.setImporteCompensacionREAGYP("");
			if(invoice.getInvestAsset() != null) {
				r.setBienAfectoIRPFYOIVA(BienAfectoIRPFYOIVAEnum.I);
				r.setReferenciaBien(Integer.toString(detail.getInvestAssetData().getId()));
			}
			
			renta.getDetalleRentaIVA().add(r);
		}

		return renta;
	}
	
	private static JSONObject buildJSON(Person person) {
		JSONObject json = new JSONObject();
		json.put(IJsonNames.CON, "LROE");
		json.put(IJsonNames.APA, "2.1");
		JSONObject json2 = new JSONObject();
		json2.put(IJsonNames.NIF, person.getDocument()); // TEST_NIF_140);
		json2.put(IJsonNames.NRS, person.getFirstName()); // TEST_NAME_140);
		json2.put(IJsonNames.AP1, person.getFirstSurname()); // TEST_SURNAME1_140);
		json2.put(IJsonNames.AP2, person.getSecondSurname()); // TEST_SURNAME2_140);
		json.put(IJsonNames.INTE, json2);

		JSONObject drs = new JSONObject();
		drs.put(IJsonNames.MODE, MODEL_140);
		drs.put(IJsonNames.EJER, AonDateUtils.getYear(new Date()));
		json.put(IJsonNames.DRS, drs);
		return json;
	}
	
	public static TbaiResponse alta(TbaiConfiguration tbaiConfiguration, Person person, Invoice invoice, byte[] xml) throws StatusCodeException {
		try {
			Document doc = XMLUtils.getDocument(xml);
			String sign = doc.getElementsByTagName("ds:SignatureValue").item(0).getTextContent();
			
			final LROEPF140GastosConFacturaAltaModifPeticion p140 = build(person, invoice, xml); 
			final JAXBContext jaxbContext = JAXBContext.newInstance( LROEPF140GastosConFacturaAltaModifPeticion.class );
			final Marshaller jaxbMarshaller   = jaxbContext.createMarshaller();	

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( p140, bos );
			byte[] data = bos.toByteArray();
			return LROE.send(tbaiConfiguration, buildJSON(person), data, sign);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void modificacion(TbaiConfiguration tbaiConfiguration, Invoice invoice, byte[] xml)  {

	}
	
	public static void anulacion(TbaiConfiguration tbaiConfiguration, Invoice invoice, byte[] xml)  {

	}
}
