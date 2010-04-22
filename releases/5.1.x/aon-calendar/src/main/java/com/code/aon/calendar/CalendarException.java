package com.code.aon.calendar;

import com.code.aon.common.AonException;

/**
 * Esta clase aglutina aquellos errores producidos en los diferentes procesos
 * del paquete calendar que sean susceptibles de ser capturados.
 * 
 * @author Consulting & Development. Aimar Tellitu - 21-sep-2005
 * @since 1.0
 *  
 */
public class CalendarException extends AonException {
    /**
     * Construye una nueva excepción con el mensaje y la causa especificados.
     * 
     * @param message
     *            El detalle del mensaje.
     * @param cause
     *            Causa de la excepcion.
     */
    public CalendarException (String message) {
        super(message);
    }

    /**
     * Construye una nueva excepción con el mensaje y la causa especificados.
     * 
     * @param message
     *            El detalle del mensaje.
     * @param cause
     *            Causa de la excepcion.
     */
    public CalendarException (String message, Throwable cause) {
        super(message, cause);
    }

}