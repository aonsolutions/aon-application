package es.translogia.tedi.baloo;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.json.JSONArray;
import org.json.JSONObject;

import es.translogia.tedi.ewok.TediCompany;
import es.translogia.tedi.ewok.TediInvoice;
import es.translogia.tedi.ewok.TediInvoiceStatus;
import es.translogia.tedi.json.TediCompanyJSON;
import es.translogia.tedi.json.TediInvoiceJSON;

public class Tedi extends TediRequest {
	String TEDI_URL;
	String AUTH;
	String INVOICE_BASE;
	String PUT_INVOICE;
	String UPDATE_INVOICE;
	String DOWNLOAD_INVOICE;
	String GET_INVOICE_BY_UUID;
	String GET_VERIFIED_INVOICES;
	String GET_COUNT_INVOICES;
	
	String COMPANY_BASE;
	String GET_COMPANIES_BY_COMPANY;

	String REGISTRY_BASE;
	
	private String token;
	public static Tedi login(String token) {
		return Tedi.login(token,false);
	}
	
	public static Tedi login(String token, boolean snapshot) {
		Tedi tedi = new Tedi(token,snapshot);
		return tedi;
	}
	public static Tedi login(String email, String password) {
		return Tedi.login(email,password,false);
	}
	public static Tedi login(String email, String password, boolean snapshot) {
		Tedi tedi = new Tedi(snapshot);
		String requestData = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
		TediResponse response = tedi.post(tedi.AUTH, "", requestData);
		if (response.ok()) {
			tedi.setToken(response.getJSONObject().getString("session_id"));
		}
		return tedi;
	}

	private Tedi(boolean snapshot) {
		TEDI_URL = TediRequest.getTediURL(snapshot);
		AUTH = TEDI_URL + "/auth";
		INVOICE_BASE = TEDI_URL + "/invoice";
		PUT_INVOICE = INVOICE_BASE;
		UPDATE_INVOICE = INVOICE_BASE;
		DOWNLOAD_INVOICE = INVOICE_BASE + "/d/{0}"; // uuid
		GET_INVOICE_BY_UUID = INVOICE_BASE + "/{0}/{1}/{2}";
		GET_VERIFIED_INVOICES = INVOICE_BASE + "?company={0}&status=" + TediInvoiceStatus.verified;
		COMPANY_BASE = TEDI_URL	+ "/company";
		GET_COMPANIES_BY_COMPANY = COMPANY_BASE + "?company={0}";
		GET_COUNT_INVOICES = INVOICE_BASE + "/count/{0}/{1}"; // '/cout/:company/:status'
		REGISTRY_BASE = TEDI_URL	+ "/registry/{0}";
	}

	private Tedi(String token,boolean snapshot) {
		this(snapshot);
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	private void setToken(String token) {
		this.token = token;
	}

	// INVOICE
	public LinkedList<TediInvoice> getVerifiedInvoices(String company) throws TediException {
		String url = MessageFormat.format(GET_VERIFIED_INVOICES,company);
		TediResponse tediResponse = get(url, getToken());
		if (tediResponse.ok()) {
			JSONArray array = tediResponse.getJSONArray();
			return StreamSupport.stream(array.spliterator(), false).map(r -> TediInvoiceJSON.fromJSON((JSONObject) r))
					.collect(Collectors.toCollection(LinkedList::new));
		}
		throw new TediException(
				"getVerifiedInvoices: " + tediResponse.getResponseCode() + " - " + tediResponse.getResponseMessage());
	}
	
	public Integer getCountInvoices(String company, TediInvoiceStatus status) throws TediException {
		String url = MessageFormat.format(GET_COUNT_INVOICES ,company, status);
		TediResponse tediResponse = get(url, getToken());
		if (tediResponse.ok()) {
			return Integer.parseInt(tediResponse.getContent());
		}
		throw new TediException(
				"getCountInvoices: " + tediResponse.getResponseCode() + " - " + tediResponse.getResponseMessage());
	}

	public TediInvoice getInvoice(String status,String company, String uuid) throws TediException {
		String url = MessageFormat.format(GET_INVOICE_BY_UUID,status.toString(),company,uuid);
		TediResponse tediResponse = get(url, getToken());
		if (tediResponse.ok()) {
			return TediInvoiceJSON.fromJSON(tediResponse.getJSONObject());
		}
		throw new TediException(
				"getInvoice: " + tediResponse.getResponseCode() + " - " + tediResponse.getResponseMessage());
	}

	public TediInvoice putInvoice(TediInvoice invoice) throws TediException {
		JSONObject obj = TediInvoiceJSON.toJSON(invoice);
		TediResponse tediResponse = post(PUT_INVOICE, getToken(), obj.toString());
		if (tediResponse.ok()) {
			return TediInvoiceJSON.fromJSON(tediResponse.getJSONObject());
		}
		throw new TediException(
				"createInvoice: " + tediResponse.getResponseCode() + " - " + tediResponse.getResponseMessage() );
	}
	
	public String getInvoiceAttach(String company, String uuid) throws TediException {
		String url = MessageFormat.format(DOWNLOAD_INVOICE,uuid);
		TediResponse tediResponse = get(url, getToken());
		if (tediResponse.ok()) {
			JSONObject json = tediResponse.getJSONObject();
			String attachUrl = json.optString("url");
			return attachUrl;
		}
		throw new TediException(
				"getInvoiceAttach: " + tediResponse.getResponseCode() + " - " + tediResponse.getResponseMessage());
	}
	
//	
//	public TediInvoice deleteInvoice(String uuid) {
//		return new TediInvoice(delete(TEDI + INVOICE_SRC + "/" + uuid, getToken()));
//	}

	// COMPANY
	public LinkedList<TediCompany> getCompanies(String company) throws TediException{
		String url = MessageFormat.format(COMPANY_BASE, company);

		
		TediResponse tediResponse = get(url, getToken());
		if (tediResponse.ok()) {
			JSONArray array = tediResponse.getJSONArray();
			return StreamSupport.stream(array.spliterator(), false).map(r -> TediCompanyJSON.fromJSON((JSONObject) r))
					.collect(Collectors.toCollection(LinkedList::new));
		}
		throw new TediException(
				"getCompanies: " + tediResponse.getResponseCode() + " - " + tediResponse.getResponseMessage());

		
	}
//	
//	public TediCompany getCompany(String document) {
//		JSONObject json = getObject(TEDI + COMPANY_SRC + "/" + document, getToken());
//		return new TediCompany(json);
//	}
//	
	public TediCompany createCompany(TediCompany company) throws TediException {
		TediResponse response = post(COMPANY_BASE, getToken(), TediCompanyJSON.toJSON(company).toString()); 
		if (response.ok()) {
			return company;
		}
		throw new TediException(
				"createCompany: " + response.getResponseCode() + " - " + response.getResponseMessage());
	}
//	
//	public TediCompany updateCompany(TediCompany company) {
//		return new TediCompany(put(TEDI + COMPANY_SRC + "/" + company.getDocument(), getToken(), company.toJSON().toString()));
//	}
//	
//	public TediCompany removeCompany(String document) {
//		return new TediCompany(delete(TEDI + COMPANY_SRC + "/" + document , getToken()));
//	}

	
	// REGISTRY
	
	public TediCompany getRegistry(String company) throws TediException{
		String url = MessageFormat.format(REGISTRY_BASE, company);

		TediResponse tediResponse = get(url, getToken());
		if (tediResponse.ok()) {
			JSONObject object = tediResponse.getJSONObject();
			return TediCompanyJSON.fromJSON(object);
		}
		throw new TediException(
				"getRegistry: " + tediResponse.getResponseCode() + " - " + tediResponse.getResponseMessage());
	}
}
