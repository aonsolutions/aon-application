package com.code.aon.common.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enumeration invoice periods security level.
 * 
 * @author Consulting & Development. Iñigo Gayarre - 31-ene-2005
 * @since 1.0
 * @version 1.0
 *  
 */
public enum SecurityLevel implements IResourceable {

    /**
     * Oficial
     */
	OFFICIAL,

    /**
     * Confidencial.
     */
	CONFIDENTIAL;

    /**
     * Messages file base path.
     */
    private static final String BASE_NAME = "com.code.aon.common.i18n.messages";

    /**
     * Messages key prefix. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_securitylevel_";

    /*
     * (non-Javadoc)
     * @see com.code.aon.common.enumeration.IResourceable#getName(java.util.Locale)
     */
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }

}