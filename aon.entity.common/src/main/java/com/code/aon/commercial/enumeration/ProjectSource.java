package com.code.aon.commercial.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different source of an Project.
 * 
 * @author Consulting & Development. Aimar Tellitu - 08-jun-2011
 * @since 1.0
 * @version 1.0
 */
public enum ProjectSource implements IResourceable {

	/** CALL_CENTER. */
	CALL_CENTER,

	/** COMMERCIAL_VISIT. */
	COMMERCIAL_VISIT,

    /** PRESCRIPTION. */
	PRESCRIPTION,
	
	/** WEB. */
	WEB,

    /** ADVERTISEMENT. */
	ADVERTISEMENT,
	
	/** EMAIL. */
	EMAIL,

    /** PRESENTATION. */
	PRESENTATION,

	/** RECOMMENDATION. */
	RECOMMENDATION,
	
	/** MK_ACTION. */
	MK_ACTION;
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_project_source_";
    
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