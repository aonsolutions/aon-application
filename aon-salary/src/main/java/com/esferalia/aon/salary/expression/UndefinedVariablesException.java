package com.esferalia.aon.salary.expression;

public class UndefinedVariablesException extends ExpressionException {

	private static final long serialVersionUID = 5805991372724286694L;
	
	private String variableNames [];
	

	public UndefinedVariablesException(String ...variableNames) {
		this.variableNames = variableNames;
	}

	public String [] getVariableNames() {
		return this.variableNames;
	}
	
	 
	
	
	
}
