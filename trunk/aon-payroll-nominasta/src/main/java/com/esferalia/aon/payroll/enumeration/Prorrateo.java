package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum Prorrateo implements IResourceable, IStringEnum {

    PROMENSUAL("M"),
	PRODIARIO("D");
	

	private static final String BASE_NAME = "com.code.aon.payroll.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_prorrateo_";
    private String value;
    
    Prorrateo( String value ) {
    	this.value = value;
	}
    
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }

	@Override
	public String getValue() {
		return value;
	}
    
}