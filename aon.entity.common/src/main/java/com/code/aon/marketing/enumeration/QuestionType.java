package com.code.aon.marketing.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different types of an Question.
 * 
 * @author Consulting & Development. Aimar Tellitu - 19-sep-2008
 * @since 1.0
 * @version 1.0
 */
public enum QuestionType implements IResourceable {

	/** INFO. */
	INFO,
    
	/** TEXT. */
	TEXT,
    
    /** NUMBER. */
	NUMBER,
	
    /** DATE. */
	DATE,
	
    /** BOOLEAN. */
	BOOLEAN;
    
	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.marketing.i18n.messages";

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_question_type_";
    
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