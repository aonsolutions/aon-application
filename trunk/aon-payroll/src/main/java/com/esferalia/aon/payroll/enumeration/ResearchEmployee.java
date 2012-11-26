package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum ResearchEmployee implements IResourceable, IStringEnum {
	
	EMPLOYEE_1("1"),
	EMPLOYEE_2("2")
	;
	
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_research_employee_";

    @Override
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
	private String value;
    
    ResearchEmployee( String value ) {
      	this.value = value;
  	}
	    
    @Override
	public String getValue() {
		return value;
	}
    
    public static ResearchEmployee enumByValue(String value){
    	for(ResearchEmployee type: values()){
    		if(type.getValue().equals(value)){
    			return type;
    		}
    	}
    	return null;
    }
    
}
