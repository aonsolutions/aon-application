package com.code.aon.web.help.service.drive.exception;

public class GoogleDriveException extends Exception{

	public GoogleDriveException() {
		super();
	}
	
	public GoogleDriveException(String message) {
		super(message);
	}

	public GoogleDriveException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public GoogleDriveException(String message, Throwable cause) {
		super(message, cause);
	}

	public GoogleDriveException(Throwable cause) {
		super(cause);
	}	
	
}
