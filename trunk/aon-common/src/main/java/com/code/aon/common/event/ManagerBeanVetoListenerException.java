package com.code.aon.common.event;

import com.code.aon.common.AonException;

/**
 * The class <code>ManagerBeanVetoListenerException</code> indicates conditions created by 
 * a <code>IManagerBeanVetoListener</code> that a reasonable AON application might want to 
 * catch.
 * 
 * @author 	Consulting & Development.
 * @since 	1.0
 * @see		com.code.aon.common.AonException
 *
 */

public class ManagerBeanVetoListenerException extends AonException {

	private static final long serialVersionUID = 591316645954253752L;

	/**
     * Constructs a new exception with <code>null</code> as its detail message.
     */
    public ManagerBeanVetoListenerException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message the detail message.
     */
    public ManagerBeanVetoListenerException(String message) {
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
    public ManagerBeanVetoListenerException(Throwable cause) {
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
    public ManagerBeanVetoListenerException(String message, Throwable cause) {
        super(message, cause);
    }

}