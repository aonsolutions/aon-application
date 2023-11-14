package com.esferalia.aon.occam.api.model.finance.nordigen;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonNumberUtils;

public class NordigenAccountAmount implements Serializable {
	
	private static final long serialVersionUID = 8429072833151492441L;		
	
	private Double amount;
	private String currency;
	
	public NordigenAccountAmount() {
		super();
	}
	
	public NordigenAccountAmount(Double amount, String currency) {
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

	public String getCurrency() {
		return currency;
	}

	public NordigenAccountAmount setCurrency(String currency) {
		this.currency = currency;
		return this;
	}
	
	public static double getAmount( NordigenAccountAmount amount) {
		if (amount != null) {
			return AonNumberUtils.zeroIfNull( amount.getAmount() ); 
		}
		return 0.0;
	}
	
}
