package com.esferalia.aon.salary.expression;

import com.code.aon.AonVersion;

public class UndefinedVariablesException extends ExpressionException {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String variableNames [];
	

	public UndefinedVariablesException(String ...variableNames) {
		this.variableNames = variableNames;
	}

	public String [] getVariableNames() {
		return this.variableNames;
	}
	
	 
	
	
	
}
