package com.code.aon.fiscal.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum RetentionRegime implements IResourceable {

	 NORMAL_DIRECT_EVALUATION
	,SIMPLIFIED_DIRECT_EVALUATION
	,OBJECTIVE_EVALUATION
	,EXEMPT;

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_retention_regime_";

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