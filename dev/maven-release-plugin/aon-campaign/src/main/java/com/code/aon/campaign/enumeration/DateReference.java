package com.code.aon.campaign.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum DateReference implements IResourceable {

	/** FROM START DATE */
	FROM_START_DATE,
	
	/** BEFORE END DATE */
	BEFORE_END_DATE,

    /** FROM_CREATION_DATE */
	FROM_CREATION_DATE;
	
	/** Message file base path. */
	private static final String BASE_NAME = "com.code.aon.campaign.i18n.messages";

	/** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_date_reference_";
	
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
