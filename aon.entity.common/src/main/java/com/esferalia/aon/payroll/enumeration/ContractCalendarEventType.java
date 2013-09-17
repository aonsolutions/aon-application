package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different contract durations.
 * 
 * @author esferlia Networks S.A. Aimar Tellitu - 19-jul-2010
 * @since 1.0
 */
public enum ContractCalendarEventType implements IResourceable {

	RECOVERABLE_HOURS,
	NON_RECOVERABLE_HOURS,
	HOLIDAYS,
	WORKED_HOURS,
	OVERTIME,
	STRIKE,
	ERE;
	    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_contract_calendar_event_";

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