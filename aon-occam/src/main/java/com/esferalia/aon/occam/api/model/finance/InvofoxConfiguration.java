package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

public class InvofoxConfiguration implements Serializable{
	
	private static final long serialVersionUID = 1L;
		
	private boolean personalized;
	private String user;
	private String pass;
	
	private String apiUrl;
	private String apiKey;
	private String environment;

	private boolean autoAccept;
	private boolean autoRecord;
	
	public boolean isPersonalized() {
		return personalized;
	}
	
	public InvofoxConfiguration setPersonalized(boolean personalized) {
		this.personalized = personalized;
		return this;
	}
	
	public String getUser() {
		return user;
	}
	
	public InvofoxConfiguration setUser(String user) {
		this.user = user;
		return this;
	}

	public String getPass() {
		return pass;
	}
	
	public InvofoxConfiguration setPass(String pass) {
		this.pass = pass;
		return this;
	}
	
	public String getApiUrl() {
	    return apiUrl;
	}
	
	public InvofoxConfiguration setApiUrl(String apiUrl) {
	    this.apiUrl = apiUrl;
	    return this;
	}

	public String getApiKey() {
	    return apiKey;
	}
	
	public InvofoxConfiguration setApiKey(String apiKey) {
	    this.apiKey = apiKey;
	    return this;
	}
	
	public String getEnvironment() {
		return environment;
	}
	
	public InvofoxConfiguration setEnvironment(String environment) {
		this.environment = environment;
		return this;
	}
	
	public boolean isAutoAccept() {
		return autoAccept;
	}
	
	public InvofoxConfiguration setAutoAccept(boolean autoAccept) {
		this.autoAccept = autoAccept;
		return this;
	}
	
	public boolean isAutoRecord() {
		return autoRecord;
	}
	
	public InvofoxConfiguration setAutoRecord(boolean autoRecord) {
		this.autoRecord = autoRecord;
		return this;
	}
	
	public boolean isLoginRequired() {
		return isPersonalized();
	}
}
