/**
 * 
 */
package com.code.aon.employee.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * @author rtrepiana
 *
 */
public enum SalaryPayment implements IResourceable {

	
	BASE_SALARY,
	SALARY_SUPPLEMENTS,
	OVERTIME,
	SPECIAL_BONUSES,
	SALARY_IN_KIND;
	
	
	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.employee.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_salary_payment_";

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
