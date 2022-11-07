package com.esferalia.aon.occam.api.model.finance;

import java.util.Date;

import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.occam.api.model.type.Administration;

public class TbaiConfiguration {
	
	private boolean active;
	private Administration administration;
	private boolean test;
	private Integer defaultCertificate;
	private Certificate certificate;
	private Date includeDate;
	private String registryDate;

	public boolean isActive() {
		return active;
	}
	
	public TbaiConfiguration setActive(boolean active) {
		this.active = active;
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
	
	public TbaiConfiguration setAdministration(Administration administration) {
		this.administration = administration;
		return this;
	}
	
	public boolean isTest() {
		return test;
	}
	
	public TbaiConfiguration setTest(boolean test) {
		this.test = test;
		return this;
	}
	
	public Integer getDefaultCertificate() {
		return defaultCertificate;
	}
	
	public TbaiConfiguration setDefaultCertificate(Integer defaultCertificate) {
		this.defaultCertificate = defaultCertificate;
		return this;
	}
	
	public Certificate getCertificate() {
		return certificate;
	}
	
	public TbaiConfiguration setCertificate(Certificate certificate) {
		this.certificate = certificate;
		return this;
	}
	
	public Date getIncludeDate() {
		return includeDate;
	}
	
	public TbaiConfiguration setIncludeDate(Date includeDate) {
		this.includeDate = includeDate;
		return this;
	}
	
	public String getRegistryDate() {
		return registryDate;
	}
	
	public TbaiConfiguration setRegistryDate(String registryDate) {
		this.registryDate = registryDate;
		return this;
	}
	
	public boolean isRegistryTaxDate() {
		return "tax".equalsIgnoreCase(getRegistryDate());
	}
	
}
