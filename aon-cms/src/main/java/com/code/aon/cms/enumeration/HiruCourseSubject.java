package com.code.aon.cms.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum HiruCourseSubject implements IResourceable {

	OTHER,

	ACTIVIDAD_AGRARIA,
	
	ACTIVIDAD_FISICA_DEPORTIVA,

	ACTIVIDAD_MARITIMO_PESQUERA,

	ADMINISTRACION,

	ARTES_GRAFICA_DISEÑO,
	
	COMERCIO_MARKETING,
	
	COMUNICACION_IMAGEN_SONIDO,
	
	DANZA,
	
	EDIFICACION_OBRA_CIVIL,
	
	ELECTRICIDAD_ELECTRONICA,
	
	FABRICACION_MECANICA,
	
	FORMACION_GENERAL,
	
	HOSTELERIA_TURISMO,
	
	IDIOMAS,
	
	IMAGEN_PERSONAL,
	
	INDUSTRIAS_ALIMENTARIAS,
	
	INFORMATICA,
	
	INTERNET,
	
	MADERA_MUEBLE,
	
	MANTENIMIENTO_VEHICULOS,
	
	AUTOPROPULSADOS,
	
	MANTENIMIENTOS_SERVICIOS_A_LA_PRODUCCION,
	
	MUSICA,
	
	OCIO,
	
	QUIMICA,
	
	SANIDAD,
	
	SERVICIOS_COMUNIDAD,
	
	SERVICIOS_SOCIOCULTURALES,
	
	TEXTIL_CONFECCION_PIEL;
	
	/**
	 * Ruta base del fichero de mensajes.
	 */
	private static final String BASE_NAME = "com.code.aon.cms.i18n.enumeration";
	
    /**
     * Prefijo de la llave de mensajes. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_hiru_course_subject_";

    public String getName() {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME);
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}

	public String getName(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale);
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}

}