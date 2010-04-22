package com.code.aon.infoweb.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * The Enum WebInfoPageLayoutType.
 */
public enum WebInfoPageType implements IResourceable {
	
	/** GENERIC. */
	GENERIC,
	
	/** CONTACT. */
	CONTACT,
	
	/** LOCATION. */
	LOCATION,
	
	/** GALLERY. */
	GALLERY;
	
	/** Message file base path. */
	private static final String BASE_NAME = "com.code.aon.infoweb.i18n.messages";

	/** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_infoweb_page_type_";
	
	/**
	 * Returns a <code>String</code> with the transalation <code>Locale</code>
	 * for the locale.
	 * 
	 * @param locale Required Locale.
	 * 
	 * @return String a <code>String</code>.
	 */
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
}