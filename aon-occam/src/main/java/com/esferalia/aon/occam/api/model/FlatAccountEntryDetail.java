package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class FlatAccountEntryDetail implements Serializable{
	
	private static final long serialVersionUID = -3543906096998396626L;
	
	private Integer entryId;
	private Integer entryDomain;
	private Integer entryPeriod;
	private String entryPeriodName;
	private Date entryDate;
	private AccountEntryType entryType;
	private Integer activity;
	private String activityName;
	private Integer journal;
	private SecurityLevel entrySecurityLevel;
	private String comments;
	private String entryCreationUser;
	private Date entryCreationDate;
	private String entryModificationUser;
	private Date entryModificationDate;
	private Integer detailId;
	private Integer account;
	private String accountCode;
	private String accountDescription;
	private String concept;
	private double debit;
	private double credit;
	private Integer balancingAccount;
	private String balancingAccountCode;
	private String balancingAccountDescription;
	private String documentNumber;
	
	public Integer getEntryId() {
		return entryId;
	}
	public FlatAccountEntryDetail setEntryId(Integer entryId) {
		this.entryId = entryId;
		return this;
	}
	public Integer getEntryDomain() {
		return entryDomain;
	}
	public FlatAccountEntryDetail setEntryDomain(Integer entryDomain) {
		this.entryDomain = entryDomain;
		return this;
	}
	public Integer getEntryPperiod() {
		return entryPeriod;
	}
	public FlatAccountEntryDetail setEntryPperiod(Integer entryPperiod) {
		this.entryPeriod = entryPperiod;
		return this;
	}
	public String getEntryPeriodName() {
		return entryPeriodName;
	}
	public FlatAccountEntryDetail setEntryPeriodName(String entryPeriodName) {
		this.entryPeriodName = entryPeriodName;
		return this;
	}
	public Date getEntryDate() {
		return entryDate;
	}
	public FlatAccountEntryDetail setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
		return this;
	}
	public AccountEntryType getEntryType() {
		return entryType;
	}
	public FlatAccountEntryDetail setEntryType(AccountEntryType entryType) {
		this.entryType = entryType;
		return this;
	}
	public Integer getActivity() {
		return activity;
	}
	public FlatAccountEntryDetail setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}
	public String getActivityName() {
		return activityName;
	}
	public FlatAccountEntryDetail setActivityName(String activityName) {
		this.activityName = activityName;
		return this;
	}
	public Integer getJournal() {
		return journal;
	}
	public FlatAccountEntryDetail setJournal(Integer journal) {
		this.journal = journal;
		return this;
	}
	public SecurityLevel getEntrySecurityLevel() {
		return entrySecurityLevel;
	}
	public FlatAccountEntryDetail setEntrySecurityLevel(SecurityLevel entrySecurityLevel) {
		this.entrySecurityLevel = entrySecurityLevel;
		return this;
	}
	public String getComments() {
		return comments;
	}
	public FlatAccountEntryDetail setComments(String comments) {
		this.comments = comments;
		return this;
	}
	public String getEntryCreationUser() {
		return entryCreationUser;
	}
	public FlatAccountEntryDetail setEntryCreationUser(String entryCreationUser) {
		this.entryCreationUser = entryCreationUser;
		return this;
	}
	public Date getEntryCreationDate() {
		return entryCreationDate;
	}
	public FlatAccountEntryDetail setEntryCreationDate(Date entryCreationDate) {
		this.entryCreationDate = entryCreationDate;
		return this;
	}
	public String getEntryModificationUser() {
		return entryModificationUser;
	}
	public FlatAccountEntryDetail setEntryModificationUser(String entryModificationUser) {
		this.entryModificationUser = entryModificationUser;
		return this;
	}
	public Date getEntryModificationDate() {
		return entryModificationDate;
	}
	public FlatAccountEntryDetail setEntryModificationDate(Date entryModificationDate) {
		this.entryModificationDate = entryModificationDate;
		return this;
	}
	public Integer getDetailId() {
		return detailId;
	}
	public FlatAccountEntryDetail setDetailId(Integer detailId) {
		this.detailId = detailId;
		return this;
	}
	public Integer getAccount() {
		return account;
	}
	public FlatAccountEntryDetail setAccount(Integer account) {
		this.account = account;
		return this;
	}
	public String getAccountCode() {
		return accountCode;
	}
	public FlatAccountEntryDetail setAccountCode(String accountCode) {
		this.accountCode = accountCode;
		return this;
	}
	public String getAccountDescription() {
		return accountDescription;
	}
	public FlatAccountEntryDetail setAccountDescription(String accountDescription) {
		this.accountDescription = accountDescription;
		return this;
	}
	public String getConcept() {
		return concept;
	}
	public FlatAccountEntryDetail setConcept(String concept) {
		this.concept = concept;
		return this;
	}
	public double getDebit() {
		return debit;
	}
	public FlatAccountEntryDetail setDebit(double debit) {
		this.debit = debit;
		return this;
	}
	public double getCredit() {
		return credit;
	}
	public FlatAccountEntryDetail setCredit(double credit) {
		this.credit = credit;
		return this;
	}
	public Integer getBalancingAccount() {
		return balancingAccount;
	}
	public FlatAccountEntryDetail setBalancingAccount(Integer balancingAccount) {
		this.balancingAccount = balancingAccount;
		return this;
	}
	public String getBalancingAccountCode() {
		return balancingAccountCode;
	}
	public FlatAccountEntryDetail setBalancingAccountCode(String balancingAccountCode) {
		this.balancingAccountCode = balancingAccountCode;
		return this;
	}
	public String getBalancingAccountDescription() {
		return balancingAccountDescription;
	}
	public FlatAccountEntryDetail setBalancingAccountDescription(String balancingAccountDescription) {
		this.balancingAccountDescription = balancingAccountDescription;
		return this;
	}
	public String getDocumentNumber() {
		return documentNumber;
	}
	public FlatAccountEntryDetail setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
		return this;
	}

	
}
