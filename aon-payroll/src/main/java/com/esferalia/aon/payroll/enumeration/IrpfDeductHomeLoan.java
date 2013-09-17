package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum IrpfDeductHomeLoan implements IResourceable {
	GENERAL_REGIME,
	TRANSIENT_REGIME;

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_irpf_deduct_home_loan_";

    @Override
	public String getName(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}
	
}
