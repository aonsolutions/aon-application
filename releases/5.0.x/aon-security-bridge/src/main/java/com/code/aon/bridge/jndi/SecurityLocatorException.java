package com.code.aon.bridge.jndi;

/**
 * The class <code>ServiceLocatorException</code> indicates conditions that a reasonable 
 * application might want to catch.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 11-oct-2006
 * @since 1.0
 *  
 */
public class SecurityLocatorException extends Exception {

	private static final long serialVersionUID = -4590373233362053938L;

	/**
     * Constructs a new exception with <code>null</code> as its detail message.
     */
    public SecurityLocatorException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message. 
     *
     * @param   message   the detail message. The detail message is saved for 
     *          later retrieval by the {@link #getMessage()} method.
     */
    public SecurityLocatorException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * <code>cause</code> is <i>not</i> automatically incorporated in
     * this exception's detail message.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public SecurityLocatorException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause and a detail
     * message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
     * 
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public SecurityLocatorException(Throwable cause) {
        super(cause);
    }

}
