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
    SEPA_19_14_CORE_XML(false, false),
    SEPA_34_14_XML(true, false),
    SEPA_34_14_N_XML(true, true),
    SEPA_19_14_COR1_XML(false, false),
    SEPA_58_ANTICIPO_XML(false, false),
    SEPA_58_COBRO_XML(false, false),
    SEPA_34_14_ABONO_XML(true, false);

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
    
    public boolean is19() {
    	return (this == AEB_19) || (this == AEB_19_D) || (this == SEPA_19_14_CORE_XML)
    			|| (this == FinanceBatchType.SEPA_19_14_COR1_XML);
    }

    public boolean is34() {
    	return (this == AEB_34) || (this == FinanceBatchType.SEPA_34_14_XML) ||
    			(this == AEB_34_N) || (this == FinanceBatchType.SEPA_34_14_N_XML) ||
    			 (this == SEPA_34_14_ABONO_XML);
    }
    
    public boolean is58() {
    	return (this == AEB_58) || (this == FinanceBatchType.AEB_58_D) ||
    			(this == SEPA_58_ANTICIPO_XML) || (this == FinanceBatchType.SEPA_58_COBRO_XML);
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