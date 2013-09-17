package com.code.aon.accounting.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * The Enum AccountEntryType.
 */
public enum AccountPeriodStatus implements IResourceable {
	
    ACTIVE,
    INACTIVE,
    OPENING,
    OPERATING,
    CLOSED;

    private static final String MSG_KEY_PREFIX = "aon_enum_account_period_status_";
	
    @Override
	public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
}