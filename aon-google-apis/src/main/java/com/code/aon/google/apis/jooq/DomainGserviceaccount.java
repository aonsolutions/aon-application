package com.code.aon.google.apis.jooq;

import org.jooq.Record10;

public class DomainGserviceaccount {
	
	String clientId;
	byte[] clientSecret;
	String domain;
	Integer domainId;
	String emailAddress;
	Double limit;
	byte[] privateKey;
	String publicKey;
	Double size;
	String googleAccount;
	
	public DomainGserviceaccount() {
	
	}

	public DomainGserviceaccount(Record10<String, byte[], String, Integer, String, Double, byte[], String, Double, String> r){
		if(r.value1()!=null) setClientId(r.value1());
		if(r.value2()!=null) setClientSecret(r.value2());
		if(r.value3()!=null) setDomain(r.value3());
		if(r.value4()!=null) setDomainId(r.value4());
		if(r.value5()!=null) setEmailAddress(r.value5());
		if(r.value6()!=null) setLimit(r.value6());
		if(r.value7()!=null) setPrivateKey(r.value7());
		if(r.value8()!=null) setPublicKey(r.value8());
		if(r.value9()!=null) setSize(r.value9());
		if(r.value10()!=null) setGoogleAccount(r.value10());
	}
	
	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public byte[] getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(byte[] clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Integer getDomainId() {
		return domainId;
	}

	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public Double getLimit() {
		return limit;
	}

	public void setLimit(Double limit) {
		this.limit = limit;
	}

	public byte[] getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(byte[] privateKey) {
		this.privateKey = privateKey;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public Double getSize() {
		return size;
	}

	public void setSize(Double size) {
		this.size = size;
	}

	public String getGoogleAccount() {
		return googleAccount;
	}

	public void setGoogleAccount(String googleAccount) {
		this.googleAccount = googleAccount;
	}
	

	
}
