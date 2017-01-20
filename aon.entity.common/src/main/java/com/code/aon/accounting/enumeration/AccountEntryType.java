package com.code.aon.accounting.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

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
	@Deprecated
	LEASING,
	PAYMENT,
	COLLECTION,
	STOCK_VARIATION,
	AMORTIZATION,
	SOCIAL_INSURANCE,
	LOAN_FEE,
	@Deprecated
	LEASING_FEE,
	RETURNED_PAYMENT,
	RETURNED_COLLECTION,
	SOCIAL_INSURANCE_ADJUST,
	FINANCE;
	
    private static final String MSG_KEY_PREFIX = "aon_enum_account_type_";

    @Override
	public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
}