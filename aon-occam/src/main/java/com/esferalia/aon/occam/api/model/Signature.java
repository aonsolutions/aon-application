package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Signature implements Serializable {
	
	Integer id;
	Integer domain;
	String name;
	String signature;
	Integer userId;
	
	public Signature() {}

	public Integer getId() {
		return id;
	}

	public Signature setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public Signature setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public String getName() {
		return name;
	}

	public Signature setName(String name) {
		this.name = name;
		return this;
	}

	public String getSignature() {
		return signature;
	}

	public Signature setSignature(String signature) {
		this.signature = signature;
		return this;
	}

	public Integer getUserId() {
		return userId;
	}

	public Signature setUserId(Integer userId) {
		this.userId = userId;
		return this;
	}
}
