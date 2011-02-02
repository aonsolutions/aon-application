package com.esferalia.aon.calendar.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different sources of a Calendar.
 * 
 * @author Esferalia. Ekain Agirrezabal 
 * @since 1.0
 * @version 1.0
 */
public enum CalendarSource implements IResourceable {

	/** ENTERPRISE. Calendario de empresa */
	ENTERPRISE,
	
	/** WORKPLACE. Calendario de centro de trabajo */
	WORKPLACE,
	
	/** CONTRACT. Calendario de contrato */
	CONTRACT;

	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.calendar.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_calendar_source_";

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