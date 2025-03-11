package com.esferalia.aon.occam.api.model.accounting;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;

public class AccountingIncome implements Serializable{
	
	private static final long serialVersionUID = -2866173741486957944L;

	private int domain;
	private Integer activity;
	private Date date;
	private Customer customer;
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
	public AccountingIncome setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public Optional<Integer> getActivity() {
		return Optional.ofNullable(activity);
	}
	public AccountingIncome setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}

	public Date getDate() {
		return date;
	}
	public AccountingIncome setDate(Date date) {
		this.date = date;
		return this;
	}
	
	public Optional<Customer> getCustomer() {
		return Optional.ofNullable(customer);
	}
	public AccountingIncome setCustomer(Customer customer) {
		this.customer = customer;
		return this;
	}
	
	public Optional<Account> getExpAccount() {
		return Optional.ofNullable( expAccount );
	}
	public AccountingIncome setExpAccount(Account expAccount) {
		this.expAccount = expAccount;
		return this;	
	}

	public String getConcept() {
		return concept;
	}
	public AccountingIncome setConcept(String concept) {
		this.concept = concept;
		return this;
	}
	
	public String getReferenceCode() {
		return referenceCode;
	}
	public AccountingIncome setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
		return this;
	}

	
	public double getAmount() {
		return amount;
	}
	public AccountingIncome setAmount(double amount) {
		this.amount = amount;
		return this;
	}

	public Optional<RegistryBank> getBank() {
		return Optional.ofNullable(bank);
	}
	public AccountingIncome setBank(RegistryBank bank) {
		this.bank = bank;
		return this;
	}

	public Optional<Account> getCashAccount() {
		return Optional.ofNullable( cashAccount );
	}
	public AccountingIncome setCashAccount(Account cashAccount) {
		this.cashAccount = cashAccount;
		return this;	
	}

	public String getComments() {
		return comments;
	}
	public AccountingIncome setComments(String comments) {
		this.comments = comments;
		return this;
	}
	
	public Optional<Rawdoc> getRawdoc() {
		return Optional.ofNullable(rawdoc);
	}
	public AccountingIncome setRawdoc(Rawdoc rawdoc) {
		this.rawdoc = rawdoc;
		return this;
	}
	
	public Optional<AccountEntry> getAccountEntry() {
		return Optional.ofNullable(accountEntry);
	}
	public AccountingIncome setAccountEntry(AccountEntry accountEntry) {
		this.accountEntry = accountEntry;
		return this;
	}
	
	public Optional<Finance> getFinance() {
		return Optional.ofNullable(finance);
	}
	public AccountingIncome setFinance(Finance finance) {
		this.finance = finance;
		return this;
	}

}
