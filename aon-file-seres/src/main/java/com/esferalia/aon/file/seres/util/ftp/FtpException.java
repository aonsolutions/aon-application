package com.esferalia.aon.file.seres.util.ftp;


public class FtpException extends Throwable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public FtpException(String message) {
		super(message);
	}
	
	public FtpException(Throwable cause) {
		super(cause);
	}
	
	public FtpException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
