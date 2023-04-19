package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.type.Administration;

public class SiiConfiguration implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Company company;
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
	
	public Company getCompany() {
		return company;
	}
	
	public SiiConfiguration setCompany(Company company) {
		this.company = company;
		return this;
	}
	
	public Administration getAdministration() {
		return administration;
	}
	
	public boolean isAraba() {
		return Administration.ALAVA.equals(getAdministration());
	}
	
	public boolean isBizkaia() {
		return Administration.BIZKAIA.equals(getAdministration());
	}
	
	public boolean isGipuzkoa() {
		return Administration.GIPUZKOA.equals(getAdministration());
	}
	
	public boolean isNafarroa() {
		return Administration.NAVARRA.equals(getAdministration());
	}
	
	public boolean isCommonTerritory() {
		return Administration.COMMON_TERRITORY.equals(getAdministration());
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
