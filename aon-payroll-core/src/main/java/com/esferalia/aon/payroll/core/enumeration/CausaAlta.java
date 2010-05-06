package com.esferalia.aon.payroll.core.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum CausaAlta implements IResourceable, IStringEnum  {
	
	
	CURACION("01"),
	FALLECIMIENTO("02"),
	INSPECCION_MEDICA("03"),
	INCAPACIDAD("04"),
	AGOTAMIENTO_PLAZO("05"),
	MEJORIA("06"),
	INCOMPARECENCIA("07"),
	CONTROL_INSS("10"),
	RECUPERACION("17"),
	INCOMPARECENCIA_FORMACION("18");
	
    private static final String BASE_NAME = "com.esferalia.aon.payroll.core.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_causa_alta_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
    private String value;
    
    CausaAlta( String value ) {
      	this.value = value;
  	}
    
    @Override
	public String getValue() {
		return value;
	}
}
