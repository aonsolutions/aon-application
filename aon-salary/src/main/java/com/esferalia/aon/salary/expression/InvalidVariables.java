package com.esferalia.aon.salary.expression;

import com.code.aon.AonVersion;


public class InvalidVariables extends CheckException {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String variableNames [];
	
	public InvalidVariables(String message, String... variableNames) {
		super(message);
		this.variableNames = variableNames;
	}
	
	public String [] getVariables() {
		return variableNames;
	}

}
