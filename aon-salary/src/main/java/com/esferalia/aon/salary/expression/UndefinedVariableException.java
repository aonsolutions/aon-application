package com.esferalia.aon.salary.expression;

public class UndefinedVariableException extends ExpressionException {

	private static final long serialVersionUID = 5805991372724286694L;
	
	private String variableName;
	
	public UndefinedVariableException(String variableName) {
		this(variableName, String.format("Undefined '%s'", variableName));
	}
	
	public UndefinedVariableException(String variableName, String message) {
		super(message);
		this.variableName = variableName;
	}

	public String getVariableName() {
		return this.variableName;
	}
	
}
