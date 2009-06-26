package com.code.aon.report;

import com.code.aon.common.AonException;

/**
 * Thrown when an exceptional condition has occurred.
 * 
 * @author Consulting & Development. ecastellano - 14-nov-2005
 */
public class ReportException extends AonException {
	/**
	 * Constructs a <code>ReportException</code> with the specified detail message.
	 * 
	 * @param message
	 *            El detalle del mensaje.
	 */
	public ReportException(String message) {
		super(message);
	}

	/**
	 * Constructs a <code>ReportException</code> with the specified detail message and cause.
	 * 
	 * @param message
	 *            El detalle del mensaje.
	 * @param cause
	 *            Causa de la excepcion.
	 */
	public ReportException(String message, Throwable cause) {
		super(message, cause);
	}

}