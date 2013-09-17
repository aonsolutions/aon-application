package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum FileStatus implements IResourceable {
	
	PENDING,
	GENERATED,
	PROCESSED,
	PARTIALLY,
	DENIED;
	
    private static final String MSG_KEY_PREFIX = "aon_enum_file_status_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
}
