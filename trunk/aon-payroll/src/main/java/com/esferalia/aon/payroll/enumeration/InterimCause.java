package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum InterimCause implements IResourceable, IStringEnum {
	
	INTERIM_A("A"),
	INTERIM_B("B"),
	INTERIM_C("C"),
	INTERIM_D("D"),
	INTERIM_E("E"),
	INTERIM_F("F"),
	INTERIM_G("G"),
	INTERIM_H("H"),
	INTERIM_I("I"),
	INTERIM_J("J"),
	INTERIM_K("K"),
	INTERIM_L("L"),
	INTERIM_M("M"),
	INTERIM_N("N"),
	INTERIM_O("O"),
	;
	
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_interim_cause_";

    @Override
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
	private String value;
    
    InterimCause( String value ) {
      	this.value = value;
  	}
	    
    @Override
	public String getValue() {
		return value;
	}
    
}
