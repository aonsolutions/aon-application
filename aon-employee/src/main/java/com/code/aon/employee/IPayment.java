package com.code.aon.employee;

import com.code.aon.employee.enumeration.PaymentType;

public interface IPayment {
		
	public PaymentType getType();
	
	public String getDescription();
	
	public String getFunction();
	
	public double getAmount();
	
}
