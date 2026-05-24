package com.esferalia.aon.occam.api.model.type;

public class UnknownSalaryTypeException extends RuntimeException {

	public UnknownSalaryTypeException(SalaryType salaryType) {
		super("Unknown salary type: " + salaryType.name());
	}

}
