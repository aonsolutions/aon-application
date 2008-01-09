/**
 * 
 */
package com.code.aon.jaas.client.ast;

import com.code.aon.jaas.storage.StorageException;

/**
 * The class <code>UserAlreadyExistException</code> and its subclasses are a form of 
 * <code>Throwable</code> that indicates conditions that a deployment process 
 * application might want to catch.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 24/05/2007
 * @since 1.0
 *  
 */
public class UserAlreadyExistException extends StorageException {

	/**
     * Construct a <tt>CRUDException</tt> with the specified detail message.
     * 
     * @param message
     */
    public UserAlreadyExistException(String message) {
        super( message );
    }

}