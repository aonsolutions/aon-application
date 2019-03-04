package es.translogia.tedi;

import org.json.JSONObject;

public class TediPGC {
	public TediPGC() {}
	
	public TediPGC(JSONObject json) {
		if(json != null) {
			this.account = json.getString("account");
			this.name = json.getString("name");
			this.description = json.getString("description");
		}
	}
	
	private String account;
	private String name;
	private String description;
	public String getAccount() {
		return account;
	}
	public TediPGC setAccount(String account) {
		this.account = account;
		return this;
	}
	public String getName() {
		return name;
	}
	public TediPGC setName(String name) {
		this.name = name;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public TediPGC setDescription(String description) {
		this.description = description;
		return this;
	}
}
