package com.esferalia.aon.payroll.calculator;

import com.esferalia.aon.payroll.enumeration.ContextVariable;

public class UndefinedTotalPaymentException extends
		UndefinedContextVariablesException {

	public UndefinedTotalPaymentException() {
		super(ContextVariable.TOTAL_PAYMENT);
	}
}