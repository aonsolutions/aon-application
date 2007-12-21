/**
 * 
 */
package com.code.aon.jaas.storage;

/**
 * The class <code>StorageException</code> and its subclasses are a form of 
 * <code>Throwable</code> that indicates conditions that a deployment process 
 * application might want to catch.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 24/05/2007
 * @since 1.0
 *  
 */
public class StorageException extends Exception {

	/**
     * Construct a <tt>CRUDException</tt> with the specified detail message.
     * 
     * @param message
     */
    public StorageException(String message) {
        super( message );
    }

    /**
     * Construct a <tt>CRUDException</tt> 
     * with the specified detail message and nested <tt>Throwable</tt>.
     * 
     * @param message
     * @param nested
     */
    public StorageException(String message, Throwable nested) {
        super( message, nested );
    }
}