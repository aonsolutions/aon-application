package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum LiquidationType implements IResourceable {
	
	L00("L00"),
	L02("L02"),
	L03("L03"),
	L04("L04"),
	L09("L09"),
	L10("L10"),
	L11("L11"),
	L12("L12"),
	L13("L13"),
	L15("L15"),
	A70("A70"),
	A71("A71"),
	A72("A72"),
	A73("A73"),
	A74("A74"),
	A75("A75"),
	A76("A76"),
	TP2("TP2")
	;
	
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_liquidation_type_";

    @Override
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
	private String value;
    
    LiquidationType( String value ) {
      	this.value = value;
  	}
	    
	public String getValue() {
		return value;
	}
    
}
