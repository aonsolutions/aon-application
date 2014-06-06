package com.esferalia.aon.payroll.calculator;

import com.code.aon.AonVersion;
import com.esferalia.aon.payroll.enumeration.ContextVariable;

public class UndefinedTotalPaymentException extends
		UndefinedContextVariablesException {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public UndefinedTotalPaymentException() {
		super(ContextVariable.TOTAL_PAYMENT);
	}
}