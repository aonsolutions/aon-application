package com.code.aon.finance.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different types of a FinanceTracking.
 * 
 */
public enum FinanceTrackingType implements IResourceable {
	
	/** BATCHED. */
	BATCHED,
	
	/** UNBATCHED. */
	UNBATCHED,
	
	/** RECORDED. */
	RECORDED,
	
	/** RETURNED. */
	RETURNED,
	
    /** UNRECORDED. */
    UNRECORDED,
    
    /** FRACTIONED. */
    FRACTIONED,

    /** SETTLED. */
    SETTLED;

    /** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.finance.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_finance_tracking_type_";

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