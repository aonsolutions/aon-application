package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different last inactive periods.
 * 
 * @author esferlia Networks S.A. 20-jul-2010
 * @since 1.0
 */
public enum InactiveLastPeriod implements IResourceable {

	LAST_MONTH,

	LAST_QUARTER,
    
	LAST_SEMESTER,
    
	LAST_YEAR,
	
	ALL,
	
	MANUAL
	
	;
    
	/** Message file base path. */
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_inactive_last_period_";

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