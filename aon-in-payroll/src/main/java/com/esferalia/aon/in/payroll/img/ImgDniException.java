package com.esferalia.aon.in.payroll.img;

public class ImgDniException extends RuntimeException {

	private static final long serialVersionUID = -6074883342923735687L;

	public ImgDniException() {
		super();
	}
	
	public ImgDniException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}
	
	public ImgDniException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ImgDniException(String message) {
		super(message);
	}
	
	public ImgDniException(Throwable cause) {
		super(cause);
	}
	
	public ImgDniException(String format, Object ...args) {
		super(String.format(format, args));
	}
}
