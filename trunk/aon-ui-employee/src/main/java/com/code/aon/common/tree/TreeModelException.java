/*
 * Created on 05-sep-2006
 *
 */
package com.code.aon.common.tree;

/**
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 05-sep-2006
 * @since 1.0
 *
 */

public class TreeModelException extends Exception {

    /**
     * 
     */
    public TreeModelException() {
        super();
    }

    /**
     * @param message
     * @param cause
     */
    public TreeModelException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public TreeModelException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public TreeModelException(Throwable cause) {
        super(cause);
    }

}
