package com.code.aon.data.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum DataResponseSource implements IResourceable {

	QUALITY,
	PROJECT,
	HOTEL,
	SII,
	SII_INVOICE,
	SII_FINANCE,
	TEDI_INVOICE,
	SERES_DELIVERY,
	SERES_INVOICE,
	SERES_SALES,
	INGENET,
	PACKING_LIST_NOTIFICATION;
	
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_data_response_source_";

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