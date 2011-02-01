package com.code.aon.hyperview.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enumeración para identificar los diferentes tipos de datos.
 * 
 * @author Consulting & Development. ecastellano - 28-feb-2006
 * @version 1.0
 * 
 * @since 1.0
 */
public enum LayoutType implements IResourceable {

	/**
	 * Vista en forma de formulario.
	 */
	ROW,

	/**
	 * Vista en forma de lista.
	 */
	COLUMN;

	/**
	 * Ruta base del fichero de mensajes.
	 */
	private static final String BASE_NAME = "com.code.aon.console.workstation.i18n.enum";

	/**
	 * Prefijo de la llave de mensajes.
	 */
	private static final String MSG_KEY_PREFIX = "aon_enum_viewtype_";

	/**
	 * Devuelve un <code>String</code> con la traducción correspondiente al
	 * <code>Locale</code> pasado por parámetro.
	 * 
	 * @return String un <code>String</code>.
	 */
	public String getName(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale);
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}

}