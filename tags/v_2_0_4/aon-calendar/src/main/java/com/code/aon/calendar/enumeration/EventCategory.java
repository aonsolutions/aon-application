package com.code.aon.calendar.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enumeration of event category.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 21-nov-2006
 * @version 1.0
 * @since 1.0
 * 
 */
public enum EventCategory implements IResourceable {

	VACATION,
	PUBLIC_HOLIDAY,
	DAY_OFF,
	WORK,
	APPOINTMENT,
	INCIDENCE;

	/**
	 * Messages file base path.
	 */
	private static final String BASE_NAME = "com.code.aon.calendar.i18n.messages";

	/**
	 * Messages key prefix. 
	 */
	private static final String MSG_KEY_PREFIX = "aon_enum_eventcategory_";

	/**
	 * Return the <code>EventCategory</code>
	 * 
	 * @param value
	 * @return
	 */
    public static EventCategory get(String value) {
    	for( EventCategory _enum : values() ) {
			if ( value.equals(_enum.name()) ) {
				return _enum;
			}
    	}
		return null;
    }	

    /*
     * (non-Javadoc)
     * @see com.code.aon.common.enumeration.IResourceable#getName(java.util.Locale)
     */
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
   
}