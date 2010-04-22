package com.code.aon.marketing.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different types of Operators.
 * 
 * @author Consulting & Development. Aimar Tellitu - 6-oct-2008
 * @since 1.0
 * @version 1.0
 */
public enum Operator implements IResourceable {

	/** EQUAL. */
	EQUAL,
    
	/** NOT_EQUAL. */
	NOT_EQUAL,
    
    /** GREATER_THAN. */
	GREATER_THAN,
	
    /** GREATER_THAN_OR_EQUAL. */
	GREATER_THAN_OR_EQUAL,

    /** LESS_THAN. */
	LESS_THAN,
	
    /** LESS_THAN_OR_EQUAL. */
	LESS_THAN_OR_EQUAL,
	
    /** LIKE. */
	LIKE;
    
	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.marketing.i18n.messages";

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_operator_";
    
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