package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum Mutual implements IResourceable, IStringEnum {
	
	M001("001"),
	M002("002"),
	M003("003"),
	M007("007"),
	M010("010"),
	M011("011"),
	M015("015"),
	M021("021"),
	M039("039"),
	M061("061"),
	M072("072"),
	M115("115"),
	M151("151"),
	M183("183"),
	M201("201"),
	M267("267"),
	M272("272"),
	M274("274"),
	M275("275"),
	M276("276"),
	M666("666"),
	M777("777"),
	M888("888")
	;
	
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_mutual_";

    @Override
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
	private String value;
    
    Mutual( String value ) {
      	this.value = value;
  	}
	    
    @Override
	public String getValue() {
		return value;
	}
    
}
