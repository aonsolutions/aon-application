package com.code.aon.fiscal.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum FiscalModelStatus implements IResourceable {
	
	PENDING, 
	FINISHED,
	BATCHED, 
	BLOCKED;

	private static final String MSG_KEY_PREFIX = "aon_enum_fiscal_model_status_";

	public String getName(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale);
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}

}