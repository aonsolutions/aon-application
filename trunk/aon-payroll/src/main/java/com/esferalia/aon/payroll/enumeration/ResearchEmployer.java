package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum ResearchEmployer implements IResourceable, IStringEnum {
	
	EMPLOYER_1("1"),
	EMPLOYER_2("2"),
	EMPLOYER_3("3")
	;
	
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_research_employer_";

    @Override
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
	private String value;
    
    ResearchEmployer( String value ) {
      	this.value = value;
  	}
	    
    @Override
	public String getValue() {
		return value;
	}
    
    public static ResearchEmployer enumByValue(String value){
    	for(ResearchEmployer type: values()){
    		if(type.getValue().equals(value)){
    			return type;
    		}
    	}
    	return null;
    }
    
}
