package com.code.aon.registry.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different media types.
 * 
 * @author Consulting & Development. Eugenio Castellano - 28-ene-2005
 * @version 1.0
 * @since 1.0
 */
public enum MediaType implements IResourceable {

    /** UNKNOWN. */
    UNKNOWN,

    /** FIXED PHONE. */
    FIXED_PHONE,

    /** CELLULAR. */
    CELLULAR,

    /** The FAX. */
    FAX,

    /** The EMAIL. */
    EMAIL,

    /** The WEB. */
    WEB;

    /** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.registry.i18n.messages";

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_mediatype_";

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