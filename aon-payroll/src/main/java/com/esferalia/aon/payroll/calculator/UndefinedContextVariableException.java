package com.esferalia.aon.payroll.calculator;

import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;

public class UndefinedContextVariableException extends
		UndefinedVariablesException {

	public UndefinedContextVariableException(ContextVariable variable) {
		super(variable.getName());
	}
}