package com.esferalia.aon.occam.api.model;

public class DomainGserviceaccount {
	
	String clientId;
	byte[] clientSecret;
	Domain domain;
	String emailAddress;
	Double limit;
	byte[] privateKey;
	String publicKey;
	Double size;
	String googleAccount;
	
	public DomainGserviceaccount() {
	
	}
	
	public String getClientId() {
		return clientId;
	}

	public DomainGserviceaccount setClientId(String clientId) {
		this.clientId = clientId;
		return this;
	}

	public byte[] getClientSecret() {
		return clientSecret;
	}

	public DomainGserviceaccount setClientSecret(byte[] clientSecret) {
		this.clientSecret = clientSecret;
		return this;
	}

	public Domain getDomain() {
		return domain;
	}

	public DomainGserviceaccount setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}
	
	public String getDomainName(){
		return domain.getName();
	}
	
	public Integer getDomainId(){
		return domain.getId();
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public DomainGserviceaccount setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
		return this;
	}

	public Double getLimit() {
		return limit;
	}

	public DomainGserviceaccount setLimit(Double limit) {
		this.limit = limit;
		return this;
	}

	public byte[] getPrivateKey() {
		return privateKey;
	}

	public DomainGserviceaccount setPrivateKey(byte[] privateKey) {
		this.privateKey = privateKey;
		return this;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public DomainGserviceaccount setPublicKey(String publicKey) {
		this.publicKey = publicKey;
		return this;
	}

	public Double getSize() {
		return size;
	}

	public DomainGserviceaccount setSize(Double size) {
		this.size = size;
		return this;
	}

	public String getGoogleAccount() {
		return googleAccount;
	}

	public DomainGserviceaccount setGoogleAccount(String googleAccount) {
		this.googleAccount = googleAccount;
		return this;
	}
	
}
