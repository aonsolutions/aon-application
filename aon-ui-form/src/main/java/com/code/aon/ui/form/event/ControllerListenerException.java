package com.code.aon.ui.form.event;

import com.code.aon.common.AonException;

/**
 * ControllerListenerException represents exceptions than can be generated within an IController.
 */
public class ControllerListenerException extends AonException {

	private static final long serialVersionUID = 3004694419410628899L;

	/**
     * Creates a new Exception with a <code>null</code> message.
     */
    public ControllerListenerException() {
        super();
    }

    /**
     * Creates a new Exception with the specified message.
     * 
     * @param message El detalle del mensaje.
     */
    public ControllerListenerException(String message) {
        super(message);
    }

    /**
     * Creates a new Exception with te specified cause and message
     * <tt>(cause==null ? null : cause.toString())</tt> (usually it contains
     * the class and the detail of <tt>cause</tt>).
     * 
     * @param cause La causa del problema.
     */
    public ControllerListenerException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new Exception with the specied cause and message.
     * 
     * @param message El detalle del mensaje.
     * @param cause Causa de la excepcion.
     */
    public ControllerListenerException(String message, Throwable cause) {
        super(message, cause);
    }

}