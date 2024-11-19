package com.esferalia.aon.salary.expression;

import com.code.aon.AonVersion;

public class UndefinedVariablesException extends ExpressionException {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String expression;
	private String variableNames [];
	

	public UndefinedVariablesException(String ...variableNames) {
		this.variableNames = variableNames;
	}

	public UndefinedVariablesException(String expression, String [] variableNames) {
		this(variableNames);
		this.expression = expression;
	}

	public String getExpression() {
		return expression;
	}
	
	public String [] getVariableNames() {
		return this.variableNames;
	}
	
	
	public void setExpression(String expression) {
		this.expression = expression;
	}
	 
	
	public boolean hasVariableName(String var) {
		if (var == null)
			return false;
		for (String v : variableNames)
			if (var.equals(v))
				return true;
		return false;
	}
	
	
}
