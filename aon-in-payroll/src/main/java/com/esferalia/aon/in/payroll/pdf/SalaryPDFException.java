package com.esferalia.aon.in.payroll.pdf;

public class SalaryPDFException extends RuntimeException {

	public SalaryPDFException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SalaryPDFException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SalaryPDFException(String message, Throwable cause) {
		super(message, cause);
	}

	public SalaryPDFException(String message) {
		super(message);
	}

	public SalaryPDFException(Throwable cause) {
		super(cause);
	}

	public SalaryPDFException(String format, Object ...args) {
		super(String.format(format, args));
	}
}
