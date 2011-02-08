package com.esferalia.aon.salary.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum PaymentType implements IResourceable {

	
	BASE_SALARY,
	SALARY_SUPPLEMENTS,
	STRUCTURAL_HOURS,
	NON_STRUCTURAL_HOURS,
	SPECIAL_BONUSES,
	SALARY_IN_KIND,
	COMPENSATION_OR_PREPAID_EXPENSES,
	SOCIAL_SECURITY_BENEFITS,
	MOVING_COMPENSATION,
	OTHER_NON_WAGE
	;
	
	/** Message file base path. */
    private static final String BASE_NAME = "com.esferalia.aon.salary.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_payment_type_";

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
