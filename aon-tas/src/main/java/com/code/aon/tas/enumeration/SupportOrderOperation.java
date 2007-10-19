package com.code.aon.tas.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enumeration to identify diferent support order operations
 * 
 * @author Consulting & Development. Gorka Irazu - 03-jul-2007
 * @since 1.0
 * @version 1.0
 *  
 */
public enum SupportOrderOperation implements IResourceable {
	
	/**
	 * Direct Repair
	 */
	DIRECT_REPAIR,
	
    /**
     * To Offer.
     */
    TO_OFFER;

    /**
     * Message file base path.
     */
    private static final String BASE_NAME = "com.code.aon.tas.i18n.messages";

    /**
     * Message key prefix. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_supportorderoperation_";


    /**
     * Returns a <code>String</code> with the transalation <code>Locale</code>
     * for the locale.
     * 
     * @param locale
     *            Required Locale.
     * @return String a <code>String</code>.
     */
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
}