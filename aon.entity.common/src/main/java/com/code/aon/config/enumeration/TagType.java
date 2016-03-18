package com.code.aon.config.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enumeración para identificar los diferentes tipos de impuestos.
 * 
 * @author Consulting & Development. Eugenio Castellano - 31-ene-2005
 * @since 1.0
 * @version 1.0
 * 
 */
public enum TagType implements IResourceable {

	/**
	 * RATTACH.
	 */
	RATTACH,

	/**
	 * PRODUCT
	 */
	PRODUCT,

	/**
	 * PRIORITY
	 */
	PRIORITY,

	/**
	 * MARKETPLACE
	 */
	MARKETPLACE,
	
	/**
	 * NOTICE
	 */
	OFFICE_NOTICE,
	
	OFFICE_PRIORITY,
	
	OFFICE_STATUS,
	
	OFFICE_TYPE,

	/**
	 * PACKING
	 */
	PACKING;

	/**
	 * Message key prefix.
	 */
	private static final String MSG_KEY_PREFIX = "aon_enum_tagtype_";

	/**
	 * Returns a <code>String</code> with the transalation <code>Locale</code>
	 * for the locale.
	 * 
	 * @param locale
	 *            Required Locale.
	 * @return String a <code>String</code>.
	 */
	public String getName(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale);
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}
}