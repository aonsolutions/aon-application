package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum AgeCollective implements IResourceable, IStringEnum {
	
	COLLECTIVE_1("01"),
	COLLECTIVE_2("02"),
	COLLECTIVE_3("03"),
	COLLECTIVE_4("04"),
	COLLECTIVE_5("05"),
	COLLECTIVE_6("06"),
	COLLECTIVE_7("07")	
	;
	
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_age_collective_";

    @Override
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
	private String value;
    
    AgeCollective( String value ) {
      	this.value = value;
  	}
	    
    @Override
	public String getValue() {
		return value;
	}
    
}
