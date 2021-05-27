package com.esferalia.aon.seres.ftp;


public class FtpLoginException extends Throwable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public FtpLoginException(String message) {
		super(message);
	}
	
	public FtpLoginException(Throwable cause) {
		super(cause);
	}
	
	public FtpLoginException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
