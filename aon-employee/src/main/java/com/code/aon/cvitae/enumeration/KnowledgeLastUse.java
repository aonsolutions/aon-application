package com.code.aon.cvitae.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum KnowledgeLastUse implements IResourceable {
	/**
	 * Actualmente
	 */
	AT_PRESENT,
	
	/**
	 * Más de 1 año
	 */
	MORE_ONE_YEAR,
	
	/**
	 * Más de 2 años
	 */
	MORE_TWO_YEARS,
	
	/**
	 * Más de 3 años
	 */
	MORE_THREE_YEARS;
	
	/**
     * Ruta base del fichero de mensajes.
     */
    private static final String BASE_NAME = "com.code.aon.cvitae.i18n.messages";

    /**
     * Prefijo de la llave de mensajes. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_lastuse_";
    
    /**
     * Devuelve un <code>String</code> con la traducción correspondiente al <code>Locale</code>
     * pasado por parámetro.
     * 
     * @return String un <code>String</code>.
     */
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
}
