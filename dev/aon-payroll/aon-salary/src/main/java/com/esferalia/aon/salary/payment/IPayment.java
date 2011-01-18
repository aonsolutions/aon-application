package com.esferalia.aon.salary.payment;

import com.esferalia.aon.salary.enumeration.PaymentType;

public interface IPayment {
		
	public PaymentType getType();
	
	public String getDescription();
	
	public String getExpression();
	
	public double getAmount();
	
}
