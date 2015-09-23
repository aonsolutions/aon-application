package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class AccountEntry implements Serializable {

	private static final long serialVersionUID = 369125336534396707L;

	private Integer id;
	private Integer accountPeriod;
	private Integer domain;
	private Date entryDate;
	private AccountEntryType entryType;
	private Integer journal;
	private SecurityLevel securityLevel;
	private String comments;
	private LinkedList<AccountEntryDetail> details;
	
	public Integer getId() {
		return this.id;
	}
	public AccountEntry setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getAccountPeriod() {
		return this.accountPeriod;
	}
	public AccountEntry setAccountPeriod(Integer accountPeriod) {
		this.accountPeriod = accountPeriod;
		return this;
	}

	public Integer getDomain() {
		return this.domain;
	}
	public AccountEntry setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Date getEntryDate() {
		return this.entryDate;
	}
	public AccountEntry setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
		return this;
	}

	public AccountEntryType getEntryType() {
		return this.entryType;
	}
	public AccountEntry setEntryType(AccountEntryType entryType) {
		this.entryType = entryType;
		return this;
	}

	public Integer getJournal() {
		return this.journal;
	}
	public AccountEntry setJournal(Integer journal) {
		this.journal = journal;
		return this;
	}

	public SecurityLevel getSecurityLevel() {
		return this.securityLevel;
	}
	public AccountEntry setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}

	public String getComments() {
		return this.comments;
	}
	public AccountEntry setComments(String comments) {
		this.comments = comments;
		return this;
	}

	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	public AccountEntry setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
		return this;
	}
	
	public Collection<AccountEntryDetail> getDetails() {
		ensureNotNullCollection();
		return details;
	}
	public AccountEntry setDetails(LinkedList<AccountEntryDetail> details) {
		this.details = details;
		return this;
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
	
	public void print() {
		System.out.println("id:{"+id+"}"+
			";accountPeriod:{"+ accountPeriod+"}"+
			";domain:{"+ domain+"}"+
			";entryDate:{"+ entryDate+"}"+
			";entryType:{"+ entryType+"}"+
			";journal:{"+ journal+"}"+
			";securityLevel:{"+ securityLevel+"}"+
			";comments:{"+comments+"}");
		for ( AccountEntryDetail det : getDetails()) {
			det.print();
		}
	}
}
