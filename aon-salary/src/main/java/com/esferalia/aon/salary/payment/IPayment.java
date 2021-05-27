package com.esferalia.aon.salary.payment;

import java.io.Serializable;

import com.esferalia.aon.salary.ISalaryItem;
import com.esferalia.aon.salary.enumeration.PaymentType;

public interface IPayment extends ISalaryItem<PaymentType>, Serializable {
		
	public String getExpression();
	

}
