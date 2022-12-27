package net.aonsolutions.aon.tbai;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Random;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceStatus;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.watson.server.io.AonIOUtils;

public class TbaiEmisionBizkaiaTest {

	private static final String CERT_NAME = "FNMT_AON.p12";
	private static final String CERT_PASSWORD = "aon@FNMT";
	private static final String CERT_TYPE = "AEAT";
	
	private final static String TEST_NIF_140 = "99980200M";
	private final static String TEST_NAME_140 = "8FVCxNbMNm"; 
	private final static String TEST_SURNAME1_140 = "Vux9anjAES"; 
	private final static String TEST_SURNAME2_140 = "EMPTmw3fmi";
	
	private final static String TEST_NIF_240 = "A99802019";
	private final static String TEST_NAME_240 = "4wbLGzaHUvHzMkJm9Z5knRPBKpLKr7"; 
	
	private Invoice buildInvoice() {
		Invoice invoice = new Invoice()
		.setType(InvoiceType.SALES)
		.setSeries("TEST1")
		.setNumber(19)
		.setReferenceCode("TEST1/0000019")
		.setIssueDate(new Date())
		.setTaxDate(new Date())
		.setSecurityLevel(SecurityLevel.OFFICIAL)
		.setRegistryDocument("B66941873")
		.setRegistryDocumentType(DocumentType.CIF)
		.setRegistryDocumentCountry(Country.ES)
		.setRegistryName("TRANSLOGIA DEVELOPMENT, S.L.")
		.setAddress(new RegistryAddress()
				.setAddress("ASDASDASDAS")
				.setNumber("2")
				.setZip("01010"))
		.setRectificationType(RectificationType.NONE)	
		.setTransaction(InvoiceTransactionType.NATIONAL)
		.setSurcharge(false)
		.setWithholding(false)
		.setWithholdingFarmer(false)
		.setVatAccrualPayment(false)
		.setInvestment(false)
		.setService(false)
		.setAdvance(false)
		.setTotal(12.10)
		.setRecorded(false)
		.setCreationDate(new Date())
		.setModificationDate(new Date());
		
		InvoiceDetail invoiceDetail = new InvoiceDetail();
		invoiceDetail.setDescription("Test Detail 1");
		invoiceDetail.setQuantity(1.0);
		invoiceDetail.setPrice(10.0);
		invoiceDetail.setSurcharge(0.0);
		invoiceDetail.setLine((short) 0);
		invoiceDetail.setDiscountExpression("0.0");
		
		InvoiceTax tax = new InvoiceTax();
		tax.setBase(10.0);
		tax.setPercentage(21.0);
		tax.setSurcharge(0.0);
		tax.setQuota(2.1);
		tax.setSurchargeQuota(0.0);
		tax.setTaxType(TaxType.VAT);
		tax.setVatDeductionType(VatDeductionType.WITH_RIGHT);
		tax.setWithholding(false);
		invoiceDetail.getInvoiceTaxes().add(tax);
		invoice.getDetails().add(invoiceDetail);
		
		InvoiceBreakdown ib = new InvoiceBreakdown();
		ib.setBase(10.0);
		ib.setPercentage(21.0);
		ib.setQuota(2.1);
		ib.setSurcharge(0.0);
		ib.setSurchargeQuota(0.0);
		invoice.getBreakdown().add(ib);
		
		return invoice;
	}
	

	private Company getCompany240() {
		Company company = new Company();
		company.setName(TEST_NAME_240);
		company.setDocument(TEST_NIF_240);
		return company;
	}
	
	private Company getCompany140() {
		Company company = new Company();
		company.setName(TEST_NAME_140 + " " 
				+ TEST_SURNAME1_140 + " " 
				+ TEST_SURNAME2_140);
		company.setDocument(TEST_NIF_140);
		return company;
	}
	
	private Person getPerson140() {
		Person person = new Person();
		person.setName(TEST_NAME_140 + " " 
				+ TEST_SURNAME1_140 + " " 
				+ TEST_SURNAME2_140);
		person.setFirstName(TEST_NAME_140);
		person.setFirstSurname(TEST_SURNAME1_140);
		person.setSecondSurname(TEST_SURNAME2_140);
		person.setDocument(TEST_NIF_140);
		return person;
	}
	
	private Certificate getCertificate() throws IOException {
		InputStream is = TbaiEmisionBizkaiaTest.class.getResourceAsStream(CERT_NAME);
		return new Certificate()
				.setData(AonIOUtils.toByteArray(is))
				.setCompany(new Random().nextBoolean())
				.setConfidential(new Random().nextBoolean())
				.setName(CERT_NAME)
				.setPassword(CERT_PASSWORD)
				.setType(CERT_TYPE);
	}
	
	private TbaiConfiguration getTbaiConfiguration() throws IOException {
		return new TbaiConfiguration()
				.setActive(true)
				.setAdministration(Administration.BIZKAIA)
				.setCertificate(getCertificate())
				.setTest(true);
	}
	
	TbaiBlockchain blockchain;
	
//	@Test
//	public void test() {	
//		TbaiMain tbaiMain = new TbaiMain();
//		try {
//			tbaiMain.createEmisionTBAI(getCompany(), buildInvoice(), getTbaiConfiguration());
//		} catch (JAXBException | ParserConfigurationException | SAXException | IOException | TbaiException e) {
//			e.printStackTrace();
//		}
//	}	
	
//	@Test
//	public void certTest() {
//		try (final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("FNMT_AON.p12")){
//	        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
//	        String password = "aon@FNMT";
//	        keystore.load(is, password.toCharArray());
//
//	        Enumeration<String> enumeration = keystore.aliases();
//	        while(enumeration.hasMoreElements()) {
//	            String alias = enumeration.nextElement();
//	            Certificate certificate = keystore.getCertificate(alias);
//	            Boolean a = alias.contains("B01487271") || certificate.toString().contains("B01487271");
//	            System.out.println(a);
//	            System.out.println("alias name: " + alias);
//	            System.out.println(certificate.toString());
//
//	        }
//
//	    } catch (java.security.cert.CertificateException e) {
//	        e.printStackTrace();
//	    } catch (NoSuchAlgorithmException e) {
//	        e.printStackTrace();
//	    } catch (FileNotFoundException e) {
//	        e.printStackTrace();
//	    } catch (KeyStoreException e) {
//	        e.printStackTrace();
//	    } catch (IOException e) {
//	        e.printStackTrace();
//	    }
//	}
//	
//	@Test
//	public void SuccessTest() {
//		try (final InputStream certificateInputStream = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("FNMT_AON.p12")){
//			Company company = buildCompany();
//			Invoice invoice = buildInvoice();
//			
//			byte[] certData = AonIOUtils.toByteArray(certificateInputStream);
//			TbaiConfiguration config = new TbaiConfiguration();
//			com.esferalia.aon.occam.api.model.security.Certificate certificate = new com.esferalia.aon.occam.api.model.security.Certificate();
//			certificate.setCertificate(certData);
//			certificate.setPassword("aon@FNMT");
//			config.setCertificate(certificate);
//			TbaiMain.createEmisionTBAI(company, invoice, config);
//		} catch (StatusCodeException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		System.out.println();
//		System.out.println("Test passed successfully.");
//	}
//	
//	@Test
//	@Ignore("503: Service Unavailable")
//	public void TbaiNotAcceptedTest() {
//		Date d = new Date();
//		start_console(1);
//		test_title("TBAI NOT ACCEPTED");
//
//		try {
//			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("factura.json");
//			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
//			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
//		} catch (StatusCodeException e) {
//			log("SERVER ERROR: ", e.getMessage(), d);
//		} catch (TbaiResponseException e) {
//			System.out.println();
//			log("Test passed successfully", "[" + DataToolkit.int_format(e.getCode() + 0d,2) + "] " + e.getMessage(), d); return;
//		} catch (Exception e) {
//			e.printStackTrace();
//			log("", "Test failed.", d);
//			fail("Unexpected exception " + e);
//		}
//		fail("Unexpected path");
//		log("", "Test failed.", d);
//	}
//
//	@Test
//	public void jsonNotFoundTest() {
//		Date d = new Date();
//		start_console(1);
//		test_title("json not found");
//
//		try {
//			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("factura.txt");
//			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
//			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
//		} catch (JsonNotFoundException e) {
//			log("Test passed successfully", e.getMessage(), d);
//			return;
//		} catch (StatusCodeException e) {
//			log("SERVER ERROR: ", e.getMessage(), d);
//			return;
//		} catch (Exception e) {
//			e.printStackTrace();
//			log("", "Test failed.", d);
//			fail("Unexpected exception " + e);
//		}
//		fail("Unexpected path");
//		log("", "Test failed.", d);
//	}
//
//	@Test
//	public void NullInputStreamTest() {
//		Date d = new Date();
//		start_console(1);
//		test_title("Null input stream");
//
//		try {
//			final InputStream is = null;
//			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
//			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
//		} catch (JsonParseException e) {
//			log("Test passed successfully", e.getMessage(), d);
//			return;
//		} catch (StatusCodeException e) {
//			log("SERVER ERROR: ", e.getMessage(), d);
//			return;
//		} catch (Exception e) {
//			e.printStackTrace();
//			log("", "Test failed.", d);
//			fail("Unexpected exception " + e);
//		}
//		fail("Unexpected path");
//		log("", "Test failed.", d);
//	}
//
//	@Test
//	public void NullInvoiceTest() {
//		Date d = new Date();
//		start_console(1);
//		test_title("Null invoice");
//
//		try {
//			final EmisionInvoice invoice = null;
//			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
//		} catch (ValidationException e) {
//			log("Test passed successfully", e.getMessage(), d);
//			return;
//		} catch (StatusCodeException e) {
//			log("SERVER ERROR: ", e.getMessage(), d);
//			return;
//		} catch (Exception e) {
//			e.printStackTrace();
//			log("", "Test failed.", d);
//			fail("Unexpected exception " + e);
//		}
//		fail("Unexpected path");
//		log("", "Test failed.", d);
//	}
//
//	@Test
//	public void NoSenderTest() {
//		Date d = new Date();
//		start_console(1);
//		test_title("No sender");
//
//		try {
//			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaNoSender.json");
//			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
//			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
//		} catch (ValidationException e) {
//			log("Test passed successfully", e.getMessage(), d);
//			return;
//		} catch (StatusCodeException e) {
//			log("SERVER ERROR: ", e.getMessage(), d);
//			return;
//		} catch (Exception e) {
//			e.printStackTrace();
//			log("", "Test failed.", d);
//			fail("Unexpected exception " + e);
//		}
//
//		fail("Unexpected path");
//		log("", "Test failed.", d);
//	}
//
//	@Test
//	public void NoSenderNIFTest() {
//		Date d = new Date();
//		start_console(1);
//		test_title("No sender NIF");
//
//		try {
//			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaNoSenderNIF.json");
//			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
//			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
//		} catch (ValidationException e) {
//			log("Test passed successfully", e.getMessage(), d);
//			return;
//		} catch (StatusCodeException e) {
//			log("SERVER ERROR: ", e.getMessage(), d);
//			return;
//		} catch (Exception e) {
//			e.printStackTrace();
//			log("", "Test failed.", d);
//			fail("Unexpected exception " + e);
//		}
//		fail("Unexpected path");
//		log("", "Test failed.", d);
//	}
//
//	@Test
//	public void WrongSenderNifTest() {
//		Date d = new Date();
//		start_console(1);
//		test_title("Wrong sender NIF format");
//
//		try {
//			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaWrongSenderNIF.json");
//			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
//			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
//		} catch (ValidationException e) {
//			log("Test passed successfully", e.getMessage(), d);
//			return;
//		} catch (StatusCodeException e) {
//			log("SERVER ERROR: ", e.getMessage(), d);
//			return;
//		} catch (Exception e) {
//			e.printStackTrace();
//			log("", "Test failed.", d);
//			fail("Unexpected exception " + e);
//		}
//		fail("Unexpected path");
//		log("", "Test failed.", d);
//	}
//
//	@Test
//	public void NoSenderNameTest() {
//		Date d = new Date();
//		start_console(1);
//		test_title("No sender name");
//
//		try {
//			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaNoSenderName.json");
//			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
//			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
//		} catch (ValidationException e) {
//			log("Test passed successfully", e.getMessage(), d);
//			return;
//		} catch (StatusCodeException e) {
//			log("SERVER ERROR: ", e.getMessage(), d);
//			return;
//		} catch (Exception e) {
//			e.printStackTrace();
//			log("", "Test failed.", d);
//			fail("Unexpected exception " + e);
//		}
//		fail("Unexpected path");
//		log("", "Test failed.", d);
//	}
//
//	@Test
//	public void NoDateTest() {
//		Date d = new Date();
//		start_console(1);
//		test_title("No date");
//
//		try {
//			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaNoDate.json");
//			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
//
//			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
//		} catch (ValidationException e) {
//			log("Test passed successfully", e.getMessage(), d);
//			return;
//		} catch (StatusCodeException e) {
//			log("SERVER ERROR: ", e.getMessage(), d);
//			return;
//		} catch (Exception e) {
//			e.printStackTrace();
//			log("", "Test failed.", d);
//			fail("Unexpected exception " + e);
//		}
//		fail("Unexpected path");
//		log("", "Test failed.", d);
//	}
//
//	@Test
//	public void WrongDateFormatTest() {
//		Date d = new Date();
//		start_console(1);
//		test_title("Wrong date format");
//
//		try {
//			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaWrongDateFormat.json");
//			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
//
//			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
//		} catch (ValidationException e) {
//			log("Test passed successfully", e.getMessage(), d);
//			return;
//		} catch (StatusCodeException e) {
//			log("SERVER ERROR: ", e.getMessage(), d);
//			return;
//		} catch (Exception e) {
//			e.printStackTrace();
//			log("", "Test failed.", d);
//			fail("Unexpected exception " + e);
//		}
//		fail("Unexpected path");
//		log("", "Test failed.", d);
//	}
//
//	@Test
//	public void NoInvoiceNumberTest() {
//		Date d = new Date();
//		start_console(1);
//		test_title("No invoice number");
//
//		try {
//			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaNoInvoiceNumber.json");
//			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
//
//			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
//		} catch (ValidationException e) {
//			log("Test passed successfully", e.getMessage(), d);
//			return;
//		} catch (StatusCodeException e) {
//			log("SERVER ERROR: ", e.getMessage(), d);
//			return;
//		} catch (Exception e) {
//			e.printStackTrace();
//			log("", "Test failed.", d);
//			fail("Unexpected exception " + e);
//		}
//		fail("Unexpected path");
//		log("", "Test failed.", d);
//	}
//
//	@Test
//	public void NoInvoiceTotalTest() {
//		Date d = new Date();
//		start_console(1);
//		test_title("No invoice total");
//
//		try {
//			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaNoInvoiceTotal.json");
//			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
//
//			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
//		} catch (ValidationException e) {
//			log("Test passed successfully", e.getMessage(), d);
//			return;
//		} catch (StatusCodeException e) {
//			log("SERVER ERROR: ", e.getMessage(), d);
//			return;
//		} catch (Exception e) {
//			e.printStackTrace();
//			log("", "Test failed.", d);
//			fail("Unexpected exception " + e);
//		}
//		fail("Unexpected path");
//		log("", "Test failed.", d);
//	}
//
//	@Test
//	public void NoInvoiceDetails() {
//		Date d = new Date();
//		start_console(1);
//		test_title("No invoice details");
//
//		try {
//			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaNoInvoiceDetails.json");
//			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
//
//			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
//		} catch (ValidationException e) {
//			log("Test passed successfully", e.getMessage(), d);
//			return;
//		} catch (StatusCodeException e) {
//			log("SERVER ERROR: ", e.getMessage(), d);
//			return;
//		} catch (Exception e) {
//			e.printStackTrace();
//			log("", "Test failed.", d);
//			fail("Unexpected exception " + e);
//		}
//		fail("Unexpected path");
//		log("", "Test failed.", d);
//	}
//
//	@Test
//	public void NoInvoiceDetailAmount() {
//		Date d = new Date();
//		start_console(1);
//		test_title("No invoice detail amount");
//
//		try {
//			final InputStream is = TbaiEmisionGipuzkoaTest.class
//					.getResourceAsStream("facturaNoInvoiceDetailAmount.json");
//			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
//
//			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
//		} catch (ValidationException e) {
//			log("Test passed successfully", e.getMessage(), d);
//			return;
//		} catch (StatusCodeException e) {
//			log("SERVER ERROR: ", e.getMessage(), d);
//			return;
//		} catch (Exception e) {
//			e.printStackTrace();
//			log("", "Test failed.", d);
//			fail("Unexpected exception " + e);
//		}
//		fail("Unexpected path");
//		log("", "Test failed.", d);
//	}
//
//	@Test
//	public void NoInvoiceDetailQuantity() {
//		Date d = new Date();
//		start_console(1);
//		test_title("No invoice detail quantity");
//
//		try {
//			final InputStream is = TbaiEmisionGipuzkoaTest.class
//					.getResourceAsStream("facturaNoInvoiceDetailQuantity.json");
//			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
//
//			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
//		} catch (ValidationException e) {
//			log("Test passed successfully", e.getMessage(), d);
//			return;
//		} catch (StatusCodeException e) {
//			log("SERVER ERROR: ", e.getMessage(), d);
//			return;
//		} catch (Exception e) {
//			e.printStackTrace();
//			log("", "Test failed.", d);
//			fail("Unexpected exception " + e);
//		}
//		fail("Unexpected path");
//		log("", "Test failed.", d);
//	}
//
//	@Test
//	public void NoInvoiceDetailPrice() {
//		Date d = new Date();
//		start_console(1);
//		test_title("No invoice detail price");
//
//		try {
//			final InputStream is = TbaiEmisionGipuzkoaTest.class
//					.getResourceAsStream("facturaNoInvoiceDetailPrice.json");
//			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
//
//			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
//		} catch (ValidationException e) {
//			log("Test passed successfully", e.getMessage(), d);
//			return;
//		} catch (StatusCodeException e) {
//			log("SERVER ERROR: ", e.getMessage(), d);
//			return;
//		} catch (Exception e) {
//			e.printStackTrace();
//			log("", "Test failed.", d);
//			fail("Unexpected exception " + e);
//		}
//		fail("Unexpected path");
//		log("", "Test failed.", d);
//	}
//
//	@Test
//	public void NoInvoiceDetailDescription() {
//		Date d = new Date();
//		start_console(1);
//		test_title("No invoice detail description");
//
//		try {
//			final InputStream is = TbaiEmisionGipuzkoaTest.class
//					.getResourceAsStream("facturaNoInvoiceDetailDescription.json");
//			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
//
//			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
//		} catch (ValidationException e) {
//			log("Test passed successfully", e.getMessage(), d);
//			return;
//		} catch (StatusCodeException e) {
//			log("SERVER ERROR: ", e.getMessage(), d);
//			return;
//		} catch (Exception e) {
//			e.printStackTrace();
//			log("", "Test failed.", d);
//			fail("Unexpected exception " + e);
//		}
//		fail("Unexpected path");
//		log("", "Test failed.", d);
//	}
//
//	@Test
//	public void NoInvoiceCategory() {
//		Date d = new Date();
//		start_console(1);
//		test_title("No invoice category");
//
//		try {
//			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaNoInvoiceCategory.json");
//			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
//
//			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
//		} catch (ValidationException e) {
//			log("Test passed successfully", e.getMessage(), d);
//			return;
//		} catch (StatusCodeException e) {
//			log("SERVER ERROR: ", e.getMessage(), d);
//			return;
//		} catch (Exception e) {
//			e.printStackTrace();
//			log("", "Test failed.", d);
//			fail("Unexpected exception " + e);
//		}
//		fail("Unexpected path");
//		log("", "Test failed.", d);
//	}
//
//	@Test
//	public void NoRecievers() {
//		Date d = new Date();
//		start_console(1);
//		test_title("No recievers");
//
//		try {
//			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaNoRecievers.json");
//			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
//
//			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
//		} catch (ValidationException e) {
//			log("Test passed successfully", e.getMessage(), d);
//			return;
//		} catch (StatusCodeException e) {
//			log("SERVER ERROR: ", e.getMessage(), d);
//			return;
//		} catch (Exception e) {
//			e.printStackTrace();
//			log("", "Test failed.", d);
//			fail("Unexpected exception " + e);
//		}
//		fail("Unexpected path");
//		log("", "Test failed.", d);
//	}
//
//	@Test
//	public void NoRecieverAddress() {
//		Date d = new Date();
//		start_console(1);
//		test_title("No reciever address");
//		try {
//			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaNoRecieverAddress.json");
//			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
//
//			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
//		} catch (StatusCodeException e) {
//			log("SERVER ERROR: ", e.getMessage(), d);
//			return;
//		} catch (ValidationException e) {
//			log("Test passed successfully", e.getMessage(), d);
//			return;
//		} catch (Exception e) {
//			e.printStackTrace();
//			log("", "Test failed.", d);
//			fail("Unexpected exception " + e);
//		}
//		fail("Unexpected path");
//		log("", "Test failed.", d);
//	}
//
//	@Test
//	public void NoRecieverNIF() {
//		Date d = new Date();
//		start_console(1);
//		test_title("No reciever NIF");
//
//		try {
//			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaNoRecieverNIF.json");
//			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
//
//			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
//		} catch (ValidationException e) {
//			log("Test passed successfully", e.getMessage(), d);
//			return;
//		} catch (StatusCodeException e) {
//			log("SERVER ERROR: ", e.getMessage(), d);
//			return;
//		} catch (Exception e) {
//			e.printStackTrace();
//			log("", "Test failed.", d);
//			fail("Unexpected exception " + e);
//		}
//		fail("Unexpected path");
//		log("", "Test failed.", d);
//	}
//
//	@Test
//	public void WrongRecieverNIF() {
//		Date d = new Date();
//		start_console(1);
//		test_title("Wrong reciever NIF format");
//
//		try {
//			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaWrongRecieverNIF.json");
//			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
//
//			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
//		} catch (ValidationException e) {
//			log("Test passed successfully", e.getMessage(), d);
//			return;
//		} catch (StatusCodeException e) {
//			log("SERVER ERROR: ", e.getMessage(), d);
//			return;
//		} catch (Exception e) {
//			e.printStackTrace();
//			log("", "Test failed.", d);
//			fail("Unexpected exception " + e);
//		}
//		fail("Unexpected path");
//		log("", "Test failed.", d);
//	}
//
//	@Test
//	public void NoRecieverName() {
//		Date d = new Date();
//		start_console(1);
//		test_title("No reciever name");
//
//		try {
//			final InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream("facturaNoRecieverName.json");
//			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
//			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml", GIPUZKOA);
//		} catch (ValidationException e) {
//			log("Test passed successfully", e.getMessage(), d);
//			return;
//		} catch (StatusCodeException e) {
//			log("SERVER ERROR: ", e.getMessage(), d);
//			return;
//		} catch (Exception e) {
//			e.printStackTrace();
//			log("", "Test failed.", d);
//			fail("Unexpected exception " + e);
//		}
//		fail("Unexpected path");
//		log("", "Test failed.", d);
//	}
//
//	@Test
//	public void NoSignature() {
//		//Date d = new Date();
//		start_console(1);
//		test_title("No signature");
//
//		ConsoleToolkit.log_warning("Not implemented yet.");
//		// fail("Not implemented yet");
//	}
//
//	@Test
//	public void WrongChainingProcess() {
//		//Date d = new Date();
//		start_console(1);
//		test_title("No signature");
//
//		ConsoleToolkit.log_warning("Not implemented yet.");
//		// fail("Not implemented yet");
//	}
}
