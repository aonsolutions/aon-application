package com.esferalia.aon.payroll.core.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum CausaAlta implements IResourceable {
	
	
	CURACION,
	FALLECIMIENTO,
	INSPECCION_MEDICA,
	INCAPACIDAD,
	AGOTAMIENTO_PLAZO,
	MEJORIA,
	INCOMPARECENCIA,
	CONTROL_INSS,
	RECUPERACION,
	INCOMPARECENCIA_FORMACION;
	
    private static final String BASE_NAME = "com.esferalia.aon.payroll.core.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_causa_alta_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
}
