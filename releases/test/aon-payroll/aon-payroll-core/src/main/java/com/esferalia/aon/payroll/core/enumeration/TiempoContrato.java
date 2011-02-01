package com.esferalia.aon.payroll.core.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum TiempoContrato implements IResourceable, IStringEnum {
	
	COMPLETO("0"),
	PARCIAL_HORAS("1"),	
	PARCIAL_DIAS("2");
	
	private static final String BASE_NAME = "com.esferalia.aon.payroll.core.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_tiempo_contrato_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
    private String value;
    
    TiempoContrato( String value ) {
      	this.value = value;
  	}
    
    @Override
	public String getValue() {
		return value;
	}
	
}
	