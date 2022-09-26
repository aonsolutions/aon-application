package net.aonsolutions.aon.bank.checkit;

public class CheckItException extends Exception {

	private static final long serialVersionUID = -5640775455927597322L;
	
	public static final String NO_CONNECTION_MSG = "No se pudo establecer la conexi\u00F3n con el servicio de agregador bancario";

	public CheckItException() {
		super();
	}

	public CheckItException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CheckItException(String message, Throwable cause) {
		super(message, cause);
	}

	public CheckItException(String message) {
		super(message);
	}

	public CheckItException(Throwable cause) {
		super(cause);
	}

}
