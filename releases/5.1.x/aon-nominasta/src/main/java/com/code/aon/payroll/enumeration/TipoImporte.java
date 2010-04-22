package com.code.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

/**
 * Enumeración de Desempleados.
 */
public enum TipoImporte implements IResourceable, IStringEnum {

	DIARIO("D"),
	ANUAL("A"),
	MENSUAL("M"),
	N("N"),
	n("n");
	
	/*
	A
	M
	N
	n
	*/
	
	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.payroll.i18n.messages";

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_tipimporte_";
    
    private String value;
    
    TipoImporte( String value ) {
    	this.value = value;
	}
    
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
    
    @Override
	public String getValue() {
		return value;
	}
}