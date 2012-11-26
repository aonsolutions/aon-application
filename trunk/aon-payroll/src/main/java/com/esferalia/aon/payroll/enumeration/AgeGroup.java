package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum AgeGroup implements IResourceable, IStringEnum {
	
	AG01("01"),
	AG02("02"),
	AG03("03"),
	AG04("04"),
	AG05("05"),
	AG06("06"),
	AG07("07"),
	AG08("08"),
	AG09("09"),
	AG10("10"),
	AG11("11"),
	AG12("12"),
	AG13("13"),
	AG14("14"),
	AG15("15"),
	AG16("16"),
	AG17("17")
	;
	
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_age_group_";

    @Override
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
	private String value;
    
    AgeGroup( String value ) {
      	this.value = value;
  	}
	    
    @Override
	public String getValue() {
		return value;
	}
    
}
