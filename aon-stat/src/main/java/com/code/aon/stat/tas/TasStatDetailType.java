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

    private static final String MSG_KEY_PREFIX = "aon_enum_tas_stat_detail_type_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
    public String getColor() {
    	if (this == PROJECT) {
    		return "#CCFFFF";
    	} else if (this == OFFER) {
    		return "white";
    	} else if (this == SALES) {
    		return "white";
    	} else if (this == PURCHASE) {
    		return "white";
    	} else if (this == INCOME) {
    		return "white";
    	} else if (this == DELIVERY) {
    		return "white";
    	} else if (this == SALES_INVOICE) {
    		return "white";
    	} else if (this == PURCHASE_INVOICE) {
    		return "white";
    	} else if (this == EXPENSE_INVOICE) {
    		return "white";
    	}
		return "white";
    }
}
