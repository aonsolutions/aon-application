package com.code.aon.ui.accounting.check;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum CheckCategory implements IResourceable {
	
	INVOICE,
	FINANCE,
	MASTERS,
	ACCOUNT,
	ACCOUNTING,
	AMORTIZATION;
	
    private static final String MSG_KEY_PREFIX = "aon_enum_check_category_";
	
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
}