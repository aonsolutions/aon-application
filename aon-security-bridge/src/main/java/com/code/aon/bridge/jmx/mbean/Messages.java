package com.code.aon.bridge.jmx.mbean;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Clase para obtener los mensajes en función de una clave.   
 * 
 * @author Consulting & Development. Eugenio Castellano - 17-feb-2005
 * @since 1.0
 *  
 */
public class Messages {
    /**
     * Recurso donde se encuentran los mensajes. En este caso
     * <code>com.code.aon.bridge.jmx.mbean.messages</code>.
     */
    private static final String BUNDLE_NAME = "com.code.aon.bridge.jmx.mbean.messages";//$NON-NLS-1$

    /**
     * Instancia estática de esta clase.
     */
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
            .getBundle(BUNDLE_NAME);

    /**
     * Devuelve el mensaje correspondiente a la clave especificada.
     * 
     * @param key
     *            String Clave del mensaje.
     * @return El mensaje correspondiente a la clave especificada.
     */
    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}