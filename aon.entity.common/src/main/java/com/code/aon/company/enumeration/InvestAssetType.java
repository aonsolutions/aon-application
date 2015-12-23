package com.code.aon.company.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum InvestAssetType implements IResourceable {

	PREMISES,
	OTHER_BUILDING,
	MEANS_OF_TRANSPORT,
	FIXED_PHONE,
	CELLULAR_PHONE,
	FAX,
	FURNITURE,
	MACHINERY,
	COMPUTER_EQUIPMENT,
	INSTALLATION,
	ACCOUNT_GROUP_20_ASSET,
	ACCOUNT_GROUP_21_ASSET,
	ACCOUNT_GROUP_23_ASSET,
	BUILDING_PLOT;

    private static final String MSG_KEY_PREFIX = "aon_enum_invest_asset_type_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }

}