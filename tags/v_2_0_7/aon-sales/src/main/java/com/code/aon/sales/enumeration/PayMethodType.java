package com.code.aon.sales.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different types of pay methods.
 * 
 * @author jurkiri
 */
public enum PayMethodType implements IResourceable {

    /** UNKNOWN. */
    UNKNOWN,

    /** CASH BASIS. */
    CASH_BASIS,

    /** NEGOTIABLE DOCUMENT. */
    NEGOTIABLE_DOCUMENT,

    /** DEBIT CARD. */
    DEBIT_CARD,

    /** CREDIT CARD. */
    CREDIT_CARD,

    /** CHEQUE. */
    CHEQUE;
    
    /** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.i18n.messages";

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_paymethodtype_";

   
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