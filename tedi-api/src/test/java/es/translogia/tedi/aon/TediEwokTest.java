package es.translogia.tedi.aon;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;


import es.translogia.tedi.ewok.IConstants;
import es.translogia.tedi.ewok.TediAddress;
import es.translogia.tedi.ewok.TediCompany;
import es.translogia.tedi.ewok.TediEmailInfo;
import es.translogia.tedi.ewok.TediFile;
import es.translogia.tedi.ewok.TediFinance;
import es.translogia.tedi.ewok.TediInvoice;
import es.translogia.tedi.ewok.TediInvoiceCategory;
import es.translogia.tedi.ewok.TediInvoiceDetail;
import es.translogia.tedi.ewok.TediInvoiceStatus;
import es.translogia.tedi.ewok.TediInvoiceTax;
import es.translogia.tedi.ewok.TediInvoiceTransaction;
import es.translogia.tedi.ewok.TediInvoiceType;
import es.translogia.tedi.ewok.TediPayMethod;
import es.translogia.tedi.ewok.TediPlan;
import es.translogia.tedi.ewok.TediRegistry;
import es.translogia.tedi.ewok.TediTaxType;
import es.translogia.tedi.json.TediAddressJSON;
import es.translogia.tedi.json.TediCompanyJSON;
import es.translogia.tedi.json.TediEmailInfoJSON;
import es.translogia.tedi.json.TediFileJSON;
import es.translogia.tedi.json.TediFinanceJSON;
import es.translogia.tedi.json.TediInvoiceDetailJSON;
import es.translogia.tedi.json.TediInvoiceJSON;
import es.translogia.tedi.json.TediInvoiceTaxJSON;
import es.translogia.tedi.json.TediJSONUtils;
import es.translogia.tedi.json.TediRegistryJSON;

public class TediEwokTest {
	private static int TIMES = TediEwokFaker.getTimes(); 
	
	static {
		System.out.println( "["+ TIMES + " times each] ");
	}
	@After
	public void afterEach() {
		System.out.println("");
	}
	
	private void assertNulls(Object left, Object right) {
		if (left == null) {
			Assert.assertNull(right);
		}
		if (right == null) {
			Assert.assertNull(left);
		}
	}
	private void assertEquals(Date left, Date right) {
		assertNulls(left, right);
		if (left != null && right != null) {
			Assert.assertEquals(left, right);
		}
	}
	private void assertEquals(String left, String right) {
		if (left != null && right != null) {
			Assert.assertEquals(left, right);
		}
	}
	private void assertEquals(Double left, Double right) {
		if (left != null && right != null) {
			Assert.assertEquals(left, right,0);
		}
	}
	private void assertEquals(Integer left, Integer right) {
		if (left != null && right != null) {
			Assert.assertEquals(left, right,0);
		}
	}
	private void assertEquals(Boolean left, Boolean right) {
		if (left != null && right != null) {
			Assert.assertEquals(left, right);
		}
	}
	private void assertEquals(Enum<?> left, Enum<?> right) {
		if (left != null && right != null) {
			Assert.assertEquals(left, right);
		}
	}
	
	@Test
	public void testTediAddress() throws Exception {
		System.out.print( "\t-test TediAddress ");
		for (int i = 0; i < TIMES; i++) {
			JSONObject json = TediEwokFaker.getTediAddress();
			TediAddress address = TediAddressJSON.fromJSON(json);
			tediAddressAssert(address, json);
			tediAddressAssert(address, TediAddressJSON.toJSON(address));
			if (i % 10 == 0) System.out.print( ".");
		}
		System.out.print( " [OK]");
	}
	
	private  void tediAddressAssert(TediAddress address, JSONObject json ) {
		assertEquals(address.getAddress(), json.optString(IConstants.ADDRESS));
		assertEquals(address.getCity(), json.optString(IConstants.CITY));
		assertEquals(address.getCountry(), json.optString(IConstants.COUNTRY));
		assertEquals(address.getProvince(), json.optString(IConstants.PROVINCE));
		assertEquals(address.getPostalCode(), json.optString(IConstants.POSTAL_CODE));
	}
	
	private  void tediPlanAssert(TediPlan plan, JSONObject json ) {
		assertEquals(plan.getPlan(), json.optString(IConstants.PLAN));
		assertEquals(plan.getPeriod(), json.optString(IConstants.PERIOD));
	}

	@Test
	public void testTediCompany() throws Exception {
		System.out.print( "\t-test TediCompany ");
		for (int i = 0; i < TIMES; i++) {
			JSONObject json = TediEwokFaker.getTediCompany();
			TediCompany company = TediCompanyJSON.fromJSON(json);
			tediCompanyAssert(company, json);
			tediCompanyAssert(company, TediCompanyJSON.toJSON(company));
			if (i % 10 == 0) System.out.print( ".");
		}
		System.out.print( " [OK]");
	}
	
	private  void tediCompanyAssert(TediCompany company, JSONObject json ) {
		assertEquals(company.getDocument(), json.optString(IConstants.DOCUMENT));
		assertEquals(company.getName(), json.optString(IConstants.NAME));
		assertEquals(company.getAlias(), json.optString(IConstants.ALIAS));
		assertEquals(company.getActive(), json.optBoolean(IConstants.ACTIVE));
		JSONObject jsonPlan = json.optJSONObject(IConstants.PLAN);
		if (company.getPlan() == null && jsonPlan == null) {
			// OK
		} else {
			tediPlanAssert(company.getPlan(), jsonPlan);
		}
		assertEquals(company.getIban(), json.optString(IConstants.IBAN));
		JSONObject jsonAddress = json.optJSONObject(IConstants.ADDRESS);
		if (company.getAddress() == null && jsonAddress == null) {
			// OK
		} else {
			tediAddressAssert(company.getAddress(), jsonAddress);
		}
	}
	
	@Test
	public void testTediEmailInfo() throws Exception {
		System.out.print( "\t-test TediEmailInfo ");
		for (int i = 0; i < TIMES; i++) {
			JSONObject json = TediEwokFaker.getTediEmailInfo();
			TediEmailInfo emailInfo = TediEmailInfoJSON.fromJSON(json);
			tediEmailInfoAssert(emailInfo, json);
			tediEmailInfoAssert(emailInfo, TediEmailInfoJSON.toJSON(emailInfo));
			if (i % 10 == 0) System.out.print( ".");
		}
		System.out.print( " [OK]");
	}
	
	private  void tediEmailInfoAssert(TediEmailInfo emailInfo, JSONObject json ) {
		assertEquals(emailInfo.getId(), json.optString(IConstants.ID));
		Object o = json.opt(IConstants.FROM);
		if (o != null) {
			if (o instanceof String[]) {
				Assert.assertArrayEquals(emailInfo.getFrom(), (String[]) o);		
			} else {
				Assert.fail("TediEmailInfo FROM is not a string array");
			}
		}
		assertEquals(emailInfo.getFileName(), json.optString(IConstants.FILE_NAME));
	}
	
	@Test
	@Ignore
	public void testTediFinance() throws Exception {
		System.out.print( "\t-test TediFinance ");
		for (int i = 0; i < TIMES; i++) {
			JSONObject json = TediEwokFaker.getTediFinance();
			TediFinance finance = TediFinanceJSON.fromJSON(json);
			tediFinanceAssert(finance, json);
			tediFinanceAssert(finance, TediFinanceJSON.toJSON(finance));
			if (i % 10 == 0) System.out.print( ".");
		}
		System.out.print( " [OK]");
	}
	
	private  void tediFinanceAssert(TediFinance finance, JSONObject json ) {
		try {
			assertEquals(finance.getDueDate(), TediJSONUtils.parseDate( json.optString(IConstants.DUE_DATE)));
			assertEquals(finance.getAmount(), json.optDouble(IConstants.AMOUNT));
			assertEquals(finance.getIban(), json.optString(IConstants.IBAN));
			assertEquals(finance.getPayMethod(), json.optEnum(TediPayMethod.class, IConstants.PAY_METHOD));
			assertEquals(finance.getPending(), json.optBoolean(IConstants.PENDING));
		} catch (Throwable e) {
			System.out.println(finance.getDueDate() );
			System.out.println(TediJSONUtils.parseDate( json.optString(IConstants.DUE_DATE)) );
			throw e;
		}
	}
	
	@Test
	public void testTediRegistry() throws Exception {
		System.out.print( "\t-test TediRegistry ");
		for (int i = 0; i < TIMES; i++) {
			JSONObject json = TediEwokFaker.getTediRegistry();
			TediRegistry registry = TediRegistryJSON.fromJSON(json);
			tediRegistryAssert(registry, json);
			tediRegistryAssert(registry, TediRegistryJSON.toJSON(registry));
			if (i % 10 == 0) System.out.print( ".");
		}
		System.out.print( " [OK]");
	}
	
	private  void tediRegistryAssert(TediRegistry registry, JSONObject json ) {
		assertEquals(registry.getDocument(), json.optString(IConstants.DOCUMENT));
		assertEquals(registry.getDocumentCountry(), json.optString(IConstants.DOCUMENT_COUNTRY));
		assertEquals(registry.getName(), json.optString(IConstants.NAME));
		JSONObject jsonAddress = json.optJSONObject(IConstants.ADDRESS);
		if (registry.getAddress() == null && jsonAddress == null) {
			// OK
		} else {
			tediAddressAssert(registry.getAddress(), jsonAddress);
		}
	}
	
	@Test
	public void testTediInvoiceDetail() throws Exception {
		System.out.print( "\t-test TediInvoiceDetail ");
		for (int i = 0; i < TIMES; i++) {
			JSONObject json = TediEwokFaker.getTediInvoiceDetail();
			TediInvoiceDetail invoiceDetail = TediInvoiceDetailJSON.fromJSON(json);
			tediInvoiceDetailAssert(invoiceDetail, json);
			tediInvoiceDetailAssert(invoiceDetail, TediInvoiceDetailJSON.toJSON(invoiceDetail));
			if (i % 10 == 0) System.out.print( ".");
		}
		System.out.print( " [OK]");
	}

	private  void tediInvoiceDetailAssert(TediInvoiceDetail invoiceDetail, JSONObject json ) {
		try {
			assertEquals(invoiceDetail.getDescription(), json.optString(IConstants.DESCRIPTION));
			assertEquals(invoiceDetail.getQuantity(), json.optDouble(IConstants.QUANTITY));
			assertEquals(invoiceDetail.getPrice(), json.optDouble(IConstants.PRICE));
			assertEquals(invoiceDetail.getDiscount(), json.optDouble(IConstants.DISCOUNT));
			assertEquals(invoiceDetail.getAmount(), json.optDouble(IConstants.AMOUNT));
			assertEquals(invoiceDetail.getVat(), json.optDouble(IConstants.VAT));
			assertEquals(invoiceDetail.getSurcharge(), json.optDouble(IConstants.SURCHARGE));
		} catch (Throwable e) {
			throw e;
		}
	}
	
	@Test
	public void testTediInvoiceTax() throws Exception {
		System.out.print( "\t-test TediInvoiceTax ");
		for (int i = 0; i < TIMES; i++) {
			JSONObject json = TediEwokFaker.getTediInvoiceTax();
			TediInvoiceTax invoiceTax = TediInvoiceTaxJSON.fromJSON(json);
			tediInvoiceTaxAssert(invoiceTax, json);
			tediInvoiceTaxAssert(invoiceTax, TediInvoiceTaxJSON.toJSON(invoiceTax));
			if (i % 10 == 0) System.out.print( ".");
		}
		System.out.print( " [OK]");
	}

	private  void tediInvoiceTaxAssert(TediInvoiceTax invoiceTax, JSONObject json ) {
		try {
			assertEquals(invoiceTax.getTaxType(), json.optEnum(TediTaxType.class, IConstants.TAX));
			assertEquals(invoiceTax.getBase(), json.optDouble(IConstants.BASE));
			assertEquals(invoiceTax.getPercentage(), json.optDouble(IConstants.PERCENTAGE));
			assertEquals(invoiceTax.getQuota(), json.optDouble(IConstants.QUOTA));
			assertEquals(invoiceTax.getSurcharge(), json.optDouble(IConstants.SURCHARGE));
			assertEquals(invoiceTax.getSurchargeQuota(), json.optDouble(IConstants.SURCHARGE_QUOTA));
		} catch (Throwable e) {
			throw e;
		}
	}
	
	@Test
	public void testTediInvoiceFile() throws Exception {
		System.out.print( "\t-test TediInvoiceFile ");
		for (int i = 0; i < TIMES; i++) {
			JSONObject json = TediEwokFaker.getTediInvoiceFile();
			TediFile file = TediFileJSON.fromJSON(json);
			TediFileAssert(file, json);
			TediFileAssert(file, TediFileJSON.toJSON(file));
			if (i % 10 == 0) System.out.print( ".");
		}
		System.out.print( " [OK]");
	}
	
	private  void TediFileAssert(TediFile file, JSONObject json ) {
		assertEquals(file.getUrl(), json.optString(IConstants.URL));
		assertEquals(file.getThumbUrl(), json.optString(IConstants.THUMB_URL));
		assertEquals(file.getContentType(), json.optString(IConstants.CONTENT_TYPE));
	}
	
	@Test
	@Ignore
	public void testTediInvoice() throws Exception {
		System.out.print( "\t-test TediInvoice ");
		for (int i = 0; i < TIMES; i++) {
			JSONObject json = TediEwokFaker.getTediInvoice();
			TediInvoice invoice = TediInvoiceJSON.fromJSON(json);
			tediInvoiceAssert(invoice, json);
			tediInvoiceAssert(invoice, TediInvoiceJSON.toJSON(invoice));
			if (i % 10 == 0) System.out.print( ".");
		}
		System.out.print( " [OK]");
	}
	
	private  void tediInvoiceAssert(TediInvoice invoice, JSONObject json ) {
		assertEquals(invoice.getUuid(), json.optString(IConstants.UUID));
		assertEquals(invoice.getCompany(), json.optString(IConstants.COMPANY));
		assertEquals(invoice.getType(), json.optEnum(TediInvoiceType.class, IConstants.TYPE));
		assertEquals(invoice.getSeries(), json.optString(IConstants.SERIES));
		assertEquals(invoice.getNumber(), json.optInt(IConstants.NUMBER));
		assertEquals(invoice.getReference(), json.optString(IConstants.REFERENCE));
		assertEquals(invoice.getDate(), TediJSONUtils.parseDate( json.optString(IConstants.DATE)));
		assertEquals(invoice.getTransaction(), json.optEnum(TediInvoiceTransaction.class, IConstants.TRANSACTION));
		assertEquals(invoice.getCategory(), json.optEnum(TediInvoiceCategory.class, IConstants.CATEGORY));
		assertEquals(invoice.getTotal(), json.optDouble(IConstants.TOTAL));
		JSONObject jsonSender = json.optJSONObject(IConstants.SENDER);
		if (invoice.getSender() == null && jsonSender == null) {
			// OK
		} else {
			tediRegistryAssert(invoice.getSender(), jsonSender);
		}
		JSONObject jsonReceiver = json.optJSONObject(IConstants.RECEIVER);
		if (invoice.getReceiver() == null && jsonReceiver == null) {
			// OK
		} else {
			tediRegistryAssert(invoice.getReceiver(), jsonReceiver);
		}
		JSONArray jsonDetails = json.optJSONArray(IConstants.DETAILS);
		if (invoice.getDetails() == null && jsonDetails == null) {
			// OK
		} else {
			for (int i = 0; i < jsonDetails.length(); i++) {
				tediInvoiceDetailAssert(invoice.getDetails().get(i), (JSONObject) jsonDetails.get(i));
			}
		}
		JSONArray jsonTaxes = json.optJSONArray(IConstants.TAXES);
		if (invoice.getTaxes() == null && jsonTaxes == null) {
			// OK
		} else {
			for (int i = 0; i < jsonTaxes.length(); i++) {
				tediInvoiceTaxAssert(invoice.getTaxes().get(i), (JSONObject) jsonTaxes.get(i));
			}
		}
		JSONArray jsonFinances = json.optJSONArray(IConstants.FINANCES);
		if (invoice.getFinances() == null && jsonFinances == null) {
			// OK
		} else {
			for (int i = 0; i < jsonFinances.length(); i++) {
				tediFinanceAssert(invoice.getFinances().get(i), (JSONObject) jsonFinances.get(i));
			}
		}
		JSONObject jsonFile = json.optJSONObject(IConstants.FILE);
		if (invoice.getFile() == null && jsonFile == null) {
			// OK
		} else {
			TediFileAssert(invoice.getFile(), jsonFile);
		}
		assertEquals(invoice.getStatus(), json.getEnum(TediInvoiceStatus.class, IConstants.STATUS));
		assertEquals(invoice.getOldStatus(), json.optEnum(TediInvoiceStatus.class, IConstants.OLD_STATUS));
		assertEquals(invoice.getSource(), json.optString(IConstants.SOURCE));
		JSONObject jsonEmail = json.optJSONObject(IConstants.EMAIL);
		if (invoice.getEmail() == null && jsonEmail == null) {
			// OK
		} else {
			tediEmailInfoAssert(invoice.getEmail(), jsonEmail);
		}
		
	}
	
}
