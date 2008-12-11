package com.code.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

/**
 * Enumeración de Desempleados.
 */
public enum Claveper implements IResourceable, IStringEnum {

	CLAVE1("A"),
	CLAVE2("B"),
	CLAVE3("C"),
	CLAVE4("D"),
	CLAVE5("E"),
	CLAVE6("F"),
	CLAVE7("G"),
	CLAVE8("H"),
	CLAVE9("I"),
	CLAVE10("J"),
	CLAVE11("K"),
	CLAVE12("L");
    
	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.payroll.i18n.messages";

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_claveper_";
    
    private String value;
    
    Claveper( String value ) {
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