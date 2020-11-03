package solutions.aon.in.invoice;

public class UnknownInvoiceException extends Exception {
	private static final long serialVersionUID = 3011553751625387926L;

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
