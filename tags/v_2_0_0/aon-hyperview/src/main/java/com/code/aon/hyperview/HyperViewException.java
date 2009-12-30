package com.code.aon.hyperview;

import com.code.aon.common.AonException;

public class HyperViewException extends AonException {

	/**
	 * 
	 */
	public HyperViewException() {
		super();
	}

	/**
	 * @param message
	 */
	public HyperViewException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public HyperViewException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public HyperViewException(String message, Throwable cause) {
		super(message, cause);
	}

}
