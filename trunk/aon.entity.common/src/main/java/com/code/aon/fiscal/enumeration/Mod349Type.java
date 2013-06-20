package com.code.aon.fiscal.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

/**
 * Enummeration to identify the different types of an Invoice.
 * 
 */
public enum Mod349Type implements IResourceable, IStringEnum  {

	E("E"),
	M("M"),
	H("H"),
	A("A"),
	T("T"),
	S("S"),
	I("I");

	private String value;
	
	private Mod349Type(String value) {
		this.value = value;	
	}
	
	public String getValue() {
		return value;
	}
	
	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.fiscal.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_mod349_type_";

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