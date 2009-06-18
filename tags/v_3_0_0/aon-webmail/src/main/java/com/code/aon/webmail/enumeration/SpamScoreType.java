package com.code.aon.webmail.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.MimeType;

public enum SpamScoreType implements IResourceable {

    /**
     * VERY_LOW
     */
	VERY_LOW (2.0),

	/**
     * LOW 
     */
	LOW (3.0),
    
	/**
     * NORMAL 
     */
	NORMAL (4.0),

	/**
     * HIGH 
     */
	HIGH (6.0),
	
	/**
     * VERY_HIGH 
     */
	VERY_HIGH (10.0);
    
    
	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.webmail.i18n.messages";

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_spamscore_type_";
    
    private double value;
    
    SpamScoreType(double value) {
		this.value = value;
	}
    
	public double getValue() {
		return value;
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
    
	public static SpamScoreType get(double value) {
    	for( SpamScoreType score : SpamScoreType.values() ) {
    		if ( score.getValue() == value ) {
    			return score;
    		}
    	}
    	return null;
	}
    
}
