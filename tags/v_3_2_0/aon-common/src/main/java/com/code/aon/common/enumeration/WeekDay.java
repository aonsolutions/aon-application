package com.code.aon.common.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;


/**
 * The Enum WeekDay.
 */
public enum WeekDay implements IResourceable {

	/** MONDAY. */
	MONDAY,
	
	/** TUESDAY. */
	TUESDAY,
	
	/** WEDNESDAY. */
	WEDNESDAY,
	
	/** THURSDAY. */
	THURSDAY,
	
	/** FRIDAY. */
	FRIDAY,
	
	/** SATURDAY. */
	SATURDAY,
    
    /** SUNDAY. */
	SUNDAY;
    
	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.common.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_week_day_";

    /** Message key prefix. */
    private static final String MSG_KEY_SHORT_NAME_PREFIX = "aon_enum_week_day_short_";

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
    
    /**
     * Returns a <code>String</code> with the transalation <code>Locale</code>
     * for the locale.
     * 
     * @param locale Required Locale.
     * 
     * @return String a <code>String</code>.
     */
    public String getShortName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_SHORT_NAME_PREFIX + toString());
    }
}
