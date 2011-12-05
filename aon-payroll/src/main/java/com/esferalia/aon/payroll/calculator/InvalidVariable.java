package com.esferalia.aon.payroll.calculator;

import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.expression.CheckException;

public class InvalidVariable extends CheckException {
	
	private String variableName;
	
	public InvalidVariable(String variableName, String message) {
		super(message);
		this.variableName = variableName;
	}
	
	public String getVariable() {
		return variableName;
	}

}
