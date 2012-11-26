package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum CollectiveReductionCode implements IResourceable, IStringEnum {
	
	REDUCTION_1("1"),
	REDUCTION_2("2")
	;
	
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_collective_reduction_code_";

    @Override
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
	private String value;
    
    CollectiveReductionCode( String value ) {
      	this.value = value;
  	}
	    
    @Override
	public String getValue() {
		return value;
	}
    
}
