package com.code.aon.common.dao;

/**
 * The class <code>DAOSysException</code> indicates conditions created by 
 * a <code>IDAO</code> that a reasonable AON application might want to catch.
 * 
 * @author 	Consulting & Development. Iñaki Ayerbe - 16-may-2005
 * @since 	1.0
 * @see		java.lang.RuntimeException
 *
 */

public class DAOSysException extends RuntimeException {

	private static final long serialVersionUID = 6906588225314153492L;

	/**
     * Constructs a new exception with <code>null</code> as its detail message.
	 */
	public DAOSysException() {
	}

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message the detail message.
     */
	public DAOSysException(String message) {
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
	public DAOSysException(Throwable cause) {
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
	public DAOSysException(String message, Throwable cause) {
		super(message, cause);
	}

}
