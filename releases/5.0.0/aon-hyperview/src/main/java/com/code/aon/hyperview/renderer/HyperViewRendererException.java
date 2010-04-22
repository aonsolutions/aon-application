package com.code.aon.hyperview.renderer;

import com.code.aon.common.AonException;

/**
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 25-may-2005
 * @since 1.0
 *
 */

public class HyperViewRendererException extends AonException {

	/**
	 * 
	 */
	public HyperViewRendererException() {
		super();
	}

	/**
	 * @param message
	 */
	public HyperViewRendererException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public HyperViewRendererException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public HyperViewRendererException(String message, Throwable cause) {
		super(message, cause);
	}

}
