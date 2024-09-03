package net.aonsolutions.aon.tbai;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Random;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
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

import net.aonsolutions.aon.tbai.lroe.LROE140_2_1;

public class Lroe140_2_1Test {

	private static final String CERT_NAME = "bizkaia.p12";
	private static final String CERT_PASSWORD = "IZDesa2021";
	private static final String CERT_TYPE = "AEAT";
	private final static String TEST_NIF_140 = "99980200M";
	private final static String TEST_NAME_140 = "8FVCxNbMNm"; 
	private final static String TEST_SURNAME1_140 = "Vux9anjAES"; 
	private final static String TEST_SURNAME2_140 = "EMPTmw3fmi";
	
	private Invoice buildInvoice() {
		Invoice invoice = new Invoice()
		.setType(InvoiceType.PURCHASE)
		.setSeries("TEST1")
		.setNumber(19)
		.setReferenceCode("TEST1/0000019")
		.setIssueDate(new Date())
		.setTaxDate(new Date())
		.setSecurityLevel(SecurityLevel.OFFICIAL)
		.setRegistryDocument("04437365K")
		.setRegistryDocumentType(DocumentType.CIF)
		.setRegistryDocumentCountry(Country.ES)
		.setRegistryName("PRUEBA AAA BBB")
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
		.setModificationDate(new Date())
		.setEpigraph("150411");
		
		
		InvoiceDetail invoiceDetail = new InvoiceDetail();
		invoiceDetail.setDescription("Description");
		invoiceDetail.setQuantity(1.0);
		invoiceDetail.setPrice(10.0);
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
		invoiceDetail.getInvoiceTaxes().add(tax);
		invoice.getDetails().add(invoiceDetail);
		
		
		InvoiceBreakdown ib = new InvoiceBreakdown();
		ib.setBase(10.0);
		ib.setPercentage(21.0);
		ib.setQuota(2.1);
		ib.setSurcharge(0.0);
		ib.setSurchargeQuota(0.0);
		invoice.addBreakdown(ib);
		
		return invoice;
	}
	
/*
	private Company getCompany() {
		Company company = new Company();
		company.setName(TEST_NAME_140 + " " + TEST_SURNAME1_140 + " " + TEST_SURNAME2_140);
		company.setDocument(TEST_NIF_140);
		return company;
	}
*/
	
	private Person getPerson() {
		Person person = new Person();
		person.setName(TEST_NAME_140 + " " + TEST_SURNAME1_140 + " " + TEST_SURNAME2_140);
		person.setFirstName(TEST_NAME_140);
		person.setFirstSurname(TEST_SURNAME1_140);
		person.setSecondSurname(TEST_SURNAME2_140);
		person.setDocument(TEST_NIF_140);
		return person;
	}
	
	private Certificate getCertificate() throws IOException {
		InputStream is = TbaiEmisionGipuzkoaTest.class.getResourceAsStream(CERT_NAME);
		return new Certificate()
				.setData(AonIOUtils.toByteArray(is))
				.setConfidential(new Random().nextBoolean())
				.setDescription(CERT_NAME)
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
	
	@Test
	public void test() throws Exception {
		TbaiConfiguration tbaiConfig = getTbaiConfiguration();
		Person person = getPerson();
		Invoice invoice = buildInvoice();
		LROE140_2_1 lroe = new LROE140_2_1();
//		lroe.alta(tbaiConfig, person, invoice);
		lroe.consulta(tbaiConfig, person, invoice);
	}
	
}
