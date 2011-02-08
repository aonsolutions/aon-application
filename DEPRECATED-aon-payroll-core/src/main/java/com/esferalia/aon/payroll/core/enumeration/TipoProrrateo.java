package com.esferalia.aon.payroll.core.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum TipoProrrateo implements IResourceable, IStringEnum {
	
	PROMENSUAL("M"),
	PRODIARIO("D");
	
	private static final String BASE_NAME = "com.esferalia.aon.payroll.core.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_tipo_prorrateo_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
    private String value;
    
    TipoProrrateo( String value ) {
      	this.value = value;
  	}
    
    @Override
	public String getValue() {
		return value;
	}
	
}
	