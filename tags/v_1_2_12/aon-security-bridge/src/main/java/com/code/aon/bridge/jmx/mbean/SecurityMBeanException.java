package com.code.aon.bridge.jmx.mbean;

/**
 * Excepción que enmascara cualquier otra excepción producida durante los
 * procesos.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 22-jun-2004
 * @since 1.0
 *  
 */
public class SecurityMBeanException extends Exception {

	/**
	 * Determines if a de-serialized file is compatible with this class.
	 *
	 * Maintainers must change this value if and only if the new version
	 * of this class is not compatible with old versions. See Sun docs
	 * for details.
	 *
	 * Not necessary to include in first version of the class, but
	 * included here as a reminder of its importance.
	 */
	private static final long serialVersionUID = -903194032569497917L;

	/**
     * Construye una nueva excepción con el mensaje especificado.
     * 
     * @param message
     *            El Detalle del mensaje.
     */
    public SecurityMBeanException(String message) {
        super(message);

    }
}