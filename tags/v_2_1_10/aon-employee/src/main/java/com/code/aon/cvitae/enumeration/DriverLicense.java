package com.code.aon.cvitae.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;


/**
 * Enumeración para identificar los diferentes permisos de circulación existentes.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 16-nov-2006
 * @version 1.0
 * 
 * @since 1.0
 */
public enum DriverLicense implements IResourceable {

	A,
	B,
	B_E,
	C1,
	C1_E,
	C,
	C_E,
	D1,
	D1_E,
	D_E;

    /**
     * Ruta base del fichero de mensajes.
     */
    private static final String BASE_NAME = "com.code.aon.cvitae.i18n.messages";

    /**
     * Prefijo de la llave de mensajes. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_driver_license_";
    
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