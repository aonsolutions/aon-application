package com.code.aon.accounting.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * The Enum AccountEntryType.
 */
public enum AccountEntryType implements IResourceable {
	
	OPENING,
	CLOSING,
	OPERATING,
	MANUAL,
	SALES_INVOICE,
	PURCHASE_INVOICE,
	EXPENSE_INVOICE,
	INVESTMENT_INVOICE,
	EXPENSES,
	SALARY,
	TAX,
	LOAN,
	LEASING,
	PAYMENT,
	COLLECTION,
	STOCK_VARIATION,
	AMORTIZATION,
	SOCIAL_INSURANCE,
	LOAN_FEE,
	LEASING_FEE,
	RETURNED_PAYMENT,
	RETURNED_COLLECTION,
	SOCIAL_INSURANCE_ADJUST;
	
	
	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.accounting.i18n.messages";

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_account_type_";
	
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