package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum EmploymentProgram implements IResourceable, IStringEnum {
	
	EP01("01"),
	EP02("02"),
	EP03("03"),
	EP04("04"),
	EP05("05"),
	EP06("06"),
	EP07("07"),
	EP08("08"),
	EP09("09"),
	EP10("10"),
	EP12("12"),
	EP13("13"),
	EP14("14"),
	EP15("15"),
	EP16("16")
	;
	
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_employment_program_";

    @Override
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
	private String value;
    
    EmploymentProgram( String value ) {
      	this.value = value;
  	}
	    
    @Override
	public String getValue() {
		return value;
	}
    
}
