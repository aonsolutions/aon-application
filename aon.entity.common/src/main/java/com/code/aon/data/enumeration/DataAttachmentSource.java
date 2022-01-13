package com.code.aon.data.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum DataAttachmentSource implements IResourceable {

	QUALITY,
	INVOICE,
	FBATCH,
	PRODUCTION,
	DELIVERY,
	SII,
	SERES,
	INGENET,
	MOD303,
	MOD111,
	MOD115,
	MOD123,
	MOD130,
	MOD131,
	MOD390,
	IMPORTATION,
	SISTEMA_RED,
	INVOICE_PRINT_CONFIGURATION,
	TBAI,
	MOD202,
	MOD190,
	LROE;
	
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_data_attachment_source_";

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