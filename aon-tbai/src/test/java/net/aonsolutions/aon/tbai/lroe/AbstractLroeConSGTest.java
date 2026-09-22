package net.aonsolutions.aon.tbai.lroe;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.json.JSONObject;
import org.xml.sax.SAXException;

import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.EnterpriseData;
import com.esferalia.aon.occam.api.model.Iae;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.sign.TbaiSigner;
import net.aonsolutions.aon.tbai.Invoice2tbai;
import net.aonsolutions.aon.tbai.TbaiBlockchain;
import net.aonsolutions.aon.tbai.responses.LROEResponse;
import net.aonsolutions.aon.tbai.utils.XMLUtils;
import ticketbai.emision.TicketBai;

/**
 * Datos y utilidades comunes a los envios de facturas emitidas con software
 * garante al entorno de pruebas del LROE de la Diputacion Foral de Bizkaia
 * (subcapitulos LROE_PF_140_1_1 y LROE_PJ_240_1_1).
 *
 * Los certificados de pruebas son los del kit publicado en la web de Batuz
 * (Batuz_LROE_Ziurtagiriak_KIT_Certificados), y el software garante de pruebas
 * es el que documenta Batuz_LROE_Entorno_Pruebas_Empresas_Desarrolladoras:
 * licencia TBAIBI00000000PRUEBA, NIF A99800005, SOFTWARE GARANTE TICKETBAI
 * PRUEBA version 1.0. Ese software lo informa {@link Invoice2tbai} en cuanto la
 * configuracion es de Bizkaia y esta en modo test.
 *
 * @see net.aonsolutions.aon.tbai.TbaiUri#getUrlEmision(InvoiceCommunicationConfiguration)
 */
abstract class AbstractLroeConSGTest {

	// ***** Software garante de pruebas (Batuz_LROE_Entorno_Pruebas_Empresas_Desarrolladoras_V1_0_9)

	/** Licencia TicketBAI del software de facturacion de pruebas. */
	protected static final String LICENCIA_TBAI_PRUEBAS = "TBAIBI00000000PRUEBA";

	/** NIF de la entidad desarrolladora de pruebas. */
	protected static final String NIF_DESARROLLADORA_PRUEBAS = "A99800005";

	/** Nombre del software de facturacion de pruebas. */
	protected static final String NOMBRE_SOFTWARE_PRUEBAS = "SOFTWARE GARANTE TICKETBAI PRUEBA";

	// ***** Certificados del kit de pruebas

	/** Certificado de persona fisica: NIF 99999972C, NUEVOCIUD FICTICIO ACTIVO. */
	protected static final String CERT_PERSONA_FISICA = "lroe_pruebas_persona_fisica.p12";

	/** Certificado de sello de empresa: entidad S7836107H, IZENPE S.A. */
	protected static final String CERT_SELLO_EMPRESA = "lroe_pruebas_sello_empresa.p12";

	/** Certificado de representante de entidad: 99999973K en representacion de S7836107H. */
	protected static final String CERT_REPRESENTANTE_ENTIDAD = "lroe_pruebas_representante_entidad.p12";

	/** PIN comun a los certificados de persona fisica, sello de empresa y representante. */
	protected static final String CERT_PASSWORD = "IZDesa2025";

	/** NIF del titular del certificado de persona fisica, obligado tributario del modelo 140. */
	protected static final String NIF_PERSONA_FISICA = "99999972C";

	/** NIF de la entidad titular del certificado de sello, obligado tributario del modelo 240. */
	protected static final String NIF_PERSONA_JURIDICA = "S7836107H";

	/** Razon social de la entidad titular del certificado de sello. */
	protected static final String NOMBRE_PERSONA_JURIDICA = "IZENPE S.A.";

	/** Epigrafe de IAE del ejemplo publicado por Batuz para el modelo 140. */
	protected static final String EPIGRAFE = "197330";

	// ***** Destinatario de las facturas de prueba

	private static final String NIF_DESTINATARIO = "B00000034";
	private static final String NOMBRE_DESTINATARIO = "CONSULTORIA GALDETU";

	// *****************************************************************
	// ********************** [CONFIGURACION] **************************
	// *****************************************************************

	/**
	 * Configuracion de comunicacion de Bizkaia en modo test: el modo test se
	 * determina por el texto de la configuracion de TBAI y de LROE, y es el que
	 * lleva el envio a pruesarrerak.bizkaia.eus y hace que se informe el software
	 * garante de pruebas.
	 */
	protected InvoiceCommunicationConfiguration config(String certResource) throws IOException {
		return new InvoiceCommunicationConfiguration()
			.setAdministration(Administration.BIZKAIA)
			.setTbaiData(enterpriseData("TBAI TEST"))
			.setLroeData(enterpriseData("LROE TEST"))
			.setCertificate(certificate(certResource));
	}

	private EnterpriseData enterpriseData(String expression) {
		return new EnterpriseData()
			.setId(1)
			.setExpression(expression)
			.setStartDate(new Date(0L));
	}

	protected Certificate certificate(String resource) throws IOException {
		try (InputStream is = AbstractLroeConSGTest.class.getResourceAsStream(resource)) {
			assertNotNull(is, "No se encuentra el certificado de pruebas " + resource);
			return new Certificate()
				.setData(AonIOUtils.toByteArray(is))
				.setDescription(resource)
				.setPassword(CERT_PASSWORD);
		}
	}

	// *****************************************************************
	// ************************* [FACTURA] *****************************
	// *****************************************************************

	protected Company company(String document, String name) {
		return (Company) new Company()
			.setDocument(document)
			.setName(name);
	}

	/**
	 * Factura emitida nacional, no exenta, con una unica base al 21%. El numero de
	 * factura es aleatorio para que cada envio sea una anotacion nueva y no un
	 * registro duplicado (error B4_2000003).
	 */
	protected Invoice invoice() {
		Date now = new Date();
		int number = ThreadLocalRandom.current().nextInt(1, 100_000_000);
		String series = "T" + Calendar.getInstance().get(Calendar.YEAR);

		Invoice invoice = new Invoice()
			.setType(InvoiceType.SALES)
			.setSeries(series)
			.setNumber(number)
			.setReferenceCode(series + "/" + number)
			.setIssueDate(now)
			.setTaxDate(now)
			.setSecurityLevel(SecurityLevel.OFFICIAL)
			.setRegistryDocument(NIF_DESTINATARIO)
			.setRegistryDocumentType(DocumentType.CIF)
			.setRegistryDocumentCountry(Country.ES)
			.setRegistryName(NOMBRE_DESTINATARIO)
			.setAddress(new RegistryAddress()
				.setAddress("GRAN VIA")
				.setNumber("2")
				.setZip("48001"))
			.setRectificationType(RectificationType.NONE)
			.setTransaction(InvoiceTransactionType.NATIONAL)
			.setSurcharge(false)
			.setWithholding(false)
			.setWithholdingFarmer(false)
			.setVatAccrualPayment(false)
			.setInvestment(false)
			.setService(false)
			.setAdvance(false)
			.setTotal(121.0)
			.setRecorded(false)
			.setCreationDate(now)
			.setModificationDate(now)
			.setEpigraph(EPIGRAFE)
			.setActivity(activity());

		invoice.ensureFiscal().setExpDate(now);
		invoice.getDetails().add(detail());
		invoice.getBreakdown().add(breakdown());
		return invoice;
	}

	private EnterpriseActivity activity() {
		return new EnterpriseActivity()
			.setId(1)
			.setPrincipal(true)
			.setVatRegime(VATRegime.GENERAL)
			.setIae(new Iae("1", EPIGRAFE, "Actividad de pruebas"));
	}

	private InvoiceDetail detail() {
		InvoiceTax tax = new InvoiceTax();
		tax.setBase(100.0);
		tax.setPercentage(21.0);
		tax.setSurcharge(0.0);
		tax.setQuota(21.0);
		tax.setSurchargeQuota(0.0);
		tax.setTaxType(TaxType.VAT);
		tax.setVatDeductionType(VatDeductionType.WITH_RIGHT);

		InvoiceDetail detail = new InvoiceDetail();
		detail.setDescription("Servicio de pruebas LROE");
		detail.setQuantity(1.0);
		detail.setPrice(100.0);
		detail.setLine((short) 0);
		detail.setDiscountExpression("0.0");
		detail.getInvoiceTaxes().add(tax);
		return detail;
	}

	private InvoiceBreakdown breakdown() {
		InvoiceBreakdown breakdown = new InvoiceBreakdown();
		breakdown.setTaxType(TaxType.VAT);
		breakdown.setBase(100.0);
		breakdown.setPercentage(21.0);
		breakdown.setQuota(21.0);
		breakdown.setSurcharge(0.0);
		breakdown.setSurchargeQuota(0.0);
		return breakdown;
	}

	// *****************************************************************
	// ************************ [TICKET BAI] ***************************
	// *****************************************************************

	/**
	 * Genera el fichero TicketBAI de la factura y lo firma con el certificado de la
	 * configuracion, igual que hace TbaiMain.createEmisionTBAI antes de incorporarlo
	 * al LROE. La factura es la primera de la cadena, por lo que no lleva
	 * encadenamiento con una factura anterior.
	 */
	protected byte[] ticketBai(Company company, Invoice invoice, InvoiceCommunicationConfiguration icc) throws Exception {
		TicketBai tbai = Invoice2tbai.build(company, invoice, icc, new TbaiBlockchain());
		byte[] xml = XMLUtils.marshal(tbai, TicketBai.class);
		return TbaiSigner.getInstance().sign(icc, xml);
	}

	// *****************************************************************
	// ************************ [VALIDACION] ***************************
	// *****************************************************************

	/**
	 * Comprueba que la peticion generada cumple el esquema XSD publicado por Batuz.
	 * Los esquemas estan en src/test/resources, junto con los tipos comunes que
	 * importan (batuz_TiposComplejos, batuz_TiposBasicos y batuz_Enumerados).
	 */
	protected void assertCumpleEsquema(byte[] xml, String xsdResource) {
		URL xsd = AbstractLroeConSGTest.class.getResource("xsd/" + xsdResource);
		assertNotNull(xsd, "No se encuentra el esquema " + xsdResource);
		try {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(xsd);
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(new ByteArrayInputStream(xml)));
		} catch (SAXException | IOException e) {
			fail("La peticion no cumple el esquema " + xsdResource + ": " + e.getMessage()
				+ System.lineSeparator() + new String(xml, StandardCharsets.UTF_8));
		}
	}

	/**
	 * Comprueba que el fichero TicketBAI incorporado a la peticion esta firmado y
	 * declara el software garante de pruebas del entorno de Bizkaia.
	 */
	protected void assertTicketBaiConSoftwareGarantePruebas(byte[] ticketBai) {
		String xml = new String(ticketBai, StandardCharsets.UTF_8);
		assertTrue(xml.contains("SignatureValue"), "El fichero TicketBAI no esta firmado");
		assertTrue(xml.contains("<LicenciaTBAI>" + LICENCIA_TBAI_PRUEBAS + "</LicenciaTBAI>"),
			"El fichero TicketBAI no declara la licencia de pruebas " + LICENCIA_TBAI_PRUEBAS);
		assertTrue(xml.contains("<NIF>" + NIF_DESARROLLADORA_PRUEBAS + "</NIF>"),
			"El fichero TicketBAI no declara la entidad desarrolladora de pruebas " + NIF_DESARROLLADORA_PRUEBAS);
		assertTrue(xml.contains(NOMBRE_SOFTWARE_PRUEBAS),
			"El fichero TicketBAI no declara el software garante de pruebas");
	}

	/**
	 * Comprueba la cabecera eus-bizkaia-n3-data del envio segun el apartado 6.1.1 de
	 * Batuz_LROE_Especificaciones_Envio_Masivo.
	 */
	protected void assertCabeceraJson(JSONObject json, String modelo, String nif) {
		org.junit.jupiter.api.Assertions.assertEquals("LROE", json.getString("con"), "concepto");
		org.junit.jupiter.api.Assertions.assertEquals("1.1", json.getString("apa"), "apartado");
		org.junit.jupiter.api.Assertions.assertEquals(nif, json.getJSONObject("inte").getString("nif"), "nif interesado");
		org.junit.jupiter.api.Assertions.assertEquals(modelo, json.getJSONObject("drs").getString("mode"), "modelo");
		org.junit.jupiter.api.Assertions.assertEquals(Calendar.getInstance().get(Calendar.YEAR),
			json.getJSONObject("drs").getInt("ejer"), "ejercicio");
	}

	/**
	 * Comprueba que el servicio de entradas ha aceptado el envio. El estado global
	 * llega en la cabecera eus-bizkaia-n3-tipo-respuesta y el identificativo de la
	 * entrada en eus-bizkaia-n3-identificativo.
	 */
	protected void assertEnvioCorrecto(LROEResponse response) {
		String tipo = response.getJson().optString(LROEResponse.LROE_RESPONSE_TYPE);
		assertTrue("Correcto".equalsIgnoreCase(tipo),
			() -> "El envio no ha sido correcto: " + describe(response));
		assertTrue(AonStringUtils.isNotBlank(response.getJson().optString(LROEResponse.LROE_RESPONSE_ID, null)),
			() -> "El servicio no ha devuelto identificativo de entrada: " + describe(response));
		assertNotNull(response.getData(), () -> "Sin cuerpo de respuesta: " + describe(response));
	}

	/** Cabeceras y cuerpo de la respuesta, para poder diagnosticar un fallo del envio. */
	protected String describe(LROEResponse response) {
		StringBuilder sb = new StringBuilder(System.lineSeparator());
		sb.append(response.getJson().toString(2)).append(System.lineSeparator());
		if (response.getData() != null) {
			sb.append(new String(response.getData(), StandardCharsets.UTF_8));
		}
		return sb.toString();
	}
}
