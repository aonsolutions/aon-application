package com.esferalia.aon.salary;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;

public class SalaryException extends AonException {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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
