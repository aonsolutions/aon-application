package com.esferalia.aon.salary.expression;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.code.aon.AonVersion;

public class UndefinedVariablesException extends ExpressionException {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String expression;
	private String variableNames [];
	
	private Map<String, ITimedVariable<?>> context ;
	

	public UndefinedVariablesException(String ...variableNames) {
		this(Collections.emptyMap(), variableNames);
	}

	public UndefinedVariablesException(String expression, String [] variableNames) {
		this(Collections.emptyMap(), expression,variableNames);
	}

	public UndefinedVariablesException(Map<String, ITimedVariable<?>> context, String ...variableNames) {
		this.context = context;
		this.variableNames = variableNames;
	}

	public UndefinedVariablesException(Map<String, ITimedVariable<?>> context,String expression, String [] variableNames) {
		this(context,variableNames);
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
	 
	public Map<String, ITimedVariable<?>> getContext() {
		return context;
	}

	
	public boolean hasVariableName(String var) {
		if (var == null)
			return false;
		for (String v : variableNames)
			if (var.equals(v))
				return true;
		return false;
	}
	
	public boolean hasAnyVariableName(String ...vars) {
		for (String var : vars) {
			if ( hasVariableName(var))
				return true;
		}
		return false;
	}

	public boolean allAreOneOf(Collection<String> names) {
		if ( variableNames.length == 0 && names.size() > 0 )
			return false;
		for ( String variableName: variableNames ) {
			if ( !names.contains(variableName))
				return false;
		}
		return true;
	}
	
	
}
