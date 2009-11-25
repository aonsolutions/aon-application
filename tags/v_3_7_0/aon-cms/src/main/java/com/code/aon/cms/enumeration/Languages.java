package com.code.aon.cms.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum Languages implements IResourceable {

	/**
	 * Spanish
	 */
	SPANISH(new Locale("es")),

	/**
	 * English
	 */
	ENGLISH(Locale.ENGLISH),

	/**
	 * Basque
	 */
	BASQUE(new Locale("eu")),

	/**
	 * French
	 */
	FRENCH(Locale.FRENCH),

	/**
	 * German
	 */
	GERMAN(Locale.GERMAN),

	/**
	 * Italian
	 */
	ITALIAN(Locale.ITALIAN),

	/**
	 * Catalan
	 */
	CATALAN(new Locale("ca")),

	/**
	 * Portugues
	 */
	PORTUGUESE(new Locale("pt"));

	/**
	 * Ruta base del fichero de mensajes.
	 */
	private static final String BASE_NAME = "com.code.aon.cms.i18n.enumeration";
	
    /**
     * Prefijo de la llave de mensajes. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_language_";

	private Locale locale;

	Languages(Locale locale) {
		this.locale = locale;
	}

	public String getName() {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME);
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}
	
	public String getName(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale);
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}

	public static Languages get(String value) {
		for (Languages _enum : values()) {
			if (value != null) {
				if (value.equals(_enum.getLocale().getLanguage())) {
					return _enum;
				}
			} else if (_enum.getLocale().getLanguage() == null) {
				return _enum;
			}
		}
		return null;
	}

	public Locale getLocale() {
		return locale;
	}

	public static Languages[] getValues() {
		return values();
	}
	
}