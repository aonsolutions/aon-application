package com.code.aon.person.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different genders.
 * 
 * @author Consulting & Development. Aimar Tellitu - 7-jun-2005
 * @version 1.0
 * @since 1.0
 */
public enum Gender implements IResourceable {

    /** MALE. */
    MALE,

    /** FEMALE. */
    FEMALE,

    /** UNKNOWN. */
    UNKNOWN;

	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.registry.i18n.messages";

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_gender_";

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