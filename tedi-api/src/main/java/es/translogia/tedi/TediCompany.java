package es.translogia.tedi;

import org.json.JSONObject;

public class TediCompany {

	public static final String SRC = "https://europe-west1-tedicenter.cloudfunctions.net/company";
	public static final String SRC_SNAPSHOT = "https://europe-west1-tedi-snapshot.cloudfunctions.net/company";
	
	private String name;
	private String document;
	private TediAddress address;
	private String iban;
	
	public TediCompany() {}
	
	public TediCompany(JSONObject json) {
		this.name = json.getString("name");
		this.document = json.getString("document");
		this.address = new TediAddress(json.optJSONObject("address"));
		this.iban = json.getString("iban");
	}
	
	public String getName() {
		return name;
	}
	public TediCompany setName(String name) {
		this.name = name;
		return this;
	}
	public String getDocument() {
		return document;
	}
	public TediCompany setDocument(String document) {
		this.document = document;
		return this;
	}
	public TediAddress getAddress() {
		return address;
	}
	public TediCompany setAddress(TediAddress address) {
		this.address = address;
		return this;
	}
	
	public String getIban() {
		return iban;
	}

	public TediCompany setIban(String iban) {
		this.iban = iban;
		return this;
	}

	public JSONObject getJSON(){
		return new JSONObject()
				.put("name", getName())
				.put("document", getDocument())
				.put("address", getAddress())
				.put("iban", getIban());
	}

}
