package com.esferalia.aon.occam.api.model.finance.nordigen;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class NordigenRequisition implements Serializable {

	private static final long serialVersionUID = 5185247966507964713L;
	
	private String id;
	private Date created;
	private String redirect;
	private NORDIGEN_REQUISITION_STATUS status;
	private String institutionId;
	private String agreement;
	private String reference;
	private List<String> accounts;
	private String userLanguage;
	private String link;
	private String ssn;
	private Boolean accountSelection;
	private Boolean redirectImmediate;
	
	public String getId() {
		return id;
	}
	public NordigenRequisition setId(String id) {
		this.id = id;
		return this;
	}
	public Date getCreated() {
		return created;
	}
	public NordigenRequisition setCreated(Date created) {
		this.created = created;
		return this;
	}
	public String getRedirect() {
		return redirect;
	}
	public NordigenRequisition setRedirect(String redirect) {
		this.redirect = redirect;
		return this;
	}
	public NORDIGEN_REQUISITION_STATUS getStatus() {
		return status;
	}
	public NordigenRequisition setStatus(NORDIGEN_REQUISITION_STATUS status) {
		this.status = status;
		return this;
	}
	public String getInstitutionId() {
		return institutionId;
	}
	public NordigenRequisition setInstitutionId(String institutionId) {
		this.institutionId = institutionId;
		return this;
	}
	public String getAgreement() {
		return agreement;
	}
	public NordigenRequisition setAgreement(String agreement) {
		this.agreement = agreement;
		return this;
	}
	public String getReference() {
		return reference;
	}
	public NordigenRequisition setReference(String reference) {
		this.reference = reference;
		return this;
	}
	public List<String> getAccounts() {
		return accounts;
	}
	public NordigenRequisition setAccounts(List<String> accounts) {
		this.accounts = accounts;
		return this;
	}
	public String getUserLanguage() {
		return userLanguage;
	}
	public NordigenRequisition setUserLanguage(String userLanguage) {
		this.userLanguage = userLanguage;
		return this;
	}
	public String getLink() {
		return link;
	}
	public NordigenRequisition setLink(String link) {
		this.link = link;
		return this;
	}
	public String getSsn() {
		return ssn;
	}
	public NordigenRequisition setSsn(String ssn) {
		this.ssn = ssn;
		return this;
	}
	public Boolean getAccountSelection() {
		return accountSelection;
	}
	public NordigenRequisition setAccountSelection(Boolean accountSelection) {
		this.accountSelection = accountSelection;
		return this;
	}
	public Boolean getRedirectImmediate() {
		return redirectImmediate;
	}
	public NordigenRequisition setRedirectImmediate(Boolean redirectImmediate) {
		this.redirectImmediate = redirectImmediate;
		return this;
	}
	
	
}
