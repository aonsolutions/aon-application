package com.esferalia.aon.occam.api.model.finance;

import java.util.Date;

import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.occam.api.model.type.Administration;

public class SiiConfiguration {
	
	private boolean active;
	private Administration administration;
	private boolean test;
	private Certificate certificate;
	private boolean autosend;
	private Date includeDate;
	private String registryDate;

	public boolean isActive() {
		return active;
	}
	
	public SiiConfiguration setActive(boolean active) {
		this.active = active;
		return this;
	}
	
	public Administration getAdministration() {
		return administration;
	}
	
	public SiiConfiguration setAdministration(Administration administration) {
		this.administration = administration;
		return this;
	}
	
	public boolean isTest() {
		return test;
	}
	
	public SiiConfiguration setTest(boolean test) {
		this.test = test;
		return this;
	}
	
	public Certificate getCertificate() {
		return certificate;
	}
	
	public SiiConfiguration setCertificate(Certificate certificate) {
		this.certificate = certificate;
		return this;
	}
	
	public boolean isAutosend() {
		return autosend;
	}
	
	public SiiConfiguration setAutosend(boolean autosend) {
		this.autosend = autosend;
		return this;
	}
	
	public Date getIncludeDate() {
		return includeDate;
	}
	
	public SiiConfiguration setIncludeDate(Date includeDate) {
		this.includeDate = includeDate;
		return this;
	}
	
	public String getRegistryDate() {
		return registryDate;
	}
	
	public SiiConfiguration setRegistryDate(String registryDate) {
		this.registryDate = registryDate;
		return this;
	}
	
	public boolean isTaxDate() {
		return "tax".equalsIgnoreCase(getRegistryDate());
	}
	
	public boolean isAuditDate() {
		return "audit".equalsIgnoreCase(getRegistryDate());
	}
	
}
