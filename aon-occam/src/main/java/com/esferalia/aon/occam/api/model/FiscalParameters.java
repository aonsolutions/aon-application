package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.Administration;

public class FiscalParameters implements Serializable {

	private static final long serialVersionUID = -7278132410437800009L;
	
	private Integer company;
	private String document;
	private String name;
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

	public Integer getCompany() {
		return company;
	}

	public void setCompany(Integer company) {
		this.company = company;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getDefaultYear() {
		return defaultYear;
	}

	public void setDefaultYear(Integer defaultYear) {
		this.defaultYear = defaultYear;
	}

	public Integer getAdministration() {
		return administration;
	}
	public Administration getAdministration(Administration ifnullAdminitration) {
		if (getAdministration() == null) return ifnullAdminitration;
		return Administration.values()[getAdministration()];
	}

	public void setAdministration(Integer administration) {
		this.administration = administration;
	}

	public String getAdministrationCode() {
		return administrationCode;
	}

	public void setAdministrationCode(String administrationCode) {
		this.administrationCode = administrationCode;
	}

	public boolean isTaxRefundRegistry() {
		return taxRefundRegistry;
	}

	public void setTaxRefundRegistry(boolean taxRefundRegistry) {
		this.taxRefundRegistry = taxRefundRegistry;
	}

	public Integer getTaxRegime() {
		return taxRegime;
	}

	public void setTaxRegime(Integer taxRegime) {
		this.taxRegime = taxRegime;
	}

	public Integer getAdmonCreditor() {
		return admonCreditor;
	}
	public void setAdmonCreditor(Integer admonCreditor) {
		this.admonCreditor = admonCreditor;
	}
	public Integer getAdmonVatCreditor() {
		return admonVatCreditor;
	}
	public void setAdmonVatCreditor(Integer admonVatCreditor) {
		this.admonVatCreditor = admonVatCreditor;
	}
	public Integer getAdmonRetentionCreditor() {
		return admonRetentionCreditor;
	}
	public void setAdmonRetentionCreditor(Integer admonRetentionCreditor) {
		this.admonRetentionCreditor = admonRetentionCreditor;
	}
	public boolean isPermAddressChanges() {
		return permAddressChanges;
	}

	public void setPermAddressChanges(boolean permAddressChanges) {
		this.permAddressChanges = permAddressChanges;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getContactCellular() {
		return contactCellular;
	}

	public void setContactCellular(String contactCellular) {
		this.contactCellular = contactCellular;
	}

	public String getContactMail() {
		return contactMail;
	}

	public void setContactMail(String contactMail) {
		this.contactMail = contactMail;
	}

	public boolean isMod303ByDifferenceDisabled() {
		return mod303ByDifferenceDisabled;
	}

	public void setMod303ByDifferenceDisabled(boolean mod303ByDifferenceDisabled) {
		this.mod303ByDifferenceDisabled = mod303ByDifferenceDisabled;
	}

	public boolean isCustomerCheckEnabled() {
		return customerCheckEnabled;
	}

	public void setCustomerCheckEnabled(boolean customerCheckEnabled) {
		this.customerCheckEnabled = customerCheckEnabled;
	}
	

}
