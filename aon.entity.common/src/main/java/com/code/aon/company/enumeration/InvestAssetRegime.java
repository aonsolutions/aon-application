package com.code.aon.company.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum InvestAssetRegime implements IResourceable {

	PROPERTY,
	RENTING,
	FINANCIAL_LEASING,
	OTHER;

    private static final String MSG_KEY_PREFIX = "aon_enum_invest_asset_regime_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }

}