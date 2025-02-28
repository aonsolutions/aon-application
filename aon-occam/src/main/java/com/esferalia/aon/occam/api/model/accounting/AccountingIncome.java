package com.esferalia.aon.occam.api.model.accounting;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.finance.PayMethod;

public class AccountingIncome implements Serializable{
	
	private EnterpriseActivity activity;
	private Date date;
	private Customer customer;
	private String description;
	private String reference;
	private double amount;
	private PayMethod paymethod;
	private String comments;
	
	public AccountingIncome() {
		
	}
	
	public EnterpriseActivity getActivity() {
		return activity;
	}
	
	public AccountingIncome setActivity(EnterpriseActivity activity) {
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
	
	public Customer getCustomer() {
		return customer;
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
	
	public PayMethod getPaymethod() {
		return paymethod;
	}
	
	public AccountingIncome setPaymethod(PayMethod paymethod) {
		this.paymethod = paymethod;
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
