package es.translogia.tedi;

import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.json.JSONArray;
import org.json.JSONObject;

public class TediUtils extends TediRequest{
	public static final String AUTH = "https://api.tedi.center/auth";

	private String token; 
	
	public static TediUtils getInstance(String email, String password) {
		TediUtils utils = new TediUtils();
		String requestData = "{\"email\":\""+ email + "\",\"password\":\"" + password + "\"}";
		utils.setToken(utils.postObject(AUTH, "", requestData).getString("session_id"));
		return utils;
	}
	
	public static TediUtils getInstance(String token) {
		return new TediUtils(token);
	}
	
	public TediUtils() {}
	
	public TediUtils(String token) {
		this.token = token;
	}
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	// INVOICE

	public LinkedList<TediInvoice> getInvoices(String company){
		JSONArray array = getArray(TediInvoice.SRC + "/" + company, getToken());
		return StreamSupport.stream(array.spliterator(), false).map(r -> new TediInvoice((JSONObject) r))
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public TediInvoice getInvoice(String company, Integer number) {
		JSONObject json = getObject(TediUser.SRC + "/" + company + "/" + number, getToken());
		return new TediInvoice(json);
	}
	
	public TediInvoice createInvoice(TediInvoice invoice) {
		return new TediInvoice(postObject(TediInvoice.SRC, getToken(), invoice.getJSON().toString()));
	}
	
	public TediInvoice updateInvoice(TediInvoice invoice) {
		return createInvoice(invoice);
	}
	
	public TediInvoice deleteInvoice(String company, Integer number) {
		return new TediInvoice(delete(TediInvoice.SRC + "/" + company + "/" + number , getToken()));
	}
	
	// USER
	
	public LinkedList<TediUser> getUsers() {
		JSONArray array = getArray(TediUser.SRC, getToken());
		return StreamSupport.stream(array.spliterator(), false).map(r -> new TediUser((JSONObject) r))
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public TediUser getUser(String email) {
		JSONObject json = getObject(TediUser.SRC, getToken());
		return new TediUser(json);
	}

	public TediUser createUser(TediUser user) {
		return new TediUser(postObject(TediUser.SRC, getToken(), user.getJSON().toString()));
	}
	
	public TediUser updateUser(TediUser user) {
		return new TediUser(put(TediUser.SRC + "/" + user.getEmail(), getToken(), user.getJSON().toString()));
	}
	
	public TediUser removeUser(String email) {
		return new TediUser(delete(TediUser.SRC + "/" + email , getToken()));
	}
	
	// COMPANY
	
	public LinkedList<TediCompany> getCompanies() {
		JSONArray array = getArray(TediCompany.SRC, getToken());
		return StreamSupport.stream(array.spliterator(), false).map(r -> new TediCompany((JSONObject) r))
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public TediCompany getCompany(String document) {
		JSONObject json = getObject(TediCompany.SRC, getToken());
		return new TediCompany(json);
	}
	
	public TediCompany createCompany(TediCompany company) {
		return new TediCompany(postObject(TediCompany.SRC, getToken(), company.getJSON().toString()));
	}
	
	public TediCompany updateCompany(TediCompany company) {
		return new TediCompany(put(TediCompany.SRC + "/" + company.getDocument(), getToken(), company.getJSON().toString()));
	}
	
	public TediCompany removeCompany(String document) {
		return new TediCompany(delete(TediCompany.SRC + "/" + document , getToken()));
	}
	
	

	public static void main(String[] args) {
		String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqZ2FyY2lhQGFvbnNvbHV0aW9ucy5lcyIsImlhdCI6MTU0NzgwMzMzNCwiZXhwIjoxNTc4OTA3MzM0fQ.qwfTr-Fitu1wlQGj1FjXtXX79mqqkW0ubUzCmT1r3p4";
	
		TediUtils t = new TediUtils();
	
		t.setToken(token);
		LinkedList<TediCompany> companies = t.getCompanies();
		for (TediCompany tediCompany : companies) {
			System.out.println(" -----");
			
			System.out.println(" Name -> " + tediCompany.getName());
			System.out.println(" Document -> " + tediCompany.getDocument());
			System.out.println(" Address -> " + tediCompany.getAddress());
			
			System.out.println(" -----");

		}
	}
}
