package com.esferalia.aon.pms.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum ReservationCheckStatus implements IResourceable {

	NO_CHECK,
	CHECK_IN,
	CHECK_OUT,
	NO_SHOW,
	NO_SHOW_NO_INVOICEABLE;
    
	/** Message file base path. */
    private static final String BASE_NAME = "com.esferalia.aon.pms.i18n.messages";

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_reservation_check_status_";
    
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