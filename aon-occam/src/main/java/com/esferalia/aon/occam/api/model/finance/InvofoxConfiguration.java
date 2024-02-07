package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public class InvofoxConfiguration implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public enum Environment {
	    PRO("$2b$10$ZyMOXKSmPwl4VUFk76wFWuK9aCDsXRiaxytOwpqk3gK.epVl6Mfwi"),
	    TEST("$2b$10$ntU8dI5/uFHV6sDjd1q9UO1JwZWBPVWKPDP50IVy5m9EMr71s7PCy");
	    
	    private Environment(String apiKey) {
		this("https://api.invofox.com",apiKey);
	    }

	    private Environment(String apiUrl, String apiKey) {
		this.apiUrl = apiUrl;
		this.apiKey = apiKey;
	    }
	    
	    private String apiUrl;
	    private String apiKey;
	    
	    private String getApiUrl(){
		return apiUrl;
	    }
	    
	    private String getApiKey() {
		return apiKey;
	    }
 	}
	
	private String apiUrl ;
	private String apiKey ;
	private Environment environment = Environment.PRO ;
	
	public boolean isTest() {
		return environment == Environment.TEST;
	}
	
	public InvofoxConfiguration setTest(boolean test) {
		this.environment = test ? Environment.TEST : Environment.PRO;
		return this;
	}
	
	public void setApiUrl(String apiUrl) {
	    this.apiUrl = AonStringUtils.defaultIfEmpty(apiUrl, null);
	}
	
	public String getApiUrl() {
	    return AonStringUtils.defaultIfEmpty(apiUrl, environment.getApiUrl());
	}
	
	public void setApiKey(String apiKey) {
	    this.apiKey = AonStringUtils.defaultIfEmpty(apiKey, null);
	}

	public String getApiKey() {
	    return AonStringUtils.defaultIfEmpty(apiKey, environment.getApiKey());
	}
	
	
	
}
