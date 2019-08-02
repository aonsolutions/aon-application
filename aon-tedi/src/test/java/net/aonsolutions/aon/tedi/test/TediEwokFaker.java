package net.aonsolutions.aon.tedi.test;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.javafaker.Faker;

import es.translogia.tedi.ewok.IConstants;
import es.translogia.tedi.ewok.TediInvoiceCategory;
import es.translogia.tedi.ewok.TediInvoiceStatus;
import es.translogia.tedi.ewok.TediInvoiceTransaction;
import es.translogia.tedi.ewok.TediInvoiceType;
import es.translogia.tedi.ewok.TediPayMethod;
import es.translogia.tedi.ewok.TediTaxType;
import es.translogia.tedi.json.TediJSONUtils;

public class TediEwokFaker {
	private static Faker faker = new Faker(new Locale("es"));
	private static String documentRegexp = "(\\d|[XYZ])\\d{7}[A-Z]";
	
	public static int getTimes() {
		return faker.random().nextInt(0, 1000);
	}
	private static boolean opt(int x) {
		return (x >= faker.random().nextInt(0,100));
	}
	private static JSONObject optPut(int x,JSONObject json, String key, Integer value) {
		return (opt(x)) ? json.put(key, value) : json;
	}
	private static JSONObject optPut(int x,JSONObject json, String key, String value) {
		return (opt(x)) ? json.put(key, value) : json;
	}
	private static JSONObject optPut(int x,JSONObject json, String key, Object[] value) {
		return (opt(x)) ? json.put(key, value) : json;
	}
	private static JSONObject optPut(int x,JSONObject json, String key, Boolean value) {
		return (opt(x)) ? json.put(key, value) : json;
	}
	
	public static JSONObject getTediAddress() {
		JSONObject json = new JSONObject();
		optPut(70,json,IConstants.ADDRESS, faker.address().streetName());
		optPut(70,json,IConstants.CITY, faker.address().city());
		optPut(30,json,IConstants.COUNTRY, faker.address().countryCode());
		optPut(70,json,IConstants.PROVINCE, faker.address().state());
		optPut(70,json,IConstants.POSTAL_CODE, faker.address().zipCode());
		return json; 
	}
	
	public static JSONObject getTediCompany() {
		JSONObject json = new JSONObject()
			.put(IConstants.DOCUMENT, faker.regexify(documentRegexp))
			.put(IConstants.NAME, faker.company().name());
		optPut(20,json,IConstants.ALIAS, faker.company().catchPhrase());
		optPut(90,json,IConstants.ACTIVE, faker.random().nextBoolean());
		optPut(10,json,IConstants.PLAN, faker.address().zipCode());
		optPut(30,json,IConstants.IBAN, faker.finance().iban());
		if (opt(60)) {
			json.put(IConstants.ADDRESS, getTediAddress());
		}
		return json;
	}
	
	public static JSONObject getTediEmailInfo() {
		JSONObject json = new JSONObject();
		optPut(20,json,IConstants.ID, faker.internet().uuid());
		optPut(70,json,IConstants.FROM, new String[] {
				faker.internet().emailAddress(),
				faker.internet().emailAddress()});
		optPut(30,json,IConstants.FILE_NAME, faker.file().fileName());
		return json;
	}
	
	public static JSONObject getTediFinance() {
		JSONObject json = new JSONObject();
		optPut(90,json,IConstants.DUE_DATE, TediJSONUtils.formatDate(faker.date().future(10, TimeUnit.DAYS)));
		optPut(95,json,IConstants.AMOUNT, faker.commerce().price());
		optPut(50,json,IConstants.IBAN, faker.finance().iban());
		optPut(90,json,IConstants.PAY_METHOD, TediPayMethod.values()[(faker.random().nextInt(TediPayMethod.values().length))].toString());
		optPut(90,json,IConstants.PENDING, faker.random().nextBoolean());
		return json;
	}
	
	public static JSONObject getTediRegistry() {
		JSONObject json = new JSONObject();
		optPut(90,json,IConstants.DOCUMENT, faker.regexify(documentRegexp));
		optPut(90,json,IConstants.NAME, faker.company().name());
		optPut(40,json,IConstants.DOCUMENT_COUNTRY, faker.address().countryCode());
		if (opt(60)) {
			json.put(IConstants.ADDRESS, getTediAddress());
		}
		return json;
	}
	
	public static JSONObject getTediInvoiceDetail() {
		JSONObject json = new JSONObject();
		optPut(50,json,IConstants.DESCRIPTION, faker.commerce().productName());
		optPut(90,json,IConstants.QUANTITY, faker.commerce().price(0, 100));
		optPut(90,json,IConstants.PRICE, faker.commerce().price());
		optPut(5,json,IConstants.DISCOUNT, faker.commerce().price(0, 100));
		optPut(90,json,IConstants.BASE, faker.commerce().price(0, 100));
		optPut(90,json,IConstants.VAT, faker.commerce().price(0, 100));
		optPut(5,json,IConstants.SURCHARGE, faker.commerce().price(0, 100));
		return json;
	}
	
	public static JSONObject getTediInvoiceTax() {
		JSONObject json = new JSONObject()
			.put(IConstants.TAX, TediTaxType.values()[(faker.random().nextInt(TediTaxType.values().length))].toString())
			.put(IConstants.BASE, faker.commerce().price())
			.put(IConstants.PERCENTAGE, faker.commerce().price(0, 100));
		optPut(90,json,IConstants.QUOTA, faker.commerce().price());
		optPut(5,json,IConstants.SURCHARGE, faker.commerce().price(0, 100));
		optPut(5,json,IConstants.SURCHARGE_QUOTA, faker.commerce().price());
		return json;
	}
	
	public static JSONObject getTediInvoiceFile() {
		JSONObject json = new JSONObject();
		optPut(50,json,IConstants.URL, faker.internet().url());
		optPut(50,json,IConstants.THUMB_URL, faker.internet().url());
		optPut(50,json,IConstants.CONTENT_TYPE, faker.file().mimeType());
		return json;
	}
	
	public static JSONObject getTediInvoice() {
		JSONObject json = new JSONObject()
			.put(IConstants.STATUS, TediInvoiceStatus.values()[(faker.random().nextInt(TediInvoiceStatus.values().length))].toString());
		optPut(90,json,IConstants.UUID, faker.internet().uuid());
		if (opt(98)) json.put(IConstants.COMPANY, getTediCompany());
		optPut(99,json,IConstants.TYPE, TediInvoiceType.values()[(faker.random().nextInt(TediInvoiceType.values().length))].toString());
		optPut(50,json,IConstants.SERIES, faker.lorem().characters(0, 5));
		optPut(50,json,IConstants.NUMBER, faker.number().numberBetween(0, 99999999));
		optPut(50,json,IConstants.REFERENCE, faker.lorem().characters(0, 20));
		optPut(90,json,IConstants.DATE, TediJSONUtils.formatDate(faker.date().future(10, TimeUnit.DAYS)));
		optPut(50,json,IConstants.TRANSACTION, TediInvoiceTransaction.values()[(faker.random().nextInt(TediInvoiceTransaction.values().length))].toString());
		optPut(50,json,IConstants.INVESTMENT, faker.random().nextBoolean());
		optPut(90,json,IConstants.CATEGORY, TediInvoiceCategory.values()[(faker.random().nextInt(TediInvoiceCategory.values().length))].toString());
		optPut(90,json,IConstants.TOTAL, faker.commerce().price());
		JSONObject reg =  opt(90)?getTediRegistry():null;
		if (opt(95)) json.put(IConstants.SENDER, reg);
		if (opt(95)) json.put(IConstants.RECEIVER, reg);
		if (reg != null) {
			optPut(90,json,IConstants.RDOCUMENT, reg.optString(IConstants.DOCUMENT));
			optPut(90,json,IConstants.RNAME, reg.optString(IConstants.NAME));
		}
		if (opt(90)) {
			JSONArray details = new JSONArray();
			int x = faker.random().nextInt(0, 10);
			for (int i = 0; i < x; i++) {
				details.put(TediEwokFaker.getTediInvoiceDetail());
			}
			json.put(IConstants.DETAILS, details);
		}
		if (opt(90)) {
			JSONArray taxes = new JSONArray();
			int x = faker.random().nextInt(0, 5);
			for (int i = 0; i < x; i++) {
				taxes.put(TediEwokFaker.getTediInvoiceTax());
			}
			json.put(IConstants.TAXES, taxes);
		}
		if (opt(50)) {
			JSONArray finances = new JSONArray();
			int x = faker.random().nextInt(0, 3);
			for (int i = 0; i < x; i++) {
				finances.put(TediEwokFaker.getTediFinance());
			}
			json.put(IConstants.FINANCES, finances);
		}
		if (opt(20)) json.put(IConstants.FILE, getTediInvoiceFile());
		optPut(20,json,IConstants.OLD_STATUS, TediInvoiceStatus.values()[(faker.random().nextInt(TediInvoiceStatus.values().length))].toString());
		optPut(20,json,IConstants.SOURCE, faker.lorem().characters(0, 10));
		if (opt(20)) json.put(IConstants.EMAIL, getTediEmailInfo());
		return json;
	}
}
