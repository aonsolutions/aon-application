package es.translogia.tedi.baloo;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.json.JSONArray;
import org.json.JSONObject;

import es.translogia.tedi.ewok.TediInvoice;
import es.translogia.tedi.ewok.TediInvoiceStatus;
import es.translogia.tedi.json.TediInvoiceJSON;

public class Tedi extends TediRequest {
	String TEDI_URL;
	String AUTH;
	String INVOICE_BASE;
	String PUT_INVOICE;
	String DOWNLOAD_INVOICE;
	String GET_INVOICE_BY_UUID;
	String GET_VERIFIED_INVOICES;

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
		DOWNLOAD_INVOICE = INVOICE_BASE + "/d/{0}"; // uuid
		GET_INVOICE_BY_UUID = INVOICE_BASE + "/{0}/{1}";
		GET_VERIFIED_INVOICES = INVOICE_BASE + "?company={0}&status=" + TediInvoiceStatus.verified;
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

	public TediInvoice getInvoice(String company, String uuid) throws TediException {
		String url = MessageFormat.format(GET_INVOICE_BY_UUID,company,uuid);
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
//	public LinkedList<TediCompany> getCompanies() {
//		JSONArray array = getArray(TEDI + COMPANY_SRC, getToken());
//		return StreamSupport.stream(array.spliterator(), false).map(r -> new TediCompany((JSONObject) r))
//			.collect(Collectors.toCollection(LinkedList::new));
//	}
//	
//	public TediCompany getCompany(String document) {
//		JSONObject json = getObject(TEDI + COMPANY_SRC + "/" + document, getToken());
//		return new TediCompany(json);
//	}
//	
//	public TediCompany createCompany(TediCompany company) {
//		TediResponse response = post(TEDI + COMPANY_SRC, getToken(), company.toJSON().toString()); 
//		return new TediCompany(response.getJsonContent());
//	}
//	
//	public TediCompany updateCompany(TediCompany company) {
//		return new TediCompany(put(TEDI + COMPANY_SRC + "/" + company.getDocument(), getToken(), company.toJSON().toString()));
//	}
//	
//	public TediCompany removeCompany(String document) {
//		return new TediCompany(delete(TEDI + COMPANY_SRC + "/" + document , getToken()));
//	}

}
