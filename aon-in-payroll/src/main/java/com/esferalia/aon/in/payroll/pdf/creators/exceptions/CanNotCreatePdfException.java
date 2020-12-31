package com.esferalia.aon.in.payroll.pdf.creators.exceptions;

public class CanNotCreatePdfException extends Exception{

	public CanNotCreatePdfException() {
	}

	public CanNotCreatePdfException(String message) {
		super(message);
	}

	public CanNotCreatePdfException(String message, Throwable cause) {
		super(message, cause);
	}

	public CanNotCreatePdfException(Throwable cause) {
		super(cause);
	}
}
