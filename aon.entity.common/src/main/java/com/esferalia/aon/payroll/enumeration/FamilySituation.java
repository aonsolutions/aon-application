package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum FamilySituation implements IResourceable {
	
	NO_MARRIED_WITH_SONS(1),
	MARRIED(2),
	OTHER(3)
	;
	
	private static final String MSG_KEY_PREFIX = "aon_enum_family_situation_";

	private int value;
	
	private FamilySituation(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
	
    
}
