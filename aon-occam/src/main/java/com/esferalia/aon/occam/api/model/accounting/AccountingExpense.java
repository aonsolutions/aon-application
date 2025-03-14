package com.esferalia.aon.occam.api.model.accounting;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;

public class AccountingExpense implements Serializable{
	
	private static final long serialVersionUID = -1050216489263392630L;
	
	private int domain;
	private Integer activity;
	private Date date;
	private Creditor creditor;
	private Account expAccount;
	private String concept;
	private String referenceCode;
	private double amount;
	private RegistryBank bank;
	private Account cashAccount;
	private String comments;
	
	private Rawdoc rawdoc;
	private AccountEntry accountEntry;
	private Finance finance;
	
	public int getDomain() {
		return domain;
	}
	public AccountingExpense setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	public Optional<Integer> getActivity() {
		return Optional.ofNullable(activity);
	}
	public AccountingExpense setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}

	public Date getDate() {
		return date;
	}
	public AccountingExpense setDate(Date date) {
		this.date = date;
		return this;
	}
	
	public Optional<Creditor> getCreditor() {
		return Optional.ofNullable(creditor);
	}
	public AccountingExpense setCreditor(Creditor creditor) {
		this.creditor = creditor;
		return this;
	}
	
	public Optional<Account> getExpAccount() {
		return Optional.ofNullable( expAccount );
	}
	public AccountingExpense setExpAccount(Account expAccount) {
		this.expAccount = expAccount;
		return this;	
	}

	public String getConcept() {
		return concept;
	}
	public AccountingExpense setConcept(String concept) {
		this.concept = concept;
		return this;
	}
	
	public String getReferenceCode() {
		return referenceCode;
	}
	public AccountingExpense setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
		return this;
	}

	
	public double getAmount() {
		return amount;
	}
	public AccountingExpense setAmount(double amount) {
		this.amount = amount;
		return this;
	}

	public Optional<RegistryBank> getBank() {
		return Optional.ofNullable(bank);
	}
	public AccountingExpense setBank(RegistryBank bank) {
		this.bank = bank;
		return this;
	}

	public Optional<Account> getCashAccount() {
		return Optional.ofNullable( cashAccount );
	}
	public AccountingExpense setCashAccount(Account cashAccount) {
		this.cashAccount = cashAccount;
		return this;	
	}

	public String getComments() {
		return comments;
	}
	public AccountingExpense setComments(String comments) {
		this.comments = comments;
		return this;
	}
	
	public Optional<Rawdoc> getRawdoc() {
		return Optional.ofNullable(rawdoc);
	}
	public AccountingExpense setRawdoc(Rawdoc rawdoc) {
		this.rawdoc = rawdoc;
		return this;
	}
	
	public Optional<AccountEntry> getAccountEntry() {
		return Optional.ofNullable(accountEntry);
	}
	public AccountingExpense setAccountEntry(AccountEntry accountEntry) {
		this.accountEntry = accountEntry;
		return this;
	}
	
	public Optional<Finance> getFinance() {
		return Optional.ofNullable(finance);
	}
	public AccountingExpense setFinance(Finance finance) {
		this.finance = finance;
		return this;
	}
	
}
