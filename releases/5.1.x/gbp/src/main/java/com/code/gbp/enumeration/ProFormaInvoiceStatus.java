package com.code.gbp.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum ProFormaInvoiceStatus implements IResourceable {

	/** PENDING */
	PENDING,
	
	/** ACCEPTED */
	ACCEPTED,
	
	/** INCIDENCES */
	INCIDENCES,
	
	/** DISCARDED */
	DISCARDED;
	
	/** Message file base path. */
    private static final String BASE_NAME = "com.code.gbp.i18n.messages";

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "gbp_pro_forma_invoice_status_";
	
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