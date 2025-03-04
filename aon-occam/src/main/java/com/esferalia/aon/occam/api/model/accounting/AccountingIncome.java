package com.esferalia.aon.occam.api.model.accounting;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;

public class AccountingIncome implements Serializable{
	
	private EnterpriseActivity activity;
	private Account account;
	private Date date;
	private Customer customer;
	private String description;
	private String reference;
	private double amount;
	private RegistryBank rbank;
	private String comments;
	
	public AccountingIncome() {
		
	}

	public Optional<EnterpriseActivity> getActivity() {
		return Optional.ofNullable(activity);
	}

	public AccountingIncome setActivity(EnterpriseActivity activity) {
		this.activity = activity;
		return this;
	}

	public Account getAccount() {
		return account;
	}

	public AccountingIncome setAccount(Account account) {
		this.account = account;
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

	public String getDescription() {
		return description;
	}

	public AccountingIncome setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getReference() {
		return reference;
	}

	public AccountingIncome setReference(String reference) {
		this.reference = reference;
		return this;
	}

	public double getAmount() {
		return amount;
	}

	public AccountingIncome setAmount(double amount) {
		this.amount = amount;
		return this;
	}

	public Optional<RegistryBank> getRbank() {
		return Optional.ofNullable(rbank);
	}

	public AccountingIncome setRbank(RegistryBank rbank) {
		this.rbank = rbank;
		return this;
	}

	public String getComments() {
		return comments;
	}

	public AccountingIncome setComments(String comments) {
		this.comments = comments;
		return this;
	}

}
