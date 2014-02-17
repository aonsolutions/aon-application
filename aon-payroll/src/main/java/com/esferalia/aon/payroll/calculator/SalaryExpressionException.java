package com.esferalia.aon.payroll.calculator;

import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;

public class SalaryExpressionException extends ExpressionException {

	public SalaryExpressionException(SalaryException cause) {
		super(cause);
	}
	
	SalaryException getSalaryException(){
		return (SalaryException) getCause();
	}
}
