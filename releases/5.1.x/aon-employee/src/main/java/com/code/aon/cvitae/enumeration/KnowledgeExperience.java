package com.code.aon.cvitae.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum KnowledgeExperience implements IResourceable {
	
	/**
	 * Menos de 1 año
	 */
	LESS_ONE_YEAR,
	
	/**
	 * Entre 1 y 2 años
	 */
	BETWEEN_ONE_TWO_YEARS,
	
	/**
	 * Entre 2 y 3 años
	 */
	BETWEEN_TWO_THREE_YEARS,
	
	/**
	 * Entre 3 y 5 años
	 */
	BETWEEN_THREE_FIVE_YEARS,
	
	/**
	 * Más de 5 años
	 */
	MORE_FIVE_YEARS;
	
	/**
     * Ruta base del fichero de mensajes.
     */
    private static final String BASE_NAME = "com.code.aon.cvitae.i18n.messages";

    /**
     * Prefijo de la llave de mensajes. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_experience_";
    
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
