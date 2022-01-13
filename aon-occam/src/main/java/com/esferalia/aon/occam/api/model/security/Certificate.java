package com.esferalia.aon.occam.api.model.security;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public class Certificate implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer domain;
	private String type;
	private String password;
	private String name;
	private byte [] certificate;
	private boolean confidential;

	public Integer getId() {
		
		return id;
	}
	
	public Certificate setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	
	public Certificate setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public String getType() {
		return type;
	}
	
	public Certificate setType(String type) {
		this.type = type;
		return this;
	}

	public String getPassword() {
		return password;
	}
	
	public Certificate setPassword(String password) {
		this.password = password;
		return this;
	}
	
	public boolean hasPassword() {
		return !AonStringUtils.isBlank(getPassword());
	}

	public byte[] getCertificate() {
		return certificate;
	}
	
	public Certificate setCertificate(byte[] certificate) {
		this.certificate = certificate;
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public Certificate setName(String name) {
		this.name = name;
		return this;
	}
	
	public Certificate setDescription(String description) {
		this.name = description.split("HIDE")[0];
		this.password = description.split("HIDE\\(")[1].split("\\)")[0];
		return this;
	}

	public boolean isConfidential() {
		return confidential;
	}
	
	public Certificate setConfidential(boolean confidential) {
		this.confidential = confidential;
		return this;
	}
	
	public boolean isEmpty() {
		return getCertificate() == null && getPassword() == null;
	}
}
