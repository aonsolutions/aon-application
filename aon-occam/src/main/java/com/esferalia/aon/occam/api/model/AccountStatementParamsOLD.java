package com.esferalia.aon.occam.api.model;

import java.util.Date;

import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class AccountStatementParamsOLD implements IAccountParams {

	private static final long serialVersionUID = 8075038329917800745L;
	
	private int domain;
	private Integer period;
	private Integer account;
	private Account fullAccount;
	private Date fromDate;
	private Date toDate;
	private Integer activity;
	private SecurityLevel securityLevel;
	
	private boolean openingEntriesExcluded;
	private boolean operatingEntriesExcluded;
	private boolean closingEntriesExcluded;

	private String documentNumber;

	@Override
	public int getDomain() {
		return domain;
	}
	public AccountStatementParamsOLD setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	@Override
	public Integer getPeriod() {
		return period;
	}
	public AccountStatementParamsOLD setPeriod(Integer period) {
		this.period = period;
		return this;
	}

	public Integer getAccount() {
		return account;
	}
	public AccountStatementParamsOLD setAccount(Integer account) {
		this.account = account;
		return this;
	}
	public Account getFullAccount() {
		return fullAccount;
	}
	public AccountStatementParamsOLD setFullAccount(Account fullAccount) {
		this.fullAccount = fullAccount;
		return this;
	} 
	@Override
	public Date getFromDate() {
		return fromDate;
	}
	public AccountStatementParamsOLD setFromDate(Date fromDate) {
		this.fromDate = fromDate;
		return this;
	}

	@Override
	public Date getToDate() {
		return toDate;
	}
	public AccountStatementParamsOLD setToDate(Date toDate) {
		this.toDate = toDate;
		return this;
	}

	@Override
	public Integer getActivity() {
		return activity;
	}
	public AccountStatementParamsOLD setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}
	
	@Override
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public AccountStatementParamsOLD setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	
	public boolean areOpeningEntriesExcluded() {
		return openingEntriesExcluded;
	}

	public AccountStatementParamsOLD setOpeningEntriesExcluded(boolean openingEntriesExcluded) {
		this.openingEntriesExcluded = openingEntriesExcluded;
		return this;
	}

	public boolean areClosingEntriesExcluded() {
		return closingEntriesExcluded;
	}

	public AccountStatementParamsOLD setClosingEntriesExcluded(boolean closingEntriesExcluded) {
		this.closingEntriesExcluded = closingEntriesExcluded;
		return this;
	}

	public boolean areOperatingEntriesExcluded() {
		return operatingEntriesExcluded;
	}

	public AccountStatementParamsOLD setOperatingEntriesExcluded(boolean operatingEntriesExcluded) {
		this.operatingEntriesExcluded = operatingEntriesExcluded;
		return this;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public AccountStatementParamsOLD setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
		return this;
	}
	
}
