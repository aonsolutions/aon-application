package net.aonsolutions.aon.tbai;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
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
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;

import net.aonsolutions.aon.sign.TbaiSigner;
import net.aonsolutions.aon.tbai.sign.TbaiSign;
import net.aonsolutions.aon.tbai.utils.XMLUtils;
import ticketbai.anulacion.AnulaTicketBai;
import ticketbai.emision.TicketBai;
import ticketbai.respuesta.ResultadosValidacion;
import ticketbai.respuesta.Salida;
import ticketbai.respuesta.TicketBaiResponse;

/**
 * Alta y anulacion de facturas emitidas en los entornos de pruebas de TicketBAI
 * de Gipuzkoa y de Alava.
 *
 * Los tests se definen aqui una sola vez y cada territorio los hereda
 * (ver {@link TbaiEmisionGipuzkoaTest} y {@link TbaiEmisionArabaTest}) aportando
 * unicamente su administracion y la licencia de pruebas que le corresponde.
 *
 * Los certificados son los mismos del kit de pruebas de Batuz que usan los tests
 * de LROE (src/test/resources/net/aonsolutions/aon/tbai/lroe), de modo que no se
 * duplican en el proyecto. El NIF emisor de la factura es el del titular del
 * certificado, para que el remitente y el emisor coincidan y no haya avisos de
 * identidad del remitente.
 *
 * Los tests que envian realmente al entorno de pruebas estan etiquetados como
 * "envio", igual que en los tests de LROE, y necesitan acceso a la red. Para
 * excluirlos:
 * mvn test -DexcludedGroups=envio
 *
 * Gipuzkoa admite los certificados del kit; Alava exige un certificado
 * homologado y usa el mismo que los tests de verifactu (ver
 * {@link TbaiEmisionArabaTest#certificadoEnvio()}).
 */
abstract class AbstractTbaiEmisionTest {

	// ***** Certificados del kit de pruebas (los mismos que en los tests de LROE)

	/** Certificado de persona fisica: NIF 99999972C, NUEVOCIUD FICTICIO ACTIVO. */
	protected static final String CERT_PERSONA_FISICA = "lroe/lroe_pruebas_persona_fisica.p12";

	/** Certificado de sello de empresa: entidad S7836107H, IZENPE S.A. */
	protected static final String CERT_SELLO_EMPRESA = "lroe/lroe_pruebas_sello_empresa.p12";

	/** Certificado de representante de entidad: 99999973K en representacion de S7836107H. */
	protected static final String CERT_REPRESENTANTE_ENTIDAD = "lroe/lroe_pruebas_representante_entidad.p12";

	/** PIN comun a los certificados de persona fisica, sello de empresa y representante. */
	protected static final String CERT_PASSWORD = "IZDesa2025";

	/** NIF del titular del certificado de persona fisica. */
	protected static final String NIF_PERSONA_FISICA = "99999972C";

	/** Nombre del titular del certificado de persona fisica. */
	protected static final String NOMBRE_PERSONA_FISICA = "FICTICIO ACTIVO NUEVOCIUD";

	/** NIF de la entidad titular del certificado de sello de empresa. */
	protected static final String NIF_PERSONA_JURIDICA = "S7836107H";

	/** Razon social de la entidad titular del certificado de sello de empresa. */
	protected static final String NOMBRE_PERSONA_JURIDICA = "IZENPE S.A.";

	// ***** Software garante (ver Invoice2tbai)

	/** NIF de la entidad desarrolladora del software de facturacion. */
	protected static final String NIF_ENTIDAD_DESARROLLADORA = "B01487271";

	/** Nombre del software de facturacion. */
	protected static final String NOMBRE_SOFTWARE = "aonSolutions";

	// ***** Datos de la factura de prueba

	private static final String EPIGRAFE = "197330";
	private static final String NIF_DESTINATARIO = "B00000034";
	private static final String NOMBRE_DESTINATARIO = "CONSULTORIA GALDETU";

	// ***** Esquemas publicados por la DFA (documentacion/tbai/Version 1.2.2)

	private static final String XSD_ALTA = "ticketBaiV1-2-2.xsd";
	private static final String XSD_ANULACION = "Anula_ticketBaiV1-2-2.xsd";

	/** Estado de la respuesta cuando el fichero ha sido recibido; el 01 es rechazo. */
	private static final String ESTADO_RECIBIDO = "00";


	// *****************************************************************
	// ********************* [DATOS DEL TERRITORIO] ********************
	// *****************************************************************

	/** Territorio historico al que se envian las facturas. */
	protected abstract Administration administration();

	/**
	 * Licencia TicketBAI que el software debe declarar en el entorno de pruebas del
	 * territorio, facilitada por la Hacienda Foral al registrar el software.
	 */
	protected abstract String licenciaPruebas();

	// *****************************************************************
	// ********************** [CONFIGURACION] **************************
	// *****************************************************************

	/**
	 * Configuracion de comunicacion del territorio en modo test. El modo test se
	 * determina por el texto de la configuracion de TBAI, y es el que lleva el envio
	 * al entorno de pruebas y el que hace que se informe la licencia de pruebas.
	 */
	protected InvoiceCommunicationConfiguration config(String certResource) throws IOException {
		return new InvoiceCommunicationConfiguration()
			.setAdministration(administration())
			.setTbaiData(new EnterpriseData()
				.setId(1)
				.setExpression("TBAI TEST")
				.setStartDate(new Date(0L)))
			.setCertificate(certificate(certResource));
	}

	protected Certificate certificate(String resource) throws IOException {
		try (InputStream is = AbstractTbaiEmisionTest.class.getResourceAsStream(resource)) {
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

	/** Emisor de las facturas: el titular del certificado con el que se envian. */
	protected Company company(String document, String name) {
		return (Company) new Company()
			.setDocument(document)
			.setName(name);
	}

	/**
	 * Factura emitida nacional, no exenta, con una unica base al 21%. El numero de
	 * factura es aleatorio para que cada envio sea una factura nueva y no una que ya
	 * consta en el sistema.
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
				.setZip("01001"))
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

		// La fecha de expedicion es la del dia, que es la que informa Invoice2tbai en el
		// alta y la que debe repetirse despues en la anulacion.
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
		detail.setDescription("Servicio de pruebas TicketBAI");
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
	 * Fichero de alta firmado con el certificado de la configuracion, igual que hace
	 * TbaiMain.createEmisionTBAI. La factura es la primera de la cadena, por lo que
	 * no lleva encadenamiento con una factura anterior.
	 */
	protected byte[] alta(Company company, Invoice invoice, InvoiceCommunicationConfiguration icc) throws Exception {
		return firmar(icc, XMLUtils.marshal(Invoice2tbai.build(company, invoice, icc, new TbaiBlockchain()), TicketBai.class));
	}

	/** Fichero de anulacion firmado de una factura dada de alta previamente. */
	protected byte[] anulacion(Company company, Invoice invoice, InvoiceCommunicationConfiguration icc) throws Exception {
		return firmar(icc, XMLUtils.marshal(Invoice2tbai.buildBaja(company, invoice, icc), AnulaTicketBai.class));
	}

	private byte[] firmar(InvoiceCommunicationConfiguration icc, byte[] xml) throws Exception {
		return TbaiSigner.getInstance().sign(icc, xml);
	}

	// *****************************************************************
	// ******************* [ALTA - SIN ENVIO] **************************
	// *****************************************************************

	@Test
	void altaCumpleEsquema_Test() throws Exception {
		InvoiceCommunicationConfiguration icc = config(CERT_SELLO_EMPRESA);

		byte[] xml = alta(companyPersonaJuridica(), invoice(), icc);

		assertCumpleEsquema(xml, XSD_ALTA);
	}

	@Test
	void altaDePersonaFisicaCumpleEsquema_Test() throws Exception {
		InvoiceCommunicationConfiguration icc = config(CERT_PERSONA_FISICA);

		byte[] xml = alta(companyPersonaFisica(), invoice(), icc);

		assertCumpleEsquema(xml, XSD_ALTA);
	}

	@Test
	void altaConSoftwareGarantePruebas_Test() throws Exception {
		InvoiceCommunicationConfiguration icc = config(CERT_SELLO_EMPRESA);

		TicketBai tbai = Invoice2tbai.build(companyPersonaJuridica(), invoice(), icc, new TbaiBlockchain());

		assertEquals(licenciaPruebas(), tbai.getHuellaTBAI().getSoftware().getLicenciaTBAI(),
			"El fichero de alta no declara la licencia de pruebas del territorio");
		assertEquals(NIF_ENTIDAD_DESARROLLADORA,
			tbai.getHuellaTBAI().getSoftware().getEntidadDesarrolladora().getNIF());
		assertEquals(NOMBRE_SOFTWARE, tbai.getHuellaTBAI().getSoftware().getNombre());
	}

	/**
	 * La primera factura de la cadena no lleva encadenamiento con una anterior; el
	 * resto llevan serie, numero, fecha y firma de la factura precedente.
	 */
	@Test
	void altaEncadenaConLaFacturaAnterior_Test() throws Exception {
		InvoiceCommunicationConfiguration icc = config(CERT_SELLO_EMPRESA);
		Company company = companyPersonaJuridica();
		Invoice primera = invoice();

		TicketBai sinEncadenar = Invoice2tbai.build(company, primera, icc, new TbaiBlockchain());
		assertNull(sinEncadenar.getHuellaTBAI().getEncadenamientoFacturaAnterior(),
			"La primera factura de la cadena no debe llevar encadenamiento");

		String firma = TbaiSign.getSign(alta(company, primera, icc));
		TbaiBlockchain blockchain = new TbaiBlockchain()
			.setDate(AonDateUtils.format(primera.getFiscal().getExpDate(), "dd-MM-yyyy"))
			.setSerie(primera.getSeries())
			.setNumber(Integer.toString(primera.getNumber()))
			.setSignature(firma.substring(0, 100));

		TicketBai encadenada = Invoice2tbai.build(company, invoice(), icc, blockchain);

		assertEquals(primera.getSeries(),
			encadenada.getHuellaTBAI().getEncadenamientoFacturaAnterior().getSerieFacturaAnterior());
		assertEquals(Integer.toString(primera.getNumber()),
			encadenada.getHuellaTBAI().getEncadenamientoFacturaAnterior().getNumFacturaAnterior());
		assertEquals(firma.substring(0, 100),
			encadenada.getHuellaTBAI().getEncadenamientoFacturaAnterior().getSignatureValueFirmaFacturaAnterior());
	}

	/**
	 * El identificador TicketBAI y el codigo QR los calcula el propio software, sin
	 * esperar la respuesta del servicio (ver "Comunicacion a Desarrolladores de
	 * SOFTWARE"). El identificador es TBAI-NIF-ddMMyy-13 primeros caracteres de la
	 * firma-CRC8, y el QR lleva su propio CRC8 al final.
	 */
	@Test
	void altaGeneraIdentificadorTbaiYQr_Test() throws Exception {
		InvoiceCommunicationConfiguration icc = config(CERT_SELLO_EMPRESA);
		Company company = companyPersonaJuridica();
		Invoice invoice = invoice();

		TicketBai tbai = Invoice2tbai.build(company, invoice, icc, new TbaiBlockchain());
		String firma = TbaiSign.getSign(firmar(icc, XMLUtils.marshal(tbai, TicketBai.class)));
		String tbaiId = TbaiSign.buildTbaiId(tbai, firma);

		String fecha = tbai.getFactura().getCabeceraFactura().getFechaExpedicionFactura();
		String esperado = "TBAI-" + company.getDocument() + "-" + fecha.substring(0, 2) + fecha.substring(3, 5)
			+ fecha.substring(8) + "-" + firma.substring(0, 13) + "-";
		assertEquals(esperado + CRC8.calculate(esperado), tbaiId);

		String qr = TbaiUri.getUrlQr(icc) + "?id=" + tbaiId + "&s=" + invoice.getSeries()
			+ "&nf=" + invoice.getNumber() + "&i=" + tbai.getFactura().getDatosFactura().getImporteTotalFactura();
		qr = qr + "&cr=" + CRC8.calculate(qr);

		assertTrue(qr.startsWith(TbaiUri.getUrlQr(icc)), "El QR no apunta al entorno de pruebas del territorio");
		assertTrue(qr.contains("&cr="), "El QR no lleva el CRC8");
	}

	@Test
	void altaSuperaLasValidaciones_Test() throws Exception {
		InvoiceCommunicationConfiguration icc = config(CERT_SELLO_EMPRESA);

		TicketBai tbai = Invoice2tbai.build(companyPersonaJuridica(), invoice(), icc, new TbaiBlockchain());

		assertDoesNotThrow(() -> TbaiValidation.validateEmision(tbai));
	}

	/** La segunda factura de la cadena lleva el encadenamiento con la anterior. */
	@Test
	void altaEncadenadaSuperaLasValidaciones_Test() throws Exception {
		InvoiceCommunicationConfiguration icc = config(CERT_SELLO_EMPRESA);
		Company company = companyPersonaJuridica();
		Invoice primera = invoice();

		TbaiBlockchain blockchain = new TbaiBlockchain()
			.setDate(AonDateUtils.format(primera.getFiscal().getExpDate(), "dd-MM-yyyy"))
			.setSerie(primera.getSeries())
			.setNumber(Integer.toString(primera.getNumber()))
			.setSignature(TbaiSign.getSign(alta(company, primera, icc)).substring(0, 100));

		TicketBai tbai = Invoice2tbai.build(company, invoice(), icc, blockchain);

		assertDoesNotThrow(() -> TbaiValidation.validateEmision(tbai));
	}

	// *****************************************************************
	// ***************** [ANULACION - SIN ENVIO] ***********************
	// *****************************************************************

	@Test
	void anulacionCumpleEsquema_Test() throws Exception {
		InvoiceCommunicationConfiguration icc = config(CERT_SELLO_EMPRESA);

		byte[] xml = anulacion(companyPersonaJuridica(), invoice(), icc);

		assertCumpleEsquema(xml, XSD_ANULACION);
	}

	@Test
	void anulacionSuperaLasValidaciones_Test() throws Exception {
		InvoiceCommunicationConfiguration icc = config(CERT_SELLO_EMPRESA);

		AnulaTicketBai anula = Invoice2tbai.buildBaja(companyPersonaJuridica(), invoice(), icc);

		assertDoesNotThrow(() -> TbaiValidation.validateAnulacion(anula));
	}

	/**
	 * La anulacion identifica la factura por emisor, serie, numero y fecha de
	 * expedicion, que deben ser los mismos con los que se dio de alta.
	 */
	@Test
	void anulacionIdentificaLaFacturaDadaDeAlta_Test() throws Exception {
		InvoiceCommunicationConfiguration icc = config(CERT_SELLO_EMPRESA);
		Company company = companyPersonaJuridica();
		Invoice invoice = invoice();

		TicketBai tbai = Invoice2tbai.build(company, invoice, icc, new TbaiBlockchain());
		AnulaTicketBai anula = Invoice2tbai.buildBaja(company, invoice, icc);

		assertEquals(company.getDocument(), anula.getIDFactura().getEmisor().getNIF());
		assertEquals(company.getName(), anula.getIDFactura().getEmisor().getApellidosNombreRazonSocial());
		assertEquals(tbai.getFactura().getCabeceraFactura().getSerieFactura(),
			anula.getIDFactura().getCabeceraFactura().getSerieFactura());
		assertEquals(tbai.getFactura().getCabeceraFactura().getNumFactura(),
			anula.getIDFactura().getCabeceraFactura().getNumFactura());
		assertEquals(tbai.getFactura().getCabeceraFactura().getFechaExpedicionFactura(),
			anula.getIDFactura().getCabeceraFactura().getFechaExpedicionFactura());
	}

	@Test
	void anulacionConSoftwareGarantePruebas_Test() throws Exception {
		InvoiceCommunicationConfiguration icc = config(CERT_SELLO_EMPRESA);

		AnulaTicketBai anula = Invoice2tbai.buildBaja(companyPersonaJuridica(), invoice(), icc);

		assertEquals(licenciaPruebas(), anula.getHuellaTBAI().getSoftware().getLicenciaTBAI(),
			"El fichero de anulacion no declara la licencia de pruebas del territorio");
		assertEquals(NIF_ENTIDAD_DESARROLLADORA,
			anula.getHuellaTBAI().getSoftware().getEntidadDesarrolladora().getNIF());
		assertEquals(NOMBRE_SOFTWARE, anula.getHuellaTBAI().getSoftware().getNombre());
	}

	// *****************************************************************
	// ***************** [ENVIO AL ENTORNO DE PRUEBAS] *****************
	// *****************************************************************

	@Test
	@Tag("envio")
	void altaEnEntornoDePruebas_Test() throws Exception {
		InvoiceCommunicationConfiguration icc = configEnvio();
		byte[] xml = alta(companyEnvio(), invoice(), icc);
		assertCumpleEsquema(xml, XSD_ALTA);

		Salida salida = enviar(icc, TbaiUri.getUrlEmision(icc), xml);

		assertRecibido(salida);
		assertNotNull(salida.getIdentificadorTBAI(),
			() -> "El servicio no ha devuelto identificador TicketBAI: " + describe(salida));
	}

	@Test
	@Tag("envio")
	void anulacionEnEntornoDePruebas_Test() throws Exception {
		InvoiceCommunicationConfiguration icc = configEnvio();
		Company company = companyEnvio();
		Invoice invoice = invoice();

		// Solo se puede anular una factura que conste en el sistema (error 018), asi que
		// primero se da de alta.
		Salida alta = enviar(icc, TbaiUri.getUrlEmision(icc), alta(company, invoice, icc));
		assertRecibido(alta);

		byte[] xml = anulacion(company, invoice, icc);
		assertCumpleEsquema(xml, XSD_ANULACION);

		Salida baja = enviar(icc, TbaiUri.getUrlAnulacion(icc), xml);

		assertRecibido(baja);
	}

	/**
	 * Configuracion con la que se firman y envian los ficheros al entorno de
	 * pruebas, con el certificado de {@link #certificadoEnvio()}.
	 */
	protected InvoiceCommunicationConfiguration configEnvio() throws IOException {
		return config(CERT_SELLO_EMPRESA).setCertificate(certificadoEnvio());
	}

	/**
	 * Certificado con el que se envian los ficheros. Por defecto el de sello de
	 * empresa del kit de pruebas, que es el que usan los tests de LROE y el que
	 * admite el entorno de pruebas de Gipuzkoa.
	 */
	protected Certificate certificadoEnvio() throws IOException {
		return certificate(CERT_SELLO_EMPRESA);
	}

	/** Emisor de los envios: el titular del certificado con el que se envian. */
	protected Company companyEnvio() {
		return companyPersonaJuridica();
	}

	private Salida enviar(InvoiceCommunicationConfiguration icc, String uri, byte[] xml) throws Exception {
		byte[] response = XMLUtils.send(icc.getCertificate(), uri, xml);
		TicketBaiResponse tbaiResponse = (TicketBaiResponse) XMLUtils.unmarshal(response, TicketBaiResponse.class);
		assertNotNull(tbaiResponse.getSalida(), () -> "Respuesta sin salida: " + texto(response));
		return tbaiResponse.getSalida();
	}

	// *****************************************************************
	// ************************ [VALIDACION] ***************************
	// *****************************************************************

	/**
	 * Comprueba que el fichero firmado cumple el esquema XSD publicado por la DFA.
	 * Los esquemas estan en src/test/resources, junto con una copia local del
	 * esquema de firma que importan (xmldsig-core-schema), para no depender de la
	 * red al validar.
	 */
	protected void assertCumpleEsquema(byte[] xml, String xsdResource) {
		URL xsd = AbstractTbaiEmisionTest.class.getResource("xsd/" + xsdResource);
		assertNotNull(xsd, "No se encuentra el esquema " + xsdResource);
		try {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(xsd);
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(new ByteArrayInputStream(xml)));
		} catch (SAXException | IOException e) {
			fail("El fichero no cumple el esquema " + xsdResource + ": " + e.getMessage()
				+ System.lineSeparator() + texto(xml));
		}
	}

	/**
	 * Comprueba que el servicio ha recibido el fichero (estado 00); el estado 01 es
	 * rechazo. Un fichero recibido puede llevar avisos (NIF emisor que no es
	 * contribuyente del territorio, licencia no registrada, certificado de
	 * dispositivo no registrado) que no suponen rechazo.
	 */
	protected void assertRecibido(Salida salida) {
		assertEquals(ESTADO_RECIBIDO, salida.getEstado(),
			() -> "El fichero ha sido rechazado: " + describe(salida));
	}

	/** Estado, descripcion y avisos de la respuesta, para poder diagnosticar un fallo. */
	protected String describe(Salida salida) {
		return System.lineSeparator() + "Estado: " + salida.getEstado()
			+ System.lineSeparator() + "Descripcion: " + salida.getDescripcion()
			+ System.lineSeparator() + avisos(salida).stream()
				.map(r -> "[" + r.getCodigo() + "] " + r.getDescripcion())
				.collect(Collectors.joining(System.lineSeparator()));
	}

	private List<ResultadosValidacion> avisos(Salida salida) {
		return salida.getResultadosValidacion() == null ? List.of() : salida.getResultadosValidacion();
	}

	private String texto(byte[] xml) {
		return new String(xml, StandardCharsets.UTF_8);
	}

	// *****************************************************************
	// ************************** [EMISORES] ***************************
	// *****************************************************************

	protected Company companyPersonaJuridica() {
		return company(NIF_PERSONA_JURIDICA, NOMBRE_PERSONA_JURIDICA);
	}

	private Company companyPersonaFisica() {
		return company(NIF_PERSONA_FISICA, NOMBRE_PERSONA_FISICA);
	}
}
