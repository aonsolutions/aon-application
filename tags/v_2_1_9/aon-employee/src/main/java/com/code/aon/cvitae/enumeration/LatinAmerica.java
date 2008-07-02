package com.code.aon.cvitae.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;


/**
 * Enumeración para identificar los paises latino-americanos.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 16-nov-2006
 * @version 1.0
 * 
 * @since 1.0
 */
public enum LatinAmerica implements IResourceable {

	LOA,
	ARG,
	BOL,
	CHL,
	COL,
	CRI,
	CUB,
	ECU,
	SLV,
	GTM,
	MEX,
	NIC,
	PAN,
	PER,
	PRI,
	DOM,
	URY,
	VEN;

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