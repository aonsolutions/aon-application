package com.code.aon.jaas.deployment;

/**
 * The class <code>DeploymentException</code> and its subclasses are a form of 
 * <code>Throwable</code> that indicates conditions that a deployment process 
 * application might want to catch.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 13-may-2004
 * @since 1.0
 *  
 */
public class DeploymentException extends Exception {

	private static final long serialVersionUID = -383206265808193166L;

	/**
     * Construct a <tt>DeploymentException</tt>
     * with the specified detail message.
     * 
     * @param string
     *            Detail message.
     */
    public DeploymentException(String string) {
        super(string);
    }

    /**
     * Construct a <tt>DeploymentException</tt>
     * with the specified detail message and nested <tt>Throwable</tt>.
     * 
     * @param msg
     *            Detail message.
     * @param nested
     *            Nested <tt>Throwable</tt>.
     */
    public DeploymentException(String msg, Throwable nested) {
        super(msg, nested);
    }

}