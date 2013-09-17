package com.code.aon.company.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different report print option
 * 
 * @author esferlia Networks S.A. 
 * @since 1.0
 */
public enum ReportPrintOption implements IResourceable {

	NO_PRINT,
	
	HEADER,
	
	FOOTER,
	
	LEFT_SIDE
	
	;
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_report_print_option_";

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