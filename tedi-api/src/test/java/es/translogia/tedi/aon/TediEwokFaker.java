package es.translogia.tedi.aon;

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

	private static JSONObject optPut(JSONObject json, String key, Integer value) {
		return (faker.random().nextBoolean()) ? json.put(key, value) : json;
	}
	private static JSONObject optPut(JSONObject json, String key, String value) {
		return (faker.random().nextBoolean()) ? json.put(key, value) : json;
	}
	private static JSONObject optPut(JSONObject json, String key, Object[] value) {
		return (faker.random().nextBoolean()) ? json.put(key, value) : json;
	}
	private static JSONObject optPut(JSONObject json, String key, Boolean value) {
		return (faker.random().nextBoolean()) ? json.put(key, value) : json;
	}
	
	public static JSONObject getTediPlan() {
		JSONObject json = new JSONObject();
		optPut(json,IConstants.PLAN, faker.address().streetName());
		optPut(json,IConstants.PERIOD,  faker.random().nextBoolean()?"A":"M");
		return json; 
	}
	
	public static JSONObject getTediAddress() {
		JSONObject json = new JSONObject();
		optPut(json,IConstants.ADDRESS, faker.address().streetName());
		optPut(json,IConstants.CITY, faker.address().city());
		optPut(json,IConstants.COUNTRY, faker.address().countryCode());
		optPut(json,IConstants.PROVINCE, faker.address().state());
		optPut(json,IConstants.POSTAL_CODE, faker.address().zipCode());
		return json; 
	}
	
	public static JSONObject getTediCompany() {
		JSONObject json = new JSONObject()
			.put(IConstants.DOCUMENT, faker.regexify(documentRegexp))
			.put(IConstants.NAME, faker.company().name());
		optPut(json,IConstants.ALIAS, faker.company().catchPhrase());
		optPut(json,IConstants.ACTIVE, faker.random().nextBoolean());
		if (faker.random().nextBoolean()) {
			json.put(IConstants.ADDRESS, getTediPlan());
		}
		
		optPut(json,IConstants.IBAN, faker.finance().iban());
		if (faker.random().nextBoolean()) {
			json.put(IConstants.ADDRESS, getTediAddress());
		}
		return json;
	}
	
	public static JSONObject getTediEmailInfo() {
		JSONObject json = new JSONObject();
		optPut(json,IConstants.ID, faker.internet().uuid());
		optPut(json,IConstants.FROM, new String[] {
				faker.internet().emailAddress(),
				faker.internet().emailAddress()});
		optPut(json,IConstants.FILE_NAME, faker.file().fileName());
		return json;
	}
	
	public static JSONObject getTediFinance() {
		JSONObject json = new JSONObject();
		optPut(json,IConstants.DUE_DATE, TediJSONUtils.formatDate(faker.date().future(10, TimeUnit.DAYS)));
		optPut(json,IConstants.AMOUNT, faker.commerce().price());
		optPut(json,IConstants.IBAN, faker.finance().iban());
		optPut(json,IConstants.PAY_METHOD, TediPayMethod.values()[(faker.random().nextInt(TediPayMethod.values().length))].toString());
		optPut(json,IConstants.PENDING, faker.random().nextBoolean());
		return json;
	}
	
	public static JSONObject getTediRegistry() {
		JSONObject json = new JSONObject();
		optPut(json,IConstants.DOCUMENT, faker.regexify(documentRegexp));
		optPut(json,IConstants.NAME, faker.company().name());
		optPut(json,IConstants.DOCUMENT_COUNTRY, faker.address().countryCode());
		if (faker.random().nextBoolean()) {
			json.put(IConstants.ADDRESS, getTediAddress());
		}
		return json;
	}
	
	public static JSONObject getTediInvoiceDetail() {
		JSONObject json = new JSONObject();
		optPut(json,IConstants.DESCRIPTION, faker.commerce().productName());
		optPut(json,IConstants.QUANTITY, faker.commerce().price(0, 100));
		optPut(json,IConstants.PRICE, faker.commerce().price());
		optPut(json,IConstants.DISCOUNT, faker.commerce().price(0, 100));
		optPut(json,IConstants.AMOUNT, faker.commerce().price(0, 100));
		optPut(json,IConstants.VAT, faker.commerce().price(0, 100));
		optPut(json,IConstants.SURCHARGE, faker.commerce().price(0, 100));
		return json;
	}
	
	public static JSONObject getTediInvoiceTax() {
		JSONObject json = new JSONObject()
			.put(IConstants.TAX, TediTaxType.values()[(faker.random().nextInt(TediTaxType.values().length))].toString())
			.put(IConstants.BASE, faker.commerce().price())
			.put(IConstants.PERCENTAGE, faker.commerce().price(0, 100));
		optPut(json,IConstants.QUOTA, faker.commerce().price());
		optPut(json,IConstants.SURCHARGE, faker.commerce().price(0, 100));
		optPut(json,IConstants.SURCHARGE_QUOTA, faker.commerce().price());
		return json;
	}
	
	public static JSONObject getTediInvoiceFile() {
		JSONObject json = new JSONObject();
		optPut(json,IConstants.URL, faker.internet().url());
		optPut(json,IConstants.THUMB_URL, faker.internet().url());
		optPut(json,IConstants.CONTENT_TYPE, faker.file().mimeType());
		return json;
	}
	
	public static JSONObject getTediInvoice() {
		JSONObject json = new JSONObject()
			.put(IConstants.STATUS, TediInvoiceStatus.values()[(faker.random().nextInt(TediInvoiceStatus.values().length))].toString());
		optPut(json,IConstants.UUID, faker.internet().uuid());
		if (faker.random().nextBoolean()) json.put(IConstants.COMPANY, getTediCompany());
		optPut(json,IConstants.TYPE, TediInvoiceType.values()[(faker.random().nextInt(TediInvoiceType.values().length))].toString());
		optPut(json,IConstants.SERIES, faker.lorem().characters(0, 5));
		optPut(json,IConstants.NUMBER, faker.number().numberBetween(0, 99999999));
		optPut(json,IConstants.REFERENCE, faker.lorem().characters(0, 20));
		optPut(json,IConstants.DATE, TediJSONUtils.formatDate(faker.date().future(10, TimeUnit.DAYS)));
		optPut(json,IConstants.TRANSACTION, TediInvoiceTransaction.values()[(faker.random().nextInt(TediInvoiceTransaction.values().length))].toString());
		optPut(json,IConstants.INVESTMENT, faker.random().nextBoolean());
		optPut(json,IConstants.CATEGORY, TediInvoiceCategory.values()[(faker.random().nextInt(TediInvoiceCategory.values().length))].toString());
		optPut(json,IConstants.TOTAL, faker.commerce().price());
		optPut(json,IConstants.RDOCUMENT, faker.regexify(documentRegexp));
		optPut(json,IConstants.RNAME, faker.company().name());
		if (faker.random().nextBoolean()) json.put(IConstants.SENDER, getTediRegistry());
		if (faker.random().nextBoolean()) json.put(IConstants.RECEIVER, getTediRegistry());
		if (faker.random().nextBoolean()) {
			JSONArray details = new JSONArray();
			int x = faker.random().nextInt(0, 10);
			for (int i = 0; i < x; i++) {
				details.put(TediEwokFaker.getTediInvoiceDetail());
			}
			json.put(IConstants.DETAILS, details);
		}
		if (faker.random().nextBoolean()) {
			JSONArray taxes = new JSONArray();
			int x = faker.random().nextInt(0, 5);
			for (int i = 0; i < x; i++) {
				taxes.put(TediEwokFaker.getTediInvoiceTax());
			}
			json.put(IConstants.TAXES, taxes);
		}
		if (faker.random().nextBoolean()) {
			JSONArray finances = new JSONArray();
			int x = faker.random().nextInt(0, 3);
			for (int i = 0; i < x; i++) {
				finances.put(TediEwokFaker.getTediFinance());
			}
			json.put(IConstants.FINANCES, finances);
		}
		if (faker.random().nextBoolean()) json.put(IConstants.FILE, getTediInvoiceFile());
		optPut(json,IConstants.OLD_STATUS, TediInvoiceStatus.values()[(faker.random().nextInt(TediInvoiceStatus.values().length))].toString());
		optPut(json,IConstants.SOURCE, faker.lorem().characters(0, 10));
		if (faker.random().nextBoolean()) json.put(IConstants.EMAIL, getTediEmailInfo());
		return json;
	}
}
