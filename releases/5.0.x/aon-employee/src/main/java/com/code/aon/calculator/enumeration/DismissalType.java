package com.code.aon.calculator.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum DismissalType implements IResourceable {

	UNFAIR,
	LAYOFF_AS_UNFAIR,
    LAYOFF;

    /**
     * Ruta base del fichero de mensajes.
     */
    private static final String BASE_NAME = "com.code.aon.calculator.i18n.messages";

    /**
     * Prefijo de la llave de mensajes. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_dismissal_type_";
    
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