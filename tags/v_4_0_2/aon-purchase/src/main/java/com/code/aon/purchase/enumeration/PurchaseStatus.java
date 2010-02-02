package com.code.aon.purchase.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;


/**
 * Enumeration to identify the different status of a purchase.
 * 
 * @author Consulting & Development. Joseba Urkiri - 22-may-2006
 * @version 1.0
 * 
 * @since 1.0
 */
public enum PurchaseStatus implements IResourceable {

	/**
     * Pending.
     */
    PENDING,

    /**
     * Blocked.
     */
    BLOCKED,
    
    /**
     * Closed
     */
    CLOSED;
	
	/**
     * Message file base path.
     */
    private static final String BASE_NAME = "com.code.aon.purchase.i18n.messages";

    /**
     * Message key prefix. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_purchase_status_";
    
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