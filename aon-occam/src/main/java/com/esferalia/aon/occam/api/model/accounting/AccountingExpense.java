package com.esferalia.aon.occam.api.model.accounting;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;

public class AccountingExpense implements Serializable{
	
	private EnterpriseActivity activity;
	private Account account;
	private Date date;
	private Creditor creditor;
	private String description;
	private String reference;
	private double amount;
	private RegistryBank rbank;
	private String comments;
	
	public AccountingExpense() {
		
	}

	public Optional<EnterpriseActivity> getActivity() {
		return Optional.ofNullable(activity);
	}

	public AccountingExpense setActivity(EnterpriseActivity activity) {
		this.activity = activity;
		return this;
	}

	public Account getAccount() {
		return account;
	}

	public AccountingExpense setAccount(Account account) {
		this.account = account;
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

	public String getDescription() {
		return description;
	}

	public AccountingExpense setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getReference() {
		return reference;
	}

	public AccountingExpense setReference(String reference) {
		this.reference = reference;
		return this;
	}

	public double getAmount() {
		return amount;
	}

	public AccountingExpense setAmount(double amount) {
		this.amount = amount;
		return this;
	}

	public Optional<RegistryBank> getRbank() {
		return Optional.ofNullable(rbank);
	}

	public AccountingExpense setRbank(RegistryBank rbank) {
		this.rbank = rbank;
		return this;
	}

	public String getComments() {
		return comments;
	}

	public AccountingExpense setComments(String comments) {
		this.comments = comments;
		return this;
	}

	
}
