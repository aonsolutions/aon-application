package com.code.aon.finance.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different status of an Invoice.
 * 
 * @author Consulting & Development. Eugenio Castellano - 21-abr-2005
 * @since 1.0
 */
public enum InvoiceStatus implements IResourceable {

    /** PENDING. */
    PENDING,

    /** SCORED. */
    SCORED;
    
	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.finance.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_invoicestatus_";

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