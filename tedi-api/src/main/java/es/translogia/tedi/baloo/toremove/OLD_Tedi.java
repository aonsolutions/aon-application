package es.translogia.tedi.baloo.toremove;

import java.io.InputStream;
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

public class OLD_Tedi extends OLD_TediRequest {
	String TEDI_URL;
	String AUTH;
	String INVOICE_BASE;
	String PUT_INVOICE;
	String UPDATE_INVOICE;
	String DOWNLOAD_INVOICE;
	String GET_INVOICE_BY_UUID;
	String GET_VERIFIED_INVOICES;
	String GET_COUNT_INVOICES;
	String PARSE_INVOICE;
	String PARSE_INVOICE_URL;
	
	String COMPANY_BASE;
	String GET_COMPANIES_BY_COMPANY;

	String REGISTRY_BASE;
	
	private String token;
	public static OLD_Tedi login(String token) {
		return OLD_Tedi.login(token,false);
	}
	
	public static OLD_Tedi login(String token, boolean snapshot) {
		OLD_Tedi tedi = new OLD_Tedi(token,snapshot);
		return tedi;
	}
	public static OLD_Tedi login(String email, String password) {
		return OLD_Tedi.login(email,password,false);
	}
	public static OLD_Tedi login(String email, String password, boolean snapshot) {
		OLD_Tedi tedi = new OLD_Tedi(snapshot);
		String requestData = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
		OLD_TediResponse response = tedi.post(tedi.AUTH, "", requestData);
		if (response.ok()) {
			tedi.setToken(response.getJSONObject().getString("session_id"));
		}
		return tedi;
	}

	private OLD_Tedi(boolean snapshot) {
		TEDI_URL = OLD_TediRequest.getTediURL(snapshot);
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
		PARSE_INVOICE = TEDI_URL + "/parse/{0}";
		PARSE_INVOICE_URL = "https://55evus1cy8.execute-api.eu-west-1.amazonaws.com/default/invoice/parse";
	}

	private OLD_Tedi(String token,boolean snapshot) {
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
	public LinkedList<TediInvoice> getVerifiedInvoices(String company) throws OLD_TediException {
		String url = MessageFormat.format(GET_VERIFIED_INVOICES,company);
		OLD_TediResponse tediResponse = get(url, getToken());
		if (tediResponse.ok()) {
			JSONArray array = tediResponse.getJSONArray();
			return StreamSupport.stream(array.spliterator(), false).map(r -> TediInvoiceJSON.fromJSON((JSONObject) r))
					.collect(Collectors.toCollection(LinkedList::new));
		}
		throw new OLD_TediException(
				"getVerifiedInvoices: " + tediResponse.getResponseCode() + " - " + tediResponse.getResponseMessage());
	}
	
	public Integer getCountInvoices(String company, TediInvoiceStatus status) throws OLD_TediException {
		String url = MessageFormat.format(GET_COUNT_INVOICES ,company, status);
		OLD_TediResponse tediResponse = get(url, getToken());
		if (tediResponse.ok()) {
			return Integer.parseInt(tediResponse.getContent());
		}
		throw new OLD_TediException(
				"getCountInvoices: " + tediResponse.getResponseCode() + " - " + tediResponse.getResponseMessage());
	}

	public TediInvoice getInvoice(String status,String company, String uuid) throws OLD_TediException {
		String url = MessageFormat.format(GET_INVOICE_BY_UUID,status.toString(),company,uuid);
		OLD_TediResponse tediResponse = get(url, getToken());
		if (tediResponse.ok()) {
			return TediInvoiceJSON.fromJSON(tediResponse.getJSONObject());
		}
		throw new OLD_TediException(
				"getInvoice: " + tediResponse.getResponseCode() + " - " + tediResponse.getResponseMessage());
	}

	public TediInvoice putInvoice(TediInvoice invoice) throws OLD_TediException {
		JSONObject obj = TediInvoiceJSON.toJSON(invoice);
		OLD_TediResponse tediResponse = post(PUT_INVOICE, getToken(), obj.toString());
		if (tediResponse.ok()) {
			return TediInvoiceJSON.fromJSON(tediResponse.getJSONObject());
		}
		throw new OLD_TediException(
				"createInvoice: " + tediResponse.getResponseCode() + " - " + tediResponse.getResponseMessage() );
	}
	
	public String getInvoiceAttach(String company, String uuid) throws OLD_TediException {
		String url = MessageFormat.format(DOWNLOAD_INVOICE,uuid);
		OLD_TediResponse tediResponse = get(url, getToken());
		if (tediResponse.ok()) {
			JSONObject json = tediResponse.getJSONObject();
			String attachUrl = json.optString("url");
			return attachUrl;
		}
		throw new OLD_TediException(
				"getInvoiceAttach: " + tediResponse.getResponseCode() + " - " + tediResponse.getResponseMessage());
	}

	public TediInvoice parseInvoice(String company, String fileName, InputStream input) throws OLD_TediException {
//		TediResponse tediResponse = postMultipartFile(url, getToken(), fileName, input);
		OLD_TediResponse tediResponse = postFile(PARSE_INVOICE_URL, getToken(), fileName, input);
		if (tediResponse.ok()) {
			
//			JSONArray array = tediResponse.getJSONArray();
//			if (array.length() > 0) {
//				return TediInvoiceJSON.fromJSON(array.getJSONObject(0));
//			}
//			return null;
			return TediInvoiceJSON.fromJSON(tediResponse.getJSONObject());
		}
		throw new OLD_TediException("parseInvoice: " + tediResponse.getResponseCode() + " - " + tediResponse.getResponseMessage() );
	}
	
	// COMPANY
	public LinkedList<TediCompany> getCompanies(String company) throws OLD_TediException{
		String url = MessageFormat.format(COMPANY_BASE, company);

		
		OLD_TediResponse tediResponse = get(url, getToken());
		if (tediResponse.ok()) {
			JSONArray array = tediResponse.getJSONArray();
			return StreamSupport.stream(array.spliterator(), false).map(r -> TediCompanyJSON.fromJSON((JSONObject) r))
					.collect(Collectors.toCollection(LinkedList::new));
		}
		throw new OLD_TediException(
				"getCompanies: " + tediResponse.getResponseCode() + " - " + tediResponse.getResponseMessage());

		
	}

	public TediCompany createCompany(TediCompany company) throws OLD_TediException {
		OLD_TediResponse response = post(COMPANY_BASE, getToken(), TediCompanyJSON.toJSON(company).toString()); 
		if (response.ok()) {
			return company;
		}
		throw new OLD_TediException(
				"createCompany: " + response.getResponseCode() + " - " + response.getResponseMessage());
	}

	
	// REGISTRY
	
	public TediCompany getRegistry(String company) throws OLD_TediException{
		String url = MessageFormat.format(REGISTRY_BASE, company);

		OLD_TediResponse tediResponse = get(url, getToken());
		if (tediResponse.ok()) {
			JSONObject object = tediResponse.getJSONObject();
			return TediCompanyJSON.fromJSON(object);
		}
		throw new OLD_TediException(
				"getRegistry: " + tediResponse.getResponseCode() + " - " + tediResponse.getResponseMessage());
	}
}
