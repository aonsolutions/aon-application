package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum SSRegimeType implements IResourceable{
	
	GENERAL,
	AGRICULTURAL,
	DOMESTIC_EMPLOYEES,
	SELF_EMPLOYED,
	COAL_MINING,
	SEA_WORKERS,
	STUDENT_INSURANCE;

	/** Message file base path. */
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_contract_ss_regime_";

	@Override
	public String getName(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale);
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}

}
