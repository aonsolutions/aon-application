package es.translogia.tedi;

import org.json.JSONObject;

public class TediRegistry {
	public TediRegistry() {}
	
	public TediRegistry(JSONObject json) {
		if(json != null) {
			this.name = json.optString("name");
			this.document = json.optString("document");
			this.address = new TediAddress(json.optJSONObject("address"));
		}
	}

	private String name;
	private String document;
	private TediAddress address;
	
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
	public TediAddress getAddress() {
		return address;
	}
	public void setAddress(TediAddress address) {
		this.address = address;
	}
}
