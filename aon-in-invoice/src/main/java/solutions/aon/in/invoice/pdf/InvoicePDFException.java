package solutions.aon.in.invoice.pdf;

public class InvoicePDFException extends RuntimeException {

	public InvoicePDFException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InvoicePDFException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvoicePDFException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvoicePDFException(String message) {
		super(message);
	}

	public InvoicePDFException(Throwable cause) {
		super(cause);
	}

	public InvoicePDFException(String format, Object ...args) {
		super(String.format(format, args));
	}
}
