package com.esferalia.aon.salary.expression;


public class InvalidVariables extends CheckException {
	
	private String variableNames [];
	
	public InvalidVariables(String message, String... variableNames) {
		super(message);
		this.variableNames = variableNames;
	}
	
	public String [] getVariables() {
		return variableNames;
	}

}
