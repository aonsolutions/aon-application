package com.esferalia.aon.payroll.calculator;

import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;

public class UndefinedContextVariablesException extends
		UndefinedVariablesException {

	public UndefinedContextVariablesException(ContextVariable... variables) {
		super(names(variables));
		;
	}
	
	/*
	 *  throvv ( throw ) I'm still laughing. :-) .
	 */
	public static void throvv(
			UndefinedVariablesException e)
			throws UndefinedVariablesException {
		String names[] = e.getVariableNames();
		ContextVariable variables[] = new ContextVariable[names.length];
		for (int i = 0; i < names.length; i++) {
			variables[i] = ContextVariable.getVariableByName(names[i]);
			if (variables[i] == null)
				throw e;
		}
		throw new UndefinedContextVariablesException(variables);
	}

	private static String[] names(ContextVariable... variables) {
		String names[] = new String[variables.length];

		for (int i = 0; i < variables.length; i++)
			names[i] = variables[i].getName();

		return names;
	}
}