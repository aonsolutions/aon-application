package com.code.aon.finance.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum PrepaymentCollect implements IResourceable {

    FEE,
    INVOICE_DETAIL;

    private static final String MSG_KEY_PREFIX = "aon_enum_prepayment_collect_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
}