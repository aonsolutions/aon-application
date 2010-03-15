package com.code.aon.cvitae.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum LanguageEnum implements IResourceable {

	SPANISH, 
	CATALAN,
	ENGLISH,
	GERMAN,
	ITALIAN,
	PORTUGUESE,
	FRENCH,
	ARABIC,
	CZECH,
	CHINESE,
	CROAT,
	DANISH,
	SLOVAK,
	SLOVENIAN,
	BASQUE,
	FINNISH,
	GALICIAN,
	GREEK,
	HUNGARIAN,
	JAPANESE,
	DUTCH,
	NORWEGIAN,
	POLISH,
	ROMANIAN,
	RUSSIAN,
	SERBIAN,
	SWEDISH,
	TURKISH;
	
	/**
     * Ruta base del fichero de mensajes.
     */
    private static final String BASE_NAME = "com.code.aon.cvitae.i18n.messages";

    /**
     * Prefijo de la llave de mensajes. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_language_";
    
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
