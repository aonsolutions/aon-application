package com.code.aon.fiscal.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different types of an Invoice.
 * 
 */
public enum Mod349Status implements IResourceable {
	
	PENDING,
	FINISHED,
	BATCHED, 
	BLOCKED,
	SENT,
	MISSING
	;
	
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_mod349_status_";

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