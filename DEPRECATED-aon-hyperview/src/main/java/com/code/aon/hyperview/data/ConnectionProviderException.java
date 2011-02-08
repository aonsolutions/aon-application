package com.code.aon.hyperview.data;

import com.code.aon.common.AonException;

public class ConnectionProviderException extends AonException {

	/**
	 * 
	 */
	public ConnectionProviderException() {
		super();
	}

	/**
	 * @param message
	 */
	public ConnectionProviderException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ConnectionProviderException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ConnectionProviderException(String message, Throwable cause) {
		super(message, cause);
	}

}
