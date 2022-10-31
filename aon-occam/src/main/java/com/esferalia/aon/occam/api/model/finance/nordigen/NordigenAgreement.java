package com.esferalia.aon.occam.api.model.finance.nordigen;

import java.io.Serializable;
import java.util.Date;

public class NordigenAgreement implements Serializable {

	private static final long serialVersionUID = -6159572986894568331L;

	private String id;
	private Date created;
	private Integer maxHistoricalDays;
	private Integer accessValidForDays;
	private NORDIGEN_ACCESS_SCOPES[] accessScope;
	private Date accepted;
	private String institutionId;
	
	public String getId() {
		return id;
	}
	public NordigenAgreement setId(String id) {
		this.id = id;
		return this;
	}
	public Date getCreated() {
		return created;
	}
	public NordigenAgreement setCreated(Date created) {
		this.created = created;
		return this;
	}
	public Integer getMaxHistoricalDays() {
		return maxHistoricalDays;
	}
	public NordigenAgreement setMaxHistoricalDays(Integer maxHistoricalDays) {
		this.maxHistoricalDays = maxHistoricalDays;
		return this;
	}
	public Integer getAccessValidForDays() {
		return accessValidForDays;
	}
	public NordigenAgreement setAccessValidForDays(Integer accessValidForDays) {
		this.accessValidForDays = accessValidForDays;
		return this;
	}
	public NORDIGEN_ACCESS_SCOPES[] getAccessScope() {
		return accessScope;
	}
	public NordigenAgreement setAccessScope(NORDIGEN_ACCESS_SCOPES[] accessScope) {
		this.accessScope = accessScope;
		return this;
	}
	public Date getAccepted() {
		return accepted;
	}
	public NordigenAgreement setAccepted(Date accepted) {
		this.accepted = accepted;
		return this;
	}
	public String getInstitutionId() {
		return institutionId;
	}
	public NordigenAgreement setInstitutionId(String institutionId) {
		this.institutionId = institutionId;
		return this;
	}
	
	
}
