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

	public static final String AUTH = TEDI_URL + "/auth";
	public static final String INVOICE_BASE = TEDI_URL + "/invoice";
	public static final String PUT_INVOICE = INVOICE_BASE;
	public static final String GET_INVOICE_BY_UUID = INVOICE_BASE + "/{0}/{1}";
	public static final String GET_VERIFIED_INVOICES = INVOICE_BASE + "?company={0}&status=" + TediInvoiceStatus.verified;

	private String token;

	public static Tedi login(String token) {
		// return Tedi.login("aibanez@aonsolutions.es", "test");
		//return Tedi.login("jgarcia@aonsolutions.es", "test");
		return new Tedi(token);
	}

	public static Tedi login(String email, String password) {
		Tedi tedi = new Tedi();
		String requestData = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
		TediResponse response = tedi.post(AUTH, "", requestData);
		if (response.ok()) {
			tedi.setToken(response.getJSONObject().getString("session_id"));
		}
		return tedi;
	}

	private Tedi() {
	}

	private Tedi(String token) {
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
