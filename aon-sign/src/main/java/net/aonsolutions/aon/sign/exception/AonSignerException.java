package net.aonsolutions.aon.sign.exception;

public class AonSignerException extends Exception{

	private static final long serialVersionUID = 1L;
	private static final String DEFAULT = "No se ha podido realizar la firma.";
	
	public AonSignerException() {
		super(DEFAULT);
	}

	public AonSignerException(String message) {
		super(message);
	}

	public AonSignerException(String message, Throwable cause) {
		super(message, cause);
	}

	public AonSignerException(Throwable cause) {
		super(cause);
	}
}
