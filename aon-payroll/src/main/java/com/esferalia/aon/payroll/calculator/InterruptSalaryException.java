package com.esferalia.aon.payroll.calculator;

import com.code.aon.AonVersion;
import com.esferalia.aon.salary.SalaryException;

public class InterruptSalaryException extends SalaryException {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public InterruptSalaryException() {
		// TODO Auto-generated constructor stub
	}

	public InterruptSalaryException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public InterruptSalaryException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public InterruptSalaryException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
