package com.code.aon.common.dao.sql;

/**
 * The class <code>DAOException</code> indicates conditions created by 
 * a <code>IDAO</code> that a reasonable AON application might want to catch.
 * 
 * @author Consulting & Development. Eugenio Castellano - 01-feb-2005
 * @since 1.0
 *  
 */
public class DAOException extends Exception {

	private static final long serialVersionUID = 388058449916834996L;

	/**
     * Constructs a new exception with <code>null</code> as its detail message.
     */
	public DAOException() {
	}

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message the detail message.
     */
	public DAOException(String message) {
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
	public DAOException(Throwable cause) {
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
	public DAOException(String message, Throwable cause) {
		super(message, cause);
	}

}
