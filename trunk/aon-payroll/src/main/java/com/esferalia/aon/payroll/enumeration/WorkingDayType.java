package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum WorkingDayType implements IResourceable, IStringEnum {
	
	WORKING_DAY_ANUAL("A"),
	WORKING_DAY_DAILY("D"),
	WORKING_DAY_MONTHLY("M"),
	WORKING_DAY_WEEKLY("S")
	;
	
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_working_day_type_";

    @Override
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
	private String value;
    
    WorkingDayType( String value ) {
      	this.value = value;
  	}
	    
    @Override
	public String getValue() {
		return value;
	}
    
    public static WorkingDayType enumByValue(String value){
    	for(WorkingDayType type: values()){
    		if(type.getValue().equals(value)){
    			return type;
    		}
    	}
    	return null;
    }
    
}
