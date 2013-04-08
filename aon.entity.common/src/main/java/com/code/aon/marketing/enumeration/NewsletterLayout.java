package com.code.aon.marketing.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum NewsletterLayout implements IResourceable {
	
	/** FULL_WIDTH_IMAGE. */
	FULL_WIDTH_IMAGE,
    
	/** RIGHT_ALIGNED_IMAGE. */
	RIGHT_ALIGNED_IMAGE,
    
    /** LEFT_ALIGNED_IMAGE. */
	LEFT_ALIGNED_IMAGE,
	
    /** ALTERNATE_ALIGNED_IMAGE. */
	ALTERNATE_ALIGNED_IMAGE;

	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.marketing.i18n.messages";

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_newsletter_layout_";
    
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
