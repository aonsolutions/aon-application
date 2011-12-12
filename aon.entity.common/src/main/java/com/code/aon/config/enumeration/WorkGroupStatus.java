package com.code.aon.config.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * The Enum WorkGroupStatus.
 */
public enum WorkGroupStatus implements IResourceable {
	
	/** ACTIVE. */
	ACTIVE,
	
	/** INACTIVE. */
	INACTIVE;

	/** Ruta base del fichero de mensajes. */
    private static final String BASE_NAME = "com.code.aon.config.i18n.messages";

    /** Prefijo de la clave de mensajes. */
    private static final String MSG_KEY_PREFIX = "aon_enum_workGroup_status_";

	/**
	 * Gets the name.
	 * 
	 * @param locale the locale
	 * 
	 * @return the name
	 */
	public String getName(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}
}
