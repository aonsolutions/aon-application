package solutions.aon.in.invoice;

public class UnknownInvoiceException extends Exception {

	public UnknownInvoiceException() {
		super();
	}

	public UnknownInvoiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UnknownInvoiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownInvoiceException(String message) {
		super(message);
	}

	public UnknownInvoiceException(Throwable cause) {
		super(cause);
	}
	

}
