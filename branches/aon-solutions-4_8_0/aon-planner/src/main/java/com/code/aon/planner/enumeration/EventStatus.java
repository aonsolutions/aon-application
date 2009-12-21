package com.code.aon.planner.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enumeration of event status.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 21-nov-2006
 * @version 1.0
 * @since 1.0
 * 
 */
public enum EventStatus implements IResourceable {

    TENTATIVE,
    CONFIRMED,
    CANCELLED,
    SPREAD,
    LOCKED;

    /**
     * Messages file base path.
     */
    private static final String BASE_NAME = "com.code.aon.planner.i18n.messages";

    /**
     * Messages key prefix. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_event_status_";

	/**
	 * Return the <code>EventStatus</code>
	 * 
	 * @param value
	 * @return
	 */
    public static EventStatus get(String value) {
    	for( EventStatus _enum : values() ) {
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