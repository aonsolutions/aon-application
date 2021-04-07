package com.esferalia.aon.in.payroll.pdf.maker.exceptions;

public class JsonParseException extends Exception{

	private static final long serialVersionUID = 1L;

	public JsonParseException() {
	}

	public JsonParseException(String message) {
		super(message);
	}

	public JsonParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public JsonParseException(Throwable cause) {
		super(cause);
	}
}
