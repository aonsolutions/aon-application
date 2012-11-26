package com.code.aon.messaging.sms;

/**
 * The class <code>SMSException</code> and its subclasses are a form of 
 * <code>Throwable</code> that indicates conditions that a reasonable 
 * AON application might want to catch.
 * 
 * @author Consulting & Development. Aimar Tellitu - 11-feb-2009
 * @since  1.0
 * @see    java.lang.Exception
 *  
 */
public class SMSException extends Exception {

	private static final long serialVersionUID = -3468394608624163825L;

	/**
     * Constructs a new exception with <code>null</code> as its detail message.
     */
    public SMSException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message the detail message.
     */
    public SMSException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified cause and a detail
     * message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
     * 
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).
     */
    public SMSException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     * 
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).
     */
    public SMSException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
