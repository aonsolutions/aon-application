package aon.bank.exceptions;

public class BankException extends Exception {

	public BankException() {
		super();
	}

	public BankException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BankException(String message, Throwable cause) {
		super(message, cause);
	}

	public BankException(String message) {
		super(message);
	}

	public BankException(Throwable cause) {
		super(cause);
	}
	
}
