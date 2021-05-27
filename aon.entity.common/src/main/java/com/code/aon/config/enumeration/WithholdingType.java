package com.code.aon.config.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different types of an Invoice.
 * 
 */
public enum WithholdingType implements IResourceable {

	/** PROFESIONALES */
	PROFESSIONAL,
	
	/** ARRENDAMIENTO */
	RENTING,
	
	/** CAPITAL MOBILIARIO */
	MOVABLE_CAPITAL,
	
	/** AGRICULTOR */
	FARMER,
	
	/** TRANSPORTISTAS Y ASIMILADOS */
	TRANSPORT_OPERATOR;

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_withholding_type_";

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