package com.esferalia.aon.occam.api.model.accounting;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.registry.Creditor;

public class AccountingExpense implements Serializable{
	
	private EnterpriseActivity activity;
	private Date date;
	private Creditor creditor;
	private String description;
	private String reference;
	private double amount;
	private PayMethod paymethod;
	private String comments;
	
	public AccountingExpense() {
		
	}
	
	public EnterpriseActivity getActivity() {
		return activity;
	}
	
	public AccountingExpense setActivity(EnterpriseActivity activity) {
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
	
	public Creditor getCreditor() {
		return creditor;
	}
	
	public AccountingExpense setCreditor(Creditor customer) {
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
	
	public PayMethod getPaymethod() {
		return paymethod;
	}
	
	public AccountingExpense setPaymethod(PayMethod paymethod) {
		this.paymethod = paymethod;
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
