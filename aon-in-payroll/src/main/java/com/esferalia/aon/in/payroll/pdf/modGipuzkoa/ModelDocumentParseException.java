package com.esferalia.aon.in.payroll.pdf.modGipuzkoa;

public class ModelDocumentParseException extends RuntimeException {

	private static final long serialVersionUID = -1519405927161789161L;

	public ModelDocumentParseException() {
		super();
	}

	public ModelDocumentParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

	}

	public ModelDocumentParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public ModelDocumentParseException(String message) {
		super(message);
	}

	public ModelDocumentParseException(Throwable cause) {
		super(cause);
	}

	public ModelDocumentParseException(String format, Object... args) {
		super(String.format(format, args));
	}
}
