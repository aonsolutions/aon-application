package es.translogia.tedi;

import org.json.JSONObject;

public class TediRegistry {
	public TediRegistry() {}
	
	public TediRegistry(JSONObject json) {
		this.name = json.getString("name");
		this.document = json.getString("document");
		this.address = json.getString("address");
	}

	private String name;
	private String document;
	private String address;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
}
