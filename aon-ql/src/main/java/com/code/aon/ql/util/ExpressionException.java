package com.code.aon.ql.util;

/**
 * 
 * @author Consulting & Development. Eugenio Castellano - 31-mar-2005
 * @since 1.0
 * @version 1.0
 *  
 */
public class ExpressionException extends Exception {

	private static final long serialVersionUID = 840796588610856591L;

	/**
     * Constructs a new exception with <code>null</code> as its detail message.
     */
    public ExpressionException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message the detail message.
     */
    public ExpressionException(String message) {
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
    public ExpressionException(Throwable cause) {
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
    public ExpressionException(String message, Throwable cause) {
        super(message, cause);
    }

}