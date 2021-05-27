package solutions.aon.in.invoice.img;

public class InvoiceIMGException extends RuntimeException {

	private static final long serialVersionUID = -510898356845421382L;

	public InvoiceIMGException() {
		super();
	}

	public InvoiceIMGException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvoiceIMGException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvoiceIMGException(String message) {
		super(message);
	}

	public InvoiceIMGException(Throwable cause) {
		super(cause);
	}

	public InvoiceIMGException(String format, Object ...args) {
		super(String.format(format, args));
	}
}
