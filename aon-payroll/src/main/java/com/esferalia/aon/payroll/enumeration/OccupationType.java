package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum OccupationType implements IResourceable , IStringEnum{
	
	A("a"),
	B("b"),
	D("d"),
	E("e"),
	F("f"),
	G("g"),
	H("h")
	;

	/** Message file base path. */
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_accupation_type_";

    @Override
	public String getName(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}
    
    private String value;
    
    OccupationType( String value ) {
      	this.value = value;
  	}

	@Override
	public String getValue() {
		return value;
	}
	
	public static OccupationType getOccupationTypeByValue(String value){
    	for( OccupationType c : OccupationType.values() ) {
    		if ( c.getValue().equals(value) ) {
    			return c;
    		}
    	}
    	return null;
    }
	
}
