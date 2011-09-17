package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum IrpfRegularizationReason implements IResourceable {

	REASON1,
	REASON2,
	REASON3,
	REASON4,
	REASON5,
	REASON6,
	REASON7,
	REASON8,
	REASON9,
	REASON10,
	REASON11
	;
	
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_irpf_regularization_reason_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
}
