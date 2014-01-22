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


	NONE(null, false),
    AEB_19(false, false),
    AEB_19_D(false, false),
    AEB_32(false, false),
    AEB_58(false, false), 
    AEB_58_D(false, false), 
    AEB_34(true, false),
    AEB_34_N(true, true),
    SEPA_19_14_CORE_XML(false, false);

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_financebatchtype_";
    
    private Boolean payment;
    private Boolean payroll;

    private FinanceBatchType(Boolean payment, Boolean payroll) {
    	this.payment = payment;
    	this.payroll = payroll;
    }

    public Boolean isPayment() {
    	return payment;
    }

    public Boolean isPayroll() {
    	return payroll;
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