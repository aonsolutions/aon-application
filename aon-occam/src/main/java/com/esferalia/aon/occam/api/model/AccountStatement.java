package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

public class AccountStatement implements Serializable {

	private static final long serialVersionUID = 8075038329917800745L;
	
	private Integer type;
	private Integer accountEntry;
	private Integer journal;
	private Date entryDate;
	private Integer account;
	private String accountCode;
	private String accountDescription;
	private String concept;
	private double debit;
	private double credit;
	private double debitBalance;
	private double unpaidBalance;
	private Integer balancingAccount;
	private String balancingAccountCode;
	private String balancingAccountDescription;
	private String documentNumber;
	
	
	public Integer getType() {
		return type;
	}
	public AccountStatement setType(Integer type) {
		this.type = type;
		return this;
	}

	public Integer getAccountEntry() {
		return accountEntry;
	}
	public AccountStatement setAccountEntry(Integer accountEntry) {
		this.accountEntry = accountEntry;
		return this;
	}
	public Integer getJournal() {
		return journal;
	}
	public AccountStatement setJournal(Integer journal) {
		this.journal = journal;
		return this;
	}
	public Date getEntryDate() {
		return entryDate;
	}
	public AccountStatement setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
		return this;
	}

	public Integer getAccount() {
		return account;
	}

	public AccountStatement setAccount(Integer account) {
		this.account = account;
		return this;
	}
	
	public String getAccountCode() {
		return accountCode;
	}
	
	public AccountStatement setAccountCode(String accountCode) {
		this.accountCode = accountCode;
		return this;
	}

	public String getAccountDescription() {
		return accountDescription;
	}

	public AccountStatement setAccountDescription(String accountDescription) {
		this.accountDescription = accountDescription;
		return this;
	}

	public String getConcept() {
		return concept;
	}

	public AccountStatement setConcept(String concept) {
		this.concept = concept;
		return this;
	}

	public double getDebit() {
		return debit;
	}

	public AccountStatement setDebit(double debit) {
		this.debit = debit;
		return this;
	}

	public double getCredit() {
		return credit;
	}

	public AccountStatement setCredit(double credit) {
		this.credit = credit;
		return this;
	}
	public double getDebitBalance() {
		return debitBalance;
	}
	public AccountStatement setDebitBalance(double debitBalance) {
		this.debitBalance = debitBalance;
		return this;
	}
	
	public double getUnpaidBalance() {
		return unpaidBalance;
	}
	public AccountStatement setUnpaidBalance(double unpaidBalance) {
		this.unpaidBalance = unpaidBalance;
		return this;
	}

	public Integer getBalancingAccount() {
		return balancingAccount;
	}

	public AccountStatement setBalancingAccount(Integer balancingAccount) {
		this.balancingAccount = balancingAccount;
		return this;
	}
	
	public String getBalancingAccountCode() {
		return balancingAccountCode;
	}

	public AccountStatement setBalancingAccountCode(String balancingAccountCode) {
		this.balancingAccountCode = balancingAccountCode;
		return this;
	}
	
	public String getBalancingAccountDescription() {
		return balancingAccountDescription;
	}

	public AccountStatement setBalancingAccountDescription(String balancingAccountDescription) {
		this.balancingAccountDescription = balancingAccountDescription;
		return this;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public AccountStatement setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
		return this;
	}
	
}
