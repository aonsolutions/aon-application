package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountEntryParams implements Serializable{

	private static final long serialVersionUID = 7399522390660289406L;
	
	private String domainName; 
	private int domain;
	private String user;
	private Integer period;
	private Date from;
	private Date to;
	private AccountEntryType type;
	private Integer journal;
	private Integer activity;
	private boolean confidential; 
	
	private Integer account;
	private Double debit;
	private Double credit;
	private String concept;
	private String document;
	private Integer balancingAccount;
	
	private String comments;
	private int order;
	
	private AccountPeriod selectedPeriod;
	private EnterpriseActivity selectedActivity;
	private Account selectedAccount;
	
	public String getDomainName() {
		return domainName;
	}
	public AccountEntryParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public AccountEntryParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public String getUser() {
		return user;
	}
	public AccountEntryParams setUser(String user) {
		this.user = user;
		return this;
	}
	public Integer getPeriod() {
		return period;
	}
	public AccountEntryParams setPeriod(Integer period) {
		this.period = period;
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
	public AccountEntryType getType() {
		return type;
	}
	public AccountEntryParams setType(AccountEntryType type) {
		this.type = type;
		return this;
	}
	public Integer getJournal() {
		return journal;
	}
	public AccountEntryParams setJournal(Integer journal) {
		this.journal = journal;
		return this;
	}
	public Integer getActivity() {
		return activity;
	}
	public AccountEntryParams setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}
	public AccountEntryParams setTo(Date to) {
		this.to = to;
		return this;
	}
	public Integer getAccount() {
		return account;
	}
	public AccountEntryParams setAccount(Integer account) {
		this.account = account;
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
	public String getComments() {
		return comments;
	}
	public AccountEntryParams setComments(String comments) {
		this.comments = comments;
		return this;
	}
	public Integer getBalancingAccount() {
		return balancingAccount;
	}
	public AccountEntryParams setBalancingAccount(Integer balancingAccount) {
		this.balancingAccount = balancingAccount;
		return this;
	}
	
	public boolean isConfidential() {
		return confidential;
	}
	public AccountEntryParams setConfidential(boolean confidential) {
		this.confidential = confidential;
		return this;
	}
	
	public int getOrder() {
		return order;
	}
	public AccountEntryParams setOrder(int order) {
		this.order = order;
		return this;
	}
	
	public AccountPeriod getSelectedPeriod() {
		return selectedPeriod;
	}
	public AccountEntryParams setSelectedPeriod(AccountPeriod selectedPeriod) {
		this.selectedPeriod = selectedPeriod;
		return this;
	}
	public EnterpriseActivity getSelectedActivity() {
		return selectedActivity;
	}
	public AccountEntryParams setSelectedActivity(EnterpriseActivity selectedActivity) {
		this.selectedActivity = selectedActivity;
		return this;
	}
	public Account getSelectedAccount() {
		return selectedAccount;
	}
	public AccountEntryParams setSelectedAccount(Account selectedAccount) {
		this.selectedAccount = selectedAccount;
		return this;
	}
	public boolean hasDetailProperties() {
		return (account != null 
			|| (debit != null  && debit != 0.0)
			|| (credit != null && credit != 0.0)
			|| AonStringUtils.isNotEmpty( concept ) 
			|| AonStringUtils.isNotEmpty( document  ) );
	}
	
}
