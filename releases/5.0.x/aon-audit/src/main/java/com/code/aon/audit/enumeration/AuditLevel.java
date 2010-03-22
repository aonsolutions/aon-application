package com.code.aon.audit.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different status of an TargetItem.
 * 
 * @author Consulting & Development. Aimar Tellitu - 21-jul-2008
 * @since 1.0
 * @version 1.0
 */
public enum AuditLevel implements IResourceable {

	/** NONE. */
	NONE,
    
	/** ACCESS. */
	ACCESS,
    
    /** MODULE. */
	MODULE;
    
	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.audit.i18n.messages";

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_audit_level_";
    
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