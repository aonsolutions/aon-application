package es.code.ecm.repository.core;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Base Runtime Exception class which will provide exception nesting functionalities.
 */
public abstract class BaseRuntimeException extends RuntimeException {

	/**
	 * Stable serialVersionUID.
	 */
	private static final long serialVersionUID = 222L;

	/**
	 * The cause for this exception.
	 */
	protected Throwable rootCause;

	/**
	 * Instantiate a new exception with an error message.
	 * @param message error message
	 */
	public BaseRuntimeException(String message) {
		super(message);
	}

	/**
	 * Instantiate a new exception with an error message and a previous exception.
	 * @param message error message
	 * @param rootCause previous exception
	 */
	public BaseRuntimeException(String message, Throwable rootCause) {
		super(message);
		this.rootCause = rootCause;
	}

	/**
	 * Instantiate a new exception with only previous exception.
	 * @param rootCause previous exception
	 */
	public BaseRuntimeException(Throwable rootCause) {
		this.rootCause = rootCause;
	}

	/**
	 * @see java.lang.Throwable#getMessage()
	 */
	public String getMessage() {
		String message = super.getMessage();
		if (rootCause == null) {
			return message;
		}

		String rootMessage = rootCause.getMessage();
		return message != null ? message + ": " + rootMessage : rootMessage; //$NON-NLS-1$
	}

	/**
	 * @see java.lang.Throwable#getLocalizedMessage()
	 */
	public String getLocalizedMessage() {
		String message = super.getLocalizedMessage();
		if (rootCause == null) {
			return message;
		}

		String rootMessage = rootCause.getLocalizedMessage();
		return message != null ? message + ": " + rootMessage : rootMessage; //$NON-NLS-1$
	}

	/**
	 * @see java.lang.Throwable#getCause()
	 */
	public Throwable getCause() {
		return rootCause;
	}

	/**
	 * @see java.lang.Throwable#printStackTrace()
	 */
	public void printStackTrace() {
		printStackTrace(System.err);
	}

	/**
	 * @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
	 */
	public void printStackTrace(PrintStream s) {
		synchronized (s) {
			super.printStackTrace(s);
			if (rootCause != null) {
				rootCause.printStackTrace(s);
			}
		}
	}

	/**
	 * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
	 */
	public void printStackTrace(PrintWriter s) {
		synchronized (s) {
			super.printStackTrace(s);
			if (rootCause != null) {
				rootCause.printStackTrace(s);
			}
		}
	}
}
