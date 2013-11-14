package com.code.aon.common.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;


/**
 * The Enum CSS Units.
 */
public enum CSSUnit implements IResourceable {

	/** PIXEL. */
	PX("px"),
	
	/** PERCENTAGE. */
	PERCENTAGE("%"),
	
	/** CENTIMETER. */
	CM("cm"),
	
	/** MILIMETER. */
	MM("mm"),
	
	/** INCH. */
	IN("in"),

	/** FONT SIZE. */
	EM("em"),

	/** HEIGHT OF THE FONT. */
	EX("ex"),
	
	/** POINT. */
	PT("pt"),
    
    /** PICA. */
	PC("pc");
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_css_unit_";

    /**
     * Value
     */
	private String value;
    
    private CSSUnit(String value) {
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

	public String getValue() {
		return value;
	}
    
}
