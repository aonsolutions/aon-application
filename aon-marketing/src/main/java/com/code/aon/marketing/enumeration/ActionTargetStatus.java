package com.code.aon.marketing.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different statuses of an Action Target.
 * 
 * @author Consulting & Development. Aimar Tellitu - 24-sep-2008
 * @since 1.0
 * @version 1.0
 */
public enum ActionTargetStatus implements IResourceable {

	/** SENT. */
	SENT,
    
	/** PENDING. */
	PENDING,
    
    /** FINISHED. */
	FINISHED,
	
    /** ABSENT. */
	ABSENT,
	
    /** INCORRECT. */
	INCORRECT,
	
    /** TRY_AGAIN. */
	TRY_AGAIN,
	
    /** CANCEL. */
	CANCEL;
    
	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.marketing.i18n.messages";

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_action_target_status_";
    
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