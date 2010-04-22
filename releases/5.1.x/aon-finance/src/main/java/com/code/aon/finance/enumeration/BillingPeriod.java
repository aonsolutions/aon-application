package com.code.aon.finance.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different billing periods.
 * 
 * @author Consulting & Development. Eugenio Castellano - 31-ene-2005
 * @since 1.0
 * @version 1.0
 */
public enum BillingPeriod implements IResourceable {

	 /** NO PERIOD. */
    NO_PERIOD(0),

	/** MONTHLY. */
	MONTHLY(1),

	/** Bi MONTHLY. */
	BI_MONTHLY(2),

    /** THREE MONTHLY. */
    THREE_MONTHLY(3),

    /** FOUR MONTHLY. */
    FOUR_MONTHLY(4),

    /** SIX MONTHLY. */
    SIX_MONTHLY(6),

    /** YEARLY. */
    YEARLY(12);

    /** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.finance.i18n.messages";

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_billing_period_";

    private int value;
    
    BillingPeriod(int value){
    	this.value = value;
    }

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
    
    public int getValue(){
    	return this.value;
    }
}