package com.code.aon.registry.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * The Enum RegistryAttachmentType.
 */
public enum RegistryAttachmentType implements IResourceable {


	/** The LOGO. */
	LOGO,
	
	/** The ADDITIONAL IMAGE. */
	ADDITIONAL_IMAGE,
	
	/** The BANNER. */
	BANNER;
	
	
	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.registry.i18n.messages";

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_registry_attachment_type_";

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