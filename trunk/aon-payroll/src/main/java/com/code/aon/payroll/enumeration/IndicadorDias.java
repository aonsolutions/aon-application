package com.code.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enumeración de Indicador de dias descuento, de Convenios Colectivos.
 */
public enum IndicadorDias implements IResourceable {

	Ind0,
	Ind1,
	Ind2,
	Ind3,
	Ind4;
    
	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.payroll.i18n.messages";

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_colectivos_";
    
    /**
     * Returns a <code>String</code> with the transalation <code>Locale</code>
     * for the locale.
     * 
     * @param locale Required Locale.
     * 
     * @return String a <code>String</code>.
     */
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
}