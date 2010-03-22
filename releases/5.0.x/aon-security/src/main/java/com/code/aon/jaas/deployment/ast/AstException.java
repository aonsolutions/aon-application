package com.code.aon.jaas.deployment.ast;

/**
 * The class <code>AstException</code> indicates conditions that a reasonable 
 * application might want to catch.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 06-feb-2004
 * @since 1.0
 *  
 */
public class AstException extends Exception {

	private static final long serialVersionUID = 7622707738978521128L;

	/**
     * Constructs a new exception with <code>null</code> as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     * 
     * @param cause
     */
    public AstException(Throwable cause) {
        super(cause);
    }
}