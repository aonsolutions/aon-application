/*
 * Created on 11-oct-2006
 *
 */
package com.code.aon.bridge.jndi;

/**
 * Esta clase aglutina aquellos errores producidos en la utilización de
 * <code>ServiceLocator</code>.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 11-oct-2006
 * @since 1.0
 *  
 */
public class SecurityLocatorException extends Exception {

    /**
     * Construye una nueva excepción con <code>null</code> como detalle de su mensaje.
     */
    public SecurityLocatorException() {
        super();
    }

    /**
     * Construye una nueva excepcion con el mensaje especificado.
     *
     * @param   message   El detalle del mensaje.
     */
    public SecurityLocatorException(String message) {
        super(message);
    }

    /**
     * Construye una nueva excepción con la causa especificada y como mensaje 
     * <tt>(cause==null ? null : cause.toString())</tt> (que 
     * normalmente contiene la clase y el detalle de <tt>cause</tt>).
     * 
     * @param cause La causa del problema. 
     */
    public SecurityLocatorException(Throwable cause) {
        super(cause);
    }

    /**
     * Construye una nueva excepción con el mensaje y la causa especificados.
     * 
     * @param  message El detalle del mensaje.
     * @param cause Causa de la excepcion.
     */
    public SecurityLocatorException(String message, Throwable cause) {
        super(message, cause);
    }

}
