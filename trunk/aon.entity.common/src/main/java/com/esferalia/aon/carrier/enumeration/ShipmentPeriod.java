package com.esferalia.aon.carrier.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different shipments periods.
 */
public enum ShipmentPeriod implements IResourceable{
	
	IN_COMMENTS,
	
	MORNING,
	
	NOON,
	
	AFTERNOON,
	
	BEFORE_10,
	
	BEFORE_12,
	
	OFFICE_HOURS,
	
	AFTER_19 
	;
		
    /** Message file base path. */
    private static final String BASE_NAME = "com.esferalia.aon.carrier.i18n.messages";

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_carrier_shipment_period_";
    
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