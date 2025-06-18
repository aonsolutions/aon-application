package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.Certificate;

public class VerifactuConfiguration implements Serializable{

	private static final long serialVersionUID = 1L;

	private boolean active;
	private boolean test;
	private Integer defaultCertificate;
	private Certificate certificate;
	private Date includeDate;
	private String registryDate;

	public boolean isActive() {
		return active;
	}
	
	public VerifactuConfiguration setActive(boolean active) {
		this.active = active;
		return this;
	}
	
	public boolean isTest() {
		return test;
	}
	
	public VerifactuConfiguration setTest(boolean test) {
		this.test = test;
		return this;
	}
	
	public Integer getDefaultCertificate() {
		return defaultCertificate;
	}
	
	public VerifactuConfiguration setDefaultCertificate(Integer defaultCertificate) {
		this.defaultCertificate = defaultCertificate;
		return this;
	}
	
	public Certificate getCertificate() {
		return certificate;
	}
	
	public VerifactuConfiguration setCertificate(Certificate certificate) {
		this.certificate = certificate;
		return this;
	}
	
	public Date getIncludeDate() {
		return includeDate;
	}
	
	public VerifactuConfiguration setIncludeDate(Date includeDate) {
		this.includeDate = includeDate;
		return this;
	}
	
	public String getRegistryDate() {
		return registryDate;
	}
	
	public VerifactuConfiguration setRegistryDate(String registryDate) {
		this.registryDate = registryDate;
		return this;
	}
	
	public boolean isRegistryTaxDate() {
		return "tax".equalsIgnoreCase(getRegistryDate());
	}
	
}
