package com.esferalia.aon.payroll.core.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum TipoContingencia implements IResourceable {
	
	// Enfermedad comun
	ENFERMEDAD_COMUN,
	// Accidente laboral
	ACCIDENTE_LABORAL,
	// Accidente no laboral
	ACCIDENTE_NO_LABORAL,
	// Maternidad
	MATERNIDAD,
	// Riesgo Embarazo
	EMBARAZO,
	// Paternidad
	PATERNIDAD,
	// Riesgo Lactancia	
	LACTANCIA;
	
	public boolean isAccident() {
		return (this == ACCIDENTE_LABORAL || this == ACCIDENTE_NO_LABORAL);
	}
	
    private static final String BASE_NAME = "com.esferalia.aon.payroll.core.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_tipo_contingencia_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
}
