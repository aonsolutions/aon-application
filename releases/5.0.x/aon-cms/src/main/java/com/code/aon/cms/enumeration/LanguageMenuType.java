package com.code.aon.cms.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum LanguageMenuType implements IResourceable {

	/**
	 * Generic Page
	 */
	LIST,

	/**
	 * Generic Page
	 */
	FLAGS,

	/**
	 * Menu Page
	 */
	DROP;


	/**
	 * Ruta base del fichero de mensajes.
	 */
	private static final String BASE_NAME = "com.code.aon.cms.i18n.enumeration";
	
    /**
     * Prefijo de la llave de mensajes. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_language_menu_type_";

    public String getName() {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME);
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}

	public String getName(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale);
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}

}