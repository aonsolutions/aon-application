package com.esferalia.aon.in.payroll.pdf.creators.exceptions;

public class JsonParseException extends Exception{
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
