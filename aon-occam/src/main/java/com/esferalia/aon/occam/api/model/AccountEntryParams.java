package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

public class AccountEntryParams implements Serializable{

	private static final long serialVersionUID = 7399522390660289406L;
	
	private int domain;
	private Date from;
	private Date to;
	private Integer accountId;
	private String accountCode;
	private Double debit;
	private Double credit;
	private String concept;
	private String document;
	private boolean confidential; 
	private boolean hasConfidentialityRole; 
	
	
	public int getDomain() {
		return domain;
	}
	public AccountEntryParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public Date getFrom() {
		return from;
	}
	public AccountEntryParams setFrom(Date from) {
		this.from = from;
		return this;
	}
	public Date getTo() {
		return to;
	}
	public AccountEntryParams setTo(Date to) {
		this.to = to;
		return this;
	}
	public Integer getAccountId() {
		return accountId;
	}
	public AccountEntryParams setAccountId(Integer accountId) {
		this.accountId = accountId;
		return this;
	}
	public String getAccountCode() {
		return accountCode;
	}
	public AccountEntryParams setAccountCode(String accountCode) {
		this.accountCode = accountCode;
		return this;
	}
	public Double getDebit() {
		return debit;
	}
	public AccountEntryParams setDebit(Double debit) {
		this.debit = debit;
		return this;
	}
	public Double getCredit() {
		return credit;
	}
	public AccountEntryParams setCredit(Double credit) {
		this.credit = credit;
		return this;
	}
	public String getConcept() {
		return concept;
	}
	public AccountEntryParams setConcept(String concept) {
		this.concept = concept;
		return this;
	}
	public String getDocument() {
		return document;
	}
	public AccountEntryParams setDocument(String document) {
		this.document = document;
		return this;
	}
	public boolean isConfidential() {
		return confidential;
	}
	public AccountEntryParams setConfidential(boolean confidential) {
		this.confidential = confidential;
		return this;
	}
	public boolean hasConfidentialityRole() {
		return hasConfidentialityRole;
	}
	public AccountEntryParams setHasConfidentialityRole(boolean hasConfidentialityRole) {
		this.hasConfidentialityRole = hasConfidentialityRole;
		return this;
	}
	
}
