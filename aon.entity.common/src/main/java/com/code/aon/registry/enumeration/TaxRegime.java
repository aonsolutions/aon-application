package com.code.aon.registry.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum TaxRegime implements IResourceable {
	
    EDN,				// Estimación Directa Normal
    EDS,				// Estimación Directa Simplificada
    MODULES,			// Módulos
    BUSINESS_SOCIETY; 	// Sociedad Mercantil.

    private static final String BASE_NAME = "com.code.aon.registry.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_tax_regime_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
}