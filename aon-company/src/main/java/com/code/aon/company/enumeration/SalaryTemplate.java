package com.code.aon.company.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum SalaryTemplate implements IResourceable {

	DEFAULT("salary"),
	
	TEMPLATE1("salaryTemplate1")
	
	;      
	
    private static final String BASE_NAME = "com.code.aon.company.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_salary_template_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
    private String value;
    
    SalaryTemplate( String value ) {
      	this.value = value;
  	}
    
    public String getValue() {
    	return value;
    }
    
    
}
