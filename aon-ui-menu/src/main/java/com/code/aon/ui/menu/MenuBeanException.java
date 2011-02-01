package com.code.aon.ui.menu;

/**
 * The class <code>MenuBeanException</code> indicates conditions created by 
 * a {@link com.code.aon.ui.menu.jsf.IJSFMenu} that a reasonable AON application
 * might want to catch.
 * 
 * @author Consulting & Development. Aimar Tellitu - 26-ago-2005
 * @since 1.0
 *  
 */
public class MenuBeanException extends Exception {

	/**
     * Constructs a new exception with <code>null</code> as its detail message.
	 */
    public MenuBeanException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message the detail message.
     */
    public MenuBeanException(String message) {
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
    public MenuBeanException(Throwable cause) {
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
    public MenuBeanException(String message, Throwable cause) {
        super(message, cause);
    }

}