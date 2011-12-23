package com.code.aon.finance.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different types of FinanceBatch.
 * 
 * @author Consulting & Development. Inigo Gayarre - 05-oct-2005
 * @since 1.0
 */
public enum FinanceBatchType implements IResourceable {


    /** NONE. */
	NONE(null),

    /** AEB_19. */
    AEB_19(false),

    /** AEB_19_D. */
    AEB_19_D(false),

    /** AEB_32. */ 
    AEB_32(false),

    /** AEB_58. */
    AEB_58(false), 
    
    /** AEB_58_D. */
    AEB_58_D(false), 
    
    /** AEB_34. */ 
    AEB_34(true);

    /** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.finance.i18n.messages";

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_financebatchtype_";
    
    private Boolean payment;

    private FinanceBatchType(Boolean payment) {
    	this.payment = payment;
    }

    public Boolean isPayment() {
    	return payment;
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
}