package com.code.aon.stat.tas;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum TasStatDetailType  implements IResourceable {
	
	PROJECT
	,OFFER
	,SALES
	,PURCHASE
	,INCOME
	,DELIVERY
	,SALES_INVOICE
	,PURCHASE_INVOICE
	,EXPENSE_INVOICE;

    private static final String BASE_NAME = "com.code.aon.stat.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_tas_stat_detail_type_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }}
