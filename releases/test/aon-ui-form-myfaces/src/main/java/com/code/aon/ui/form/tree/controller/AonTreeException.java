package com.code.aon.ui.form.tree.controller;

/**
 * AonTreeException represents an exception ocurred handling tree structures.
 */
public class AonTreeException extends Exception {

	/**
	 * The empty constructor.
	 */
	public AonTreeException() {
		super();
	}

	/**
	 * Creates an AonTreeException with the specified message
	 * 
	 * @param message the message
	 */
	public AonTreeException(String message) {
		super(message);
	}

	/**
	 * Creates an AonTreeException with the specified cause.
	 * 
	 * @param cause the cause
	 */
	public AonTreeException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates an AonTreeException with the specified cause and message
	 * 
	 * @param message the message
	 * @param cause the cause
	 */
	public AonTreeException(String message, Throwable cause) {
		super(message, cause);
	}

}
