package net.aonsolutions.aon.in.pdf.api.exception;

public class ImpossibleDrawingException extends Exception {

	public ImpossibleDrawingException() {
		super();
	}

	public ImpossibleDrawingException(
			String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace
	) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ImpossibleDrawingException(String message, Throwable cause) {
		super(message, cause);
	}

	public ImpossibleDrawingException(String message) {
		super(message);
	}

	public ImpossibleDrawingException(Throwable cause) {
		super(cause);
	}

}
