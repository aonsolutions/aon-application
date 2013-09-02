package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum ContrataFileType implements IResourceable {

	/** Contrato*/
	CONTRACT,
	
	/** Prórroga */
	EXTENSION,
	
	/** Transformación a indefinido */
	TRANSFORMATION,
	
	/** Llamamiento de fijo discontinuo */
	INDEFINITE_CALL,
	
	/** Copia Básica */
	BASIC_COPY,
	
	/** Contrato de grupo */
	GROUP_CONTRACT,
	
	/** Horas Complementarias */
	ADDITIONAL_HOURS,
	
	/** Incluir contrato de Oficina de Empleo */
	OFFICE_CONTRACT,
	
	/** Anexo de Formación */
	LEARNING_ANNEX,
	
	/** Corrección de contrato */
	CORRECTION_CONTRACT,
	
	/** Corrección de prórroga */
	CORRECTION_EXTENSION,
	
	/** Corrección de transformaciones */
	CORRECTION_TRANSFORMATION,
	
	/** Corrección de llamamientos */
	CORRECTION_INDEFINITE_CALL,

	/** Corrección de horas complementarias */
	CORRECTION_ADDITIONAL_HOURS,
	
	;
	
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_contrata_file_type_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
}
