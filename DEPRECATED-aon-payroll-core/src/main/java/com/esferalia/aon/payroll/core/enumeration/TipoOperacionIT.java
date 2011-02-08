package com.esferalia.aon.payroll.core.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum TipoOperacionIT implements IResourceable {

	BAJA,
	ALTA,
	CONFIRMACION;
	
    private static final String BASE_NAME = "com.esferalia.aon.ui.payroll.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_tipo_operacion_it_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
}
