package com.code.aon.config.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum Administration implements IResourceable {
	
	ALAVA("01"),
	BIZKAIA("48"),
	GIPUZKOA("20"),
	NAVARRA("31"),
	COMMON_TERRITORY(null);
	
	
    private static final String BASE_NAME = "com.code.aon.config.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_administration_";

    private String geozoneCode;
    
	private Administration(String geozoneCode) {
		this.geozoneCode = geozoneCode;	
	}

	public String getGeozoneCode() {
		return geozoneCode;
	}

	public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
}