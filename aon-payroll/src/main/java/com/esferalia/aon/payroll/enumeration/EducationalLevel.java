package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum EducationalLevel implements IResourceable, IStringEnum {
	
	EL11("11"),
	EL22("22"),
	EL23("23"),
	EL32("32"),
	EL33("33"),
	EL51("51"),
	EL54("54"),
	EL55("55"),
	EL59("59"),
	EL60("60"),
	EL61("61"),
	EL80("80")
	;
	
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_educational_level_";

	@Override
	public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}

	private String value;
	    
    EducationalLevel( String value ) {
      	this.value = value;
  	}
	    
    @Override
	public String getValue() {
		return value;
	}
    
//    public EducationalLevel getValue(String value) {
//    	return EducationalLevel.valueOf("EL"+value);
////    	return null;
//    }
}
