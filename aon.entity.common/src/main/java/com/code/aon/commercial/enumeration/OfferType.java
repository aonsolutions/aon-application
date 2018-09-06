package com.code.aon.commercial.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different types of offers.
 * 
 * @author Consulting & Development. Joseba Urkiri - 5-sept-2006
 * @since 1.0
 * @version 1.0
 */
public enum OfferType implements IResourceable {

	NORMAL,
    INTERNET,
    PROFORMA,
    DEALERSHIP,
    OTHER;	//OLD     A U D A T E X;
	
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_offer_type_";
    
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