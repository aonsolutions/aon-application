package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public class InvofoxConfiguration implements Serializable{
	
	private static final long serialVersionUID = 1L;

//  PRO("$2b$10$ZyMOXKSmPwl4VUFk76wFWuK9aCDsXRiaxytOwpqk3gK.epVl6Mfwi", "64804a43d883e2000ac0423a"),
//  DEMO("$2b$10$ntU8dI5/uFHV6sDjd1q9UO1JwZWBPVWKPDP50IVy5m9EMr71s7PCy", "65491eee49f881000dd14c72"),
//	TEST1("$2b$10$hEajgHFNkp/aIOQRmyGmvOM.B2j31EkvudbsqW4IKK/Evul0Kn6RO", "663a3c9c77a8e69b58d69b84"),
//	TEST2("$2b$10$GxLhQn8MtOceuLv1wsPlcOhs/nzLuX2WqzjxdsO5NoQOg3WOFYgfa", "6641fb719668517cfd939cb3"),
//	TEST3("$2b$10$6P84Ynu7HiZHuAcDPrcZF.0o4Lv2eMK0j6NxbksD9RKS.nxynpnZK", "6641fc31fc862711a4b5c80e"),
//	TEST4("$2b$10$JaQnO9lMslqdADJFr2T6o.LX7i0FllAzg6RnR.zSpA9z5/n08Qs8e", "6641fc74e44a57ce1d83a366"),
//	TEST5("$2b$10$B2lr9JkZTWz.hA10XIau/OvxnEsNKDGIHE0P5qdFbHdBj4oE0beHe", "6641fcd385c047bd8b6df0da");
	
	private static final String DEMO_API_KEY = "$2b$10$ntU8dI5/uFHV6sDjd1q9UO1JwZWBPVWKPDP50IVy5m9EMr71s7PCy";
	private static final String DEMO_ENVIRONMENT = "65491eee49f881000dd14c72";
	
	private static final String DEFAULT_USER = "app@aonsolutions.es";	
	private static final String DEFAULT_PASS = "40N@invofox";	
	public static final String DEFAULT_API_URL = "https://api.invofox.com";
	private static final String DEFAULT_API_KEY = "$2b$10$ZyMOXKSmPwl4VUFk76wFWuK9aCDsXRiaxytOwpqk3gK.epVl6Mfwi";
	private static final String DEFAULT_ENVIRONMENT = "64804a43d883e2000ac0423a";

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
		return AonStringUtils.defaultIfEmpty(user, DEFAULT_USER);
	}
	
	public InvofoxConfiguration setUser(String user) {
		this.user = AonStringUtils.defaultIfEmpty(user, DEFAULT_USER);
		return this;
	}

	public String getPass() {
		return AonStringUtils.defaultIfEmpty(pass, DEFAULT_PASS);
	}
	
	public InvofoxConfiguration setPass(String pass) {
		this.pass = AonStringUtils.defaultIfEmpty(pass, DEFAULT_PASS);
		return this;
	}
	
	public String getApiUrl() {
	    return AonStringUtils.defaultIfEmpty(apiUrl, DEFAULT_API_URL);
	}
	
	public InvofoxConfiguration setApiUrl(String apiUrl) {
	    this.apiUrl = AonStringUtils.defaultIfEmpty(apiUrl, DEFAULT_API_URL);
	    return this;
	}

	public String getApiKey() {
	    return AonStringUtils.defaultIfEmpty(apiKey, DEFAULT_API_KEY);
	}
	
	public InvofoxConfiguration setApiKey(String apiKey) {
	    this.apiKey = AonStringUtils.defaultIfEmpty(apiKey, DEFAULT_API_KEY);
	    return this;
	}
	
	public String getEnvironment() {
		return AonStringUtils.defaultIfEmpty(environment, DEFAULT_ENVIRONMENT);
	}
	
	public InvofoxConfiguration setEnvironment(String environment) {
		this.environment = AonStringUtils.defaultIfEmpty(environment, DEFAULT_ENVIRONMENT);
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
		return isPersonalized() && getUser().equalsIgnoreCase(DEFAULT_USER);
	}
	
	@Deprecated
	public InvofoxConfiguration setTest(boolean test) {
		if(test) {
			setApiKey(DEMO_API_KEY);
			setEnvironment(DEMO_ENVIRONMENT);
		}
		return this;
	}
}
