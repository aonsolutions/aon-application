package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum DeductHomeLoan implements IResourceable {
	
	AFTER_01_01_2001,
	BEFORE_01_01_2001
	;
	
    private static final String MSG_KEY_PREFIX = "aon_enum_deduct_home_loan_";

    @Override
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
}
