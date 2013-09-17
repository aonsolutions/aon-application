package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum TaxationType implements IResourceable {
	
	TAXED,
	NO_TAXED,
	MANUAL
	;
	
    private static final String MSG_KEY_PREFIX = "aon_enum_taxation_type_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
}
