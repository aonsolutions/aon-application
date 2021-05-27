package com.code.aon.fiscal.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different types of an Invoice.
 * 
 */
public enum Mod347ReportOrder implements IResourceable {

	INVOICE_TOTAL_AMOUNT,
	INVOICE_REGISTRY_ID,
	INVOICE_REGISTRY_NAME,
	INVOICE_REGISTRY_DOCUMENT;

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_mod347_report_order_";

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