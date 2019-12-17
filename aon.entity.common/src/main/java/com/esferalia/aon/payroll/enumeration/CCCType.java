package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different CCC types.
 * 
 * @author esferlia Networks S.A. 20-jul-2010
 * @since 1.0
 */
public enum CCCType implements IResourceable {

	PRINCIPAL,

	TRAINING,
    
	LEARNING,
    
	TRADE_REPRESENTATIVE,
	
	ASSIMILATEDS,
	
	FELLOWS,
	
	HOME_EMPLOYEES,
	
	AGRICULTURAL,
	
	ARTIST;
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_ccc_type_";

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