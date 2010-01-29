package com.code.aon.ui.cms.hiru;

public class XmlBuilderException extends Exception {

	/**
     * Constructs a new exception with <code>null</code> as its detail message.
     */
    public XmlBuilderException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message the detail message.
     */
    public XmlBuilderException(String message) {
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
    public XmlBuilderException(Throwable cause) {
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
    public XmlBuilderException(String message, Throwable cause) {
        super(message, cause);
    }

}
