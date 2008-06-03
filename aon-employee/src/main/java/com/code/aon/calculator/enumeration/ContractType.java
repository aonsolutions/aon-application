package com.code.aon.calculator.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;


/**
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 16-nov-2006
 * @version 1.0
 * 
 * @since 1.0
 */
public enum ContractType implements IResourceable {

	GENERAL_TYPE,
	TEMPORARY_FULL_TIME,
	TEMPORARY_PART_TIME;

    /**
     * Ruta base del fichero de mensajes.
     */
    private static final String BASE_NAME = "com.code.aon.calculator.i18n.messages";

    /**
     * Prefijo de la llave de mensajes. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_contract_type_";
    
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