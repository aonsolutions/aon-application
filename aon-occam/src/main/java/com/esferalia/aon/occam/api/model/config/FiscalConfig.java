package com.esferalia.aon.occam.api.model.config;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.Administration;

public class FiscalConfig implements Serializable {

	private static final long serialVersionUID = -2112795186729671522L;
	
	private Integer defaultYear;
	private Integer administration;
	private String administrationCode;
	private boolean taxRefundRegistry;
	private Integer taxRegime;
	private Integer admonCreditor;
	private Integer admonVatCreditor;
	private Integer admonRetentionCreditor;
	private boolean permAddressChanges;
	private String contactPerson;
	private String contactPhone;
	private String contactCellular;
	private String contactMail;
	private boolean mod303ByDifferenceDisabled;

	private boolean customerCheckEnabled;

	private String certificateDocument;
	private String certificateName;
	private boolean testEnvironment;

	public Integer getDefaultYear() {
		return defaultYear;
	}
	public FiscalConfig setDefaultYear(Integer defaultYear) {
		this.defaultYear = defaultYear;
		return this;
	}
	
	public Integer getAdministration() {
		return administration;
	}
	public Administration getAdministration(Administration ifnullAdminitration) {
		if (getAdministration() == null) return ifnullAdminitration;
		return Administration.values()[getAdministration()];
	}
	public FiscalConfig setAdministration(Integer administration) {
		this.administration = administration;
		return this;
	}
	
	public String getAdministrationCode() {
		return administrationCode;
	}
	public FiscalConfig setAdministrationCode(String administrationCode) {
		this.administrationCode = administrationCode;
		return this;
	}
	
	public boolean isTaxRefundRegistry() {
		return taxRefundRegistry;
	}
	public FiscalConfig setTaxRefundRegistry(boolean taxRefundRegistry) {
		this.taxRefundRegistry = taxRefundRegistry;
		return this;
	}
	
	public Integer getTaxRegime() {
		return taxRegime;
	}
	public FiscalConfig setTaxRegime(Integer taxRegime) {
		this.taxRegime = taxRegime;
		return this;
	}
	
	public Integer getAdmonCreditor() {
		return admonCreditor;
	}
	public FiscalConfig setAdmonCreditor(Integer admonCreditor) {
		this.admonCreditor = admonCreditor;
		return this;
	}
	
	public Integer getAdmonVatCreditor() {
		return admonVatCreditor;
	}
	public FiscalConfig setAdmonVatCreditor(Integer admonVatCreditor) {
		this.admonVatCreditor = admonVatCreditor;
		return this;
	}
	
	public Integer getAdmonRetentionCreditor() {
		return admonRetentionCreditor;
	}
	public FiscalConfig setAdmonRetentionCreditor(Integer admonRetentionCreditor) {
		this.admonRetentionCreditor = admonRetentionCreditor;
		return this;
	}
	
	public boolean isPermAddressChanges() {
		return permAddressChanges;
	}
	public FiscalConfig setPermAddressChanges(boolean permAddressChanges) {
		this.permAddressChanges = permAddressChanges;
		return this;
	}
	
	public String getContactPerson() {
		return contactPerson;
	}
	public FiscalConfig setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
		return this;
	}
	
	public String getContactPhone() {
		return contactPhone;
	}
	public FiscalConfig setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
		return this;
	}
	
	public String getContactCellular() {
		return contactCellular;
	}
	public FiscalConfig setContactCellular(String contactCellular) {
		this.contactCellular = contactCellular;
		return this;
	}
	
	public String getContactMail() {
		return contactMail;
	}
	public FiscalConfig setContactMail(String contactMail) {
		this.contactMail = contactMail;
		return this;
	}
	
	public boolean isMod303ByDifferenceDisabled() {
		return mod303ByDifferenceDisabled;
	}
	public FiscalConfig setMod303ByDifferenceDisabled(boolean mod303ByDifferenceDisabled) {
		this.mod303ByDifferenceDisabled = mod303ByDifferenceDisabled;
		return this;
	}
	
	public boolean isCustomerCheckEnabled() {
		return customerCheckEnabled;
	}
	public FiscalConfig setCustomerCheckEnabled(boolean customerCheckEnabled) {
		this.customerCheckEnabled = customerCheckEnabled;
		return this;
	}
	
	public String getCertificateDocument() {
		return certificateDocument;
	}
	public FiscalConfig setCertificateDocument(String certificateDocument) {
		this.certificateDocument = certificateDocument;
		return this;
	}
	
	public String getCertificateName() {
		return certificateName;
	}
	public FiscalConfig setCertificateName(String certificateName) {
		this.certificateName = certificateName;
		return this;
	}
	
	public boolean isTestEnvironment() {
		return testEnvironment;
	}
	public FiscalConfig setTestEnvironment(boolean testEnvironment) {
		this.testEnvironment = testEnvironment;
		return this;
	}

}
