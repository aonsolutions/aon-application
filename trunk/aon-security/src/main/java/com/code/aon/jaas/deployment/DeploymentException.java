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

	/** Deployment exception error code, if 0 then no internal error has occurred. */
	private int errorCode;

	/**
     * Construct a <tt>DeploymentException</tt> with the specified detail message.
     * 
     * @param String
     */
    public DeploymentException(String msg) {
        this( msg, 0, null );
    }

    /**
     * Construct a <tt>DeploymentException</tt> with the specified detail message and 
     * nested <tt>Throwable</tt>.
     * 
     * @param String
     * @param Throwable.
     */
    public DeploymentException(String msg, Throwable nested) {
        this( msg, 0, nested );
    }

    /**
     * Construct a <tt>DeploymentException</tt> with the specified detail message and 
     * nested <tt>Throwable</tt>.
     * 
     * @param String
     * @param int.
     * @param Throwable.
     */
    public DeploymentException(String msg, int errorCode, Throwable nested) {
        super( msg, nested );
        this.errorCode = errorCode;
    }

    /**
     * Retuns error code.
     * 
     * @return
     */
	public int getErrorCode() {
		return errorCode;
	}

}