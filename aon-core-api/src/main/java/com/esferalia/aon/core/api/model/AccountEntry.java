package com.esferalia.aon.core.api.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.core.api.model.type.AccountEntryType;
import com.esferalia.aon.core.api.model.type.SecurityLevel;

public class AccountEntry implements Serializable {

	private static final long serialVersionUID = 369125336534396707L;

	public AccountEntry() {
		
	}

	public AccountEntry(Integer id, Integer accountPeriod, Integer domain,
			Date entryDate, Byte entryType, Integer journal,
			Byte securityLevel, String comments) {
		setId(id);
		setAccountPeriod(accountPeriod);
		setDomain(domain);
		setEntryDate(entryDate);
		setEntryType(AccountEntryType.getValue(entryType));
		setJournal(journal);
		setSecurityLevel(SecurityLevel.getValue(securityLevel));
		setComments(comments);
	}	
	
	private Integer id;
	private Integer accountPeriod;
	private Integer domain;
	private Date entryDate;
	private AccountEntryType entryType;
	private Integer journal;
	private SecurityLevel securityLevel;
	private String comments;
	private Collection<AccountEntryDetail> details;

	public Integer getId() {
		return this.id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getAccountPeriod() {
		return this.accountPeriod;
	}
	public void setAccountPeriod(Integer accountPeriod) {
		this.accountPeriod = accountPeriod;
	}

	public Integer getDomain() {
		return this.domain;
	}
	public void setDomain(Integer domain) {
		this.domain = domain;
	}

	public Date getEntryDate() {
		return this.entryDate;
	}
	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	public AccountEntryType getEntryType() {
		return this.entryType;
	}
	public void setEntryType(AccountEntryType entryType) {
		this.entryType = entryType;
	}

	public Integer getJournal() {
		return this.journal;
	}
	public void setJournal(Integer journal) {
		this.journal = journal;
	}

	public SecurityLevel getSecurityLevel() {
		return this.securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	public String getComments() {
		return this.comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}

	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	public void setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
	}
	
	public Collection<AccountEntryDetail> getDetails() {
		ensureNotNullCollection();
		return details;
	}
	public void setDetails(Collection<AccountEntryDetail> details) {
		this.details = details;
	}
	public AccountEntry addDetail( AccountEntryDetail detail) {
		ensureNotNullCollection();
		this.details.add(detail);
		return this;
	}
	
	private void ensureNotNullCollection() {
		if (this.details == null) {
			this.details = new LinkedList<AccountEntryDetail>();
		}
	}
	
}
