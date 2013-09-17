package com.esferalia.aon.calendar.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;


public enum DayType implements IResourceable {

	WORKING_DAY,
	NOT_WORKING_DAY,
	HOLIDAY,
	VACATION,
	CONTINUOUS_TIME,
	OTHER
	;
	
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_daytype_";
    private static final String MSG_KEY_PREFIX_FULL = "aon_enum_daytype_full_";

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
    public String getFullName(Locale locale) {
    	ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
    	return bundle.getString(MSG_KEY_PREFIX_FULL + toString());
    }
}

	