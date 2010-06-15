package com.code.aon.commercial.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different advertising values.
 * 
 * @author Esferalia. David Uriarte - 20-nov-2009
 * @since 1.0
 * @version 1.0
 */
public enum ExpenseHolderType implements IResourceable {

	/** SELLER. */
	SELLER,   

	/** EMPLOYEE. */
	EMPLOYEE;
    
	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.commercial.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_expense_holder_type_";
	
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

