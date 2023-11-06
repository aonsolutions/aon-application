package com.esferalia.aon.in.payroll.img;

public class PersonDocumentExtractException extends RuntimeException {

	private static final long serialVersionUID = -6074883342923735687L;

	public PersonDocumentExtractException() {
		super();
	}
	
	public PersonDocumentExtractException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}
	
	public PersonDocumentExtractException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public PersonDocumentExtractException(String message) {
		super(message);
	}
	
	public PersonDocumentExtractException(Throwable cause) {
		super(cause);
	}
	
	public PersonDocumentExtractException(String format, Object ...args) {
		super(String.format(format, args));
	}
}
