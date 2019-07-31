package es.translogia.tedi;

import org.json.JSONObject;

public class TediCompany {

	public static final String SRC = "/company";
	
	private String name;
	private String document;
	private TediAddress address;
	private String iban;
	private TediPlan plan;
	private String bic;
	
	public TediCompany() {}
	
	public TediCompany(JSONObject json) {
		this.name = json.getString("name");
		this.document = json.getString("document");
		this.address = new TediAddress(json.optJSONObject("address"));
		this.iban = json.getString("iban");
		this.plan = new TediPlan(json.getJSONObject("plan"));
		this.bic = json.getString("bic");
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
	
	public TediPlan getPlan() {
		return plan;
	}

	public TediCompany setPlan(TediPlan plan) {
		this.plan = plan;
		return this;
	}
	
	public String getBic() {
		return bic;
	}

	public TediCompany setBic(String bic) {
		this.bic = bic;
		return this;
	}

	public JSONObject getJSON(){
		return new JSONObject()
				.put("name", getName())
				.put("document", getDocument())
				.put("address", getAddress())
				.put("iban", getIban())
				.put("plan", getPlan())
				.put("bic", getBic());
	}

}
