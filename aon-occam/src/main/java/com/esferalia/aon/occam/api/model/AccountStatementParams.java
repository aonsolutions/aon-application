package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class AccountStatementParams implements Serializable {

	private static final long serialVersionUID = 8075038329917800745L;
	
	private int domain;
	private Integer period;
	private Integer account;
	private Date fromDate;
	private Date toDate;
	private Integer activity;
	private SecurityLevel securityLevel;
	
	private boolean openingEntriesExcluded;
	private boolean operatingEntriesExcluded;
	private boolean closingEntriesExcluded;

	private String documentNumber;
	
	public int getDomain() {
		return domain;
	}
	public AccountStatementParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public Integer getPeriod() {
		return period;
	}
	public AccountStatementParams setPeriod(Integer period) {
		this.period = period;
		return this;
	}

	public Integer getAccount() {
		return account;
	}
	public AccountStatementParams setAccount(Integer account) {
		this.account = account;
		return this;
	}

	public Date getFromDate() {
		return fromDate;
	}
	public AccountStatementParams setFromDate(Date fromDate) {
		this.fromDate = fromDate;
		return this;
	}

	public Date getToDate() {
		return toDate;
	}
	public AccountStatementParams setToDate(Date toDate) {
		this.toDate = toDate;
		return this;
	}

	public Integer getActivity() {
		return activity;
	}
	public AccountStatementParams setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}
	
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public AccountStatementParams setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	
	public boolean areOpeningEntriesExcluded() {
		return openingEntriesExcluded;
	}

	public AccountStatementParams setOpeningEntriesExcluded(boolean openingEntriesExcluded) {
		this.openingEntriesExcluded = openingEntriesExcluded;
		return this;
	}

	public boolean areClosingEntriesExcluded() {
		return closingEntriesExcluded;
	}

	public AccountStatementParams setClosingEntriesExcluded(boolean closingEntriesExcluded) {
		this.closingEntriesExcluded = closingEntriesExcluded;
		return this;
	}

	public boolean areOperatingEntriesExcluded() {
		return operatingEntriesExcluded;
	}

	public AccountStatementParams setOperatingEntriesExcluded(boolean operatingEntriesExcluded) {
		this.operatingEntriesExcluded = operatingEntriesExcluded;
		return this;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public AccountStatementParams setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
		return this;
	}
	
}
