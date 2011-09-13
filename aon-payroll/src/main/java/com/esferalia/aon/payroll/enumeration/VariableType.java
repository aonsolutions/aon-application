package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum VariableType implements IResourceable {
	
	// PRIMITIVE
	INTEGER, 
	DOUBLE, 
	STRING, 
	BOOLEAN,
	DATE,
	
	// DROPS & LISTS
	DROP,
	CNO_DROP,
	TC2_DROP, 
	CATEGORY_DROP, 
	QUOTE_GROUP_DROP,
	
	// COMPLEX
	EXPRESSION,
	TABLE,
	
	// OTHER
	UNKNOWN 
	;
	
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_variableType_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    public String getName() {
    	return name();
    }
    
}
