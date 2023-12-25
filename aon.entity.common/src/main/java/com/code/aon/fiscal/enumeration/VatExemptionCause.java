package com.code.aon.fiscal.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum VatExemptionCause implements IResourceable {

	E1,
    E2,
    E3,
    E4,
    E5,
    E6;

	public Byte value(){
		return (byte) ordinal();
	}
	
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_vat_exemption_cause_";

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