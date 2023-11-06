package com.esferalia.aon.in.payroll.img;

public class PersonDocumentParserException extends RuntimeException {

	private static final long serialVersionUID = -4248133409420201336L;

	public PersonDocumentParserException() {
		super();
	}

	public PersonDocumentParserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

	}

	public PersonDocumentParserException(String message, Throwable cause) {
		super(message, cause);
	}

	public PersonDocumentParserException(String message) {
		super(message);
	}

	public PersonDocumentParserException(Throwable cause) {
		super(cause);
	}

	public PersonDocumentParserException(String format, Object... args) {
		super(String.format(format, args));
	}
}
