package com.code.aon.ebackoffice.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

/**
 * Enumeración de Afectados en embargos.
 */
public enum OriginalPrice implements IResourceable, IStringEnum {

	DEFAULT("0"),
	YES("1"),
	NO("2");
	


    
	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.ebackoffice.i18n.messages";

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_originalprice_";
    
    private String value;
    
    OriginalPrice( String value ) {
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