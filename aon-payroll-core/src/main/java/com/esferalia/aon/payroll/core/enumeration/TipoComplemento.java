package com.esferalia.aon.payroll.core.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum TipoComplemento implements IResourceable, IStringEnum  {
	
	SALARIO("S"),
	PERSONALES("P"),
	PUESTO("T"),
	CANTIDAD("C"),
	DEVENGO("D"),
	INDEMNIZACIONES("I"),
	PRESTACIONES("G"),
	OTROS("O");
	
	private static final String BASE_NAME = "com.esferalia.aon.payroll.core.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_complemento_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
    private String value;
    
    TipoComplemento( String value ) {
      	this.value = value;
  	}
    
    @Override
	public String getValue() {
		return value;
	}
	
}
	