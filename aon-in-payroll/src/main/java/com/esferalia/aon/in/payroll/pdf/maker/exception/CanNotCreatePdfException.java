package com.esferalia.aon.in.payroll.pdf.maker.exception;

public class CanNotCreatePdfException extends Exception{

	private static final long serialVersionUID = 8334888276412793998L;

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
