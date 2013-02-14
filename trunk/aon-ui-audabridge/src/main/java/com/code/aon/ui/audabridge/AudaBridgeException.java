package com.code.aon.ui.audabridge;

public class AudaBridgeException extends Exception{

	private static final long serialVersionUID = -7178768358465216689L;
	
	public AudaBridgeException() {
		super();
	}
	
	public AudaBridgeException(String message) {
		super(message);
	}
	
	public AudaBridgeException(Throwable cause) {
		super(cause);
	}
	
	public AudaBridgeException(String message, Throwable cause) {
		super(message, cause);
	}

}