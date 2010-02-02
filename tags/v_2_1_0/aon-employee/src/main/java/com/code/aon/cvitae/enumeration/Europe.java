package com.code.aon.cvitae.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;


/**
 * Enumeración para identificar los paises europeos.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 16-nov-2006
 * @version 1.0
 * 
 * @since 1.0
 */
public enum Europe implements IResourceable {

	EUR,
	DEU,
	AND,
	AUT,
	BEL,
	BUL,
	CYP,
	DEN,
	SLO,
	SPA,
	EST,
	FIN,
	FRA,
	GRC,
	NLD,
	HUN,
	ISL,
	IRL,
	ITA,
	LET,
	LIE,
	LTU,
	LUX,
	MLT,
	NOR,
	POL,
	PRT,
	GBR,
	SVK,
	CZE,
	ROM,
	RUS,
	SWE,
	CHE,
	SAM;

    /**
     * Ruta base del fichero de mensajes.
     */
    private static final String BASE_NAME = "com.code.aon.cvitae.i18n.messages";

    /**
     * Prefijo de la llave de mensajes. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_country_";
    
    /**
     * Devuelve un <code>String</code> con la traducción correspondiente al <code>Locale</code>
     * pasado por parámetro.
     * 
     * @return String un <code>String</code>.
     */
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
   
}