package com.esferalia.aon.payroll.calculator;

import com.code.aon.AonVersion;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;

public class SalaryExpressionException extends ExpressionException {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public SalaryExpressionException(SalaryException cause) {
		super(cause);
	}
	
	SalaryException getSalaryException(){
		return (SalaryException) getCause();
	}
}
