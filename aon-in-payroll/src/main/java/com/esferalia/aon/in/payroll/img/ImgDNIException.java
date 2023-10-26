package com.esferalia.aon.in.payroll.img;

public class ImgDNIException extends RuntimeException {

	private static final long serialVersionUID = -6074883342923735687L;

	public ImgDNIException() {
		super();
	}
	
	public ImgDNIException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}
	
	public ImgDNIException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ImgDNIException(String message) {
		super(message);
	}
	
	public ImgDNIException(Throwable cause) {
		super(cause);
	}
	
	public ImgDNIException(String format, Object ...args) {
		super(String.format(format, args));
	}
}
