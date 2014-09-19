package com.esferalia.aon.gwt.common.shared;

/**
 * 
 * @author rtrepiana
 *
 */
public class EmptyStringException extends IllegalArgumentException {

	public EmptyStringException() {
	}

	public EmptyStringException(String s) {
		super(s);
	}

	public EmptyStringException(Throwable cause) {
		super(cause);
	}

	public EmptyStringException(String message, Throwable cause) {
		super(message, cause);
	}

}
