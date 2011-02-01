package com.code.aon.common;

/**
 * The class <code>ManagerBeanException</code> indicates conditions created by 
 * a <code>IManagerBean</code> that a reasonable AON application might want to catch.
 * 
 * @author 	Consulting & Development. Iñaki Ayerbe - 25-may-2005
 * @since 	1.0
 * @see		com.code.aon.common.AonException
 *
 */

public class ManagerBeanException extends AonException {

	private static final long serialVersionUID = 3318774380387885954L;

	/**
     * Constructs a new exception with <code>null</code> as its detail message.
	 */
	public ManagerBeanException() {
		super();
	}

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message the detail message.
     */
	public ManagerBeanException(String message) {
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
	public ManagerBeanException(Throwable cause) {
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
	public ManagerBeanException(String message, Throwable cause) {
		super(message, cause);
	}

}
