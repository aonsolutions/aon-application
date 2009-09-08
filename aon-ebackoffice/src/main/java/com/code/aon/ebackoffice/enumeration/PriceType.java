package com.code.aon.ebackoffice.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

/**
 * Enumeración de Afectados en embargos.
 */
public enum PriceType implements IResourceable, IStringEnum {

	NO("0"),
	CROSS_OUT("1"),
	PERCENT("2"),
	DISCOUNT("3");
	
	    
	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.ebackoffice.i18n.messages";

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_pricetype_";
    
    private String value;
    
    PriceType( String value ) {
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