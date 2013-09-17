package com.code.aon.registry.enumeration;

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
public enum RegistryItemStatus implements IResourceable {

	/** ACTIVE. */
	ACTIVE,
    
	/** INTERESTED. */
	INTERESTED,
    
    /** REFUSED. */
	REFUSED;
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_registry_item_status_";
    
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