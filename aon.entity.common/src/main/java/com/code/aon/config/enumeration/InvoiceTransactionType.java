package com.code.aon.config.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different types of an Invoice.
 * 
 */
public enum InvoiceTransactionType implements IResourceable {

	/** NATIONAL. */
	NATIONAL,
	
	/** INTRACOMMUNITY. */
	INTRACOMMUNITY,
	
	/** EXTRACOMMUNITY. */
	EXTRACOMMUNITY,
	
	/** CANARIAS, CEUTA Y MELILLA. */
	CAN_CEU_MEL,

	/** OTHER_ISP. */
	OTHER_ISP;
	
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_invoice_transaction_type_";

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