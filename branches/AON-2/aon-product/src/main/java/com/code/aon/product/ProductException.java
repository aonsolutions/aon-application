package com.code.aon.product;

/**
 * This class includes the errors produced by products that must be catched
 * 
 * @author Consulting & Development. Iñigo GAyarre - 14-mar-2005
 * @since 1.0
 * @version 1.0
 *  
 */
public class ProductException extends Exception {

    /**
     * Void contructor
     */
    public ProductException() {
        super();
    }

    /**
     * Contructor with a concrete message.
     * 
     * @param message
     *            Message's detail.
     */
    public ProductException(String message) {
        super(message);
    }

    /**
     * Makes a new exception with this cause and as mesage
     * <tt>(cause==null ? null : cause.toString())</tt>.
     * 
     * @param cause
     *            Problem cause.
     */
    public ProductException(Throwable cause) {
        super(cause);
    }

    /**
     * Makes a new exception with this cause and mesage.
     * 
     * @param message
     *            Message detail.
     * @param cause
     *            Exception cause.
     */
    public ProductException(String message, Throwable cause) {
        super(message, cause);
    }

}