package com.code.aon.planner;

/**
 * Esta clase aglutina aquellos errores producidos en los diferentes 
 * paquetes AON que sean susceptibles de ser capturados.
 * 
 * @author Consulting & Development. Eugenio Castellano - 01-feb-2005
 * @since 1.0
 *  
 */
public class EventException extends Exception {

	private static final long serialVersionUID = 8019424220873080448L;

	private Object[] parameters;

    /**
     * Construye una nueva excepción con <code>null</code> como detalle de su
     * mensaje.
     */
    public EventException() {
        super();
    }

    /**
     * Construye una nueva excepcion con el mensaje especificado.
     * 
     * @param message
     *            El detalle del mensaje.
     */
    public EventException(String message) {
        super(message);
    }

    /**
     * Construye una nueva excepción con la causa especificada y como mensaje
     * <tt>(cause==null ? null : cause.toString())</tt> (que normalmente
     * contiene la clase y el detalle de <tt>cause</tt>).
     * 
     * @param cause
     *            La causa del problema.
     */
    public EventException(Throwable cause) {
        super(cause);
    }

    /**
     * Construye una nueva excepción con el mensaje y la causa especificados.
     * 
     * @param message
     *            El detalle del mensaje.
     * @param cause
     *            Causa de la excepcion.
     */
    public EventException(String message, Throwable cause) {
        super(message, cause);
    }

	/**
	 * @return the parameters
	 */
	public Object[] getParameters() {
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

}