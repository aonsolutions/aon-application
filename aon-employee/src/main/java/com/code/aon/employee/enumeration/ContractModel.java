/**
 * 
 */
package com.code.aon.employee.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * @author rtrepiana
 *
 */
public enum ContractModel implements IResourceable {

	
	PE166,
	PE170,
	PE174,
	PE175,
	PE176,
	PE177,
	PE179,
	PE181,
	PE182,
	PE183,
	PE185,
	PE186,
	PE187,
	PE190,
	PE191,
	PE192,
	PE193,
	PE195,
	PE196,
	PE197,
	PE200,
	PE201,
	PE202,
	PE203,
	PE204,
	PE205,
	PE206,
	PE213,
	PE217,
	PE218,
	PE220,
	PE221;

	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.employee.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_contract_model_";
    
	@Override
	public String getName(Locale locale) {
		return toString();
	}
    
	public String getDescription(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}
    
}
