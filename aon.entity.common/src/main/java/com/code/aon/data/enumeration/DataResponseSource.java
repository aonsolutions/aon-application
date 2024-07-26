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
	TBAI,
	SERES_DELIVERY,
	SERES_INVOICE,
	SERES_SALES,
	INGENET,
	PACKING_LIST_NOTIFICATION,
	MOD303,
	MOD111,
	MOD115,
	MOD123,
	MOD130,
	MOD131,
	MOD390,
	INGENET_SALES,
	PATURPAT_QUALITY,
	ANALYTIC_ACCOUNTING,
	TBAI_TEST,
	IMPORTATION,
	NOTIFICATION_TOKEN,
	MOD202,
	MOD190,
	LROE,
	MOD180,
	MOD193,
	MOD184,
	MOD347,
	MOD349,
	MOD200,
	LROE_TEST,
	PACKAGING_DELIVERY
	;
	
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