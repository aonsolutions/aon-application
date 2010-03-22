package com.code.aon.purchase.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different status of a PurchaseDetail.
 * 
 */
public enum PurchaseDetailStatus implements IResourceable {

	/** PENDING. */
	PENDING,
    
	/** PARTIAL_SETTLED. */
	PARTIAL_SETTLED,

	/** SETTLED. */
	SETTLED;
    
	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.purchase.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_purchase_detail_status_";

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