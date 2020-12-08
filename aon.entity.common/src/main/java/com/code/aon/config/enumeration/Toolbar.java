package com.code.aon.config.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum Toolbar implements IResourceable {

	/**
     * GOOGLE 
     */
	GOOGLE,
	
	/**
     * MICROSOFT 365 
     */
	MICROSOFT_365,
	
	/**
     * HOTMAIL 
     */
	HOTMAIL,

	/**
     * YAHOO 
     */
	YAHOO,

    /**
     * Esferalia WEBMAIL
     */
	ESFERALIA_WEBMAIL,

	/**
     * ARSYS 
     */
	ARSYS,   

	/**
     * ACENS 
     */
	ACENS,

	/**
     * AON SOLUTIONS
     */
	AON_SOLUTIONS

	;   
	
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_toolbar_";

    /** Template path. */
    private static final String TEMPLATE_PATH = "/com/code/aon/ui/resources/facelet/headerToolbar/";
    
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
    
    public String getTemplate() {
    	return TEMPLATE_PATH + toString().toLowerCase() + ".xhtml";
    }
    
}
