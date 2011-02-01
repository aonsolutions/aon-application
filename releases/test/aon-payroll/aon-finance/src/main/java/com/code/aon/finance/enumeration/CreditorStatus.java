package com.code.aon.finance.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enumeration to identify the different status of a creditor.
 * 
 * @author jurkiri
 *  
 */
public enum CreditorStatus implements IResourceable {

    /**
     * Active 
     */
	ACTIVE,
    
    /**
     * Inactive
     */
	INACTIVE,
    
	/**
	 * Blocked
	 */
    BLOCKED;
    
    /**
     * Message file base path.
     */
    private static final String BASE_NAME = "com.code.aon.finance.i18n.messages";

    /**
     * Message key prefix. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_creditor_status_";
    
    /**
     * Returns a <code>String</code> with the translation <code>Locale</code>
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