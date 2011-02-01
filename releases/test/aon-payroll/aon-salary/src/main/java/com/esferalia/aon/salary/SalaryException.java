package com.esferalia.aon.salary;

import com.code.aon.common.AonException;

public class SalaryException extends AonException {

	private static final long serialVersionUID = 2658939739517432990L;
	
	public SalaryException() {
		super();
	}
	public SalaryException(String message) {
		super(message);
	}
	public SalaryException(Throwable cause) {
		super(cause);
	}
	public SalaryException(String message, Throwable cause) {
		super(message, cause);
	}

}
