package com.esferalia.aon.occam.api.model.finance.nordigen;

import java.io.Serializable;
import java.util.Currency;

public class NordigenAccountAmount implements Serializable {
	
	private static final long serialVersionUID = 8429072833151492441L;		
	
	private Double amount;
	private Currency currency;
	
	public NordigenAccountAmount() {
		super();
	}
	
	public NordigenAccountAmount(Double amount, Currency currency) {
		this.amount = amount;
		this.currency = currency;
	}

	public Double getAmount() {
		return amount;
	}

	public NordigenAccountAmount setAmount(Double amount) {
		this.amount = amount;
		return this;
	}

	public Currency getCurrency() {
		return currency;
	}

	public NordigenAccountAmount setCurrency(Currency currency) {
		this.currency = currency;
		return this;
	}
	
}
