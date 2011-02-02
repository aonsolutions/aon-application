package com.esferalia.aon.payroll.core.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

public enum Periodicidad {
	
	DIARIO,
	MENSUAL;
	
	private static final String BASE_NAME = "com.esferalia.aon.payroll.core.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_periodicidad_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
	
}
	