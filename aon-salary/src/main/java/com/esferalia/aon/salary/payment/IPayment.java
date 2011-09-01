package com.esferalia.aon.salary.payment;

import com.esferalia.aon.salary.ISalaryItem;
import com.esferalia.aon.salary.enumeration.PaymentType;

public interface IPayment extends ISalaryItem<PaymentType>{
		
	public String getExpression();
	

}
