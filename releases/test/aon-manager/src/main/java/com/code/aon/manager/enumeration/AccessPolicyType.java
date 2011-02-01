package com.code.aon.manager.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum AccessPolicyType implements IResourceable {

    /**
     * NOMINAL
     */
	NOMINAL ("Nominal"),

	/**
     * LOW 
     */
	CONCURRENT ("Concurrent");   
    
	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.manager.i18n.messages";

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_access_policy_type_";
    
    private String name;
    
    AccessPolicyType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
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
    
	public static AccessPolicyType get(String name) {
    	for( AccessPolicyType type : AccessPolicyType.values() ) {
    		if ( type.getName().equals(name) ) {
    			return type;
    		}
    	}
    	return null;
	}
    
}
