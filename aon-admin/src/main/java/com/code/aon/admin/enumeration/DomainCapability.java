package com.code.aon.admin.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum DomainCapability implements IResourceable {

    /**
     * DOCUMENTAL
     */
	DOCUMENTAL,

	/**
     * MULTI_USER 
     */
	MULTI_USER,   

	/**
     * MULTI_DOMAIN 
     */
	MULTI_DOMAIN;   
	
	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.admin.i18n.messages";

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_domain_capability_";
    
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