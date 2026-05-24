package com.esferalia.aon.salary.enumeration;

@SuppressWarnings("serial")
public class UnknownSalaryTypeException extends RuntimeException {
	
	private SalaryType salaryType;
	
	UnknownSalaryTypeException(SalaryType salaryType) {
		super("Unknown salary type " + salaryType.name());
		this.salaryType = salaryType;
	}

	
}
