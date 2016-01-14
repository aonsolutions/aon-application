package com.code.aon.fiscal.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum FiscalModelType implements IResourceable, IStringEnum {
	M111("111"), 
	M115("115"), 
	M123("123"), 
	M130("130"),
	M310("310"),
	M131("131"),
	M311("311"),
	M303("303"),
	M303_AI("3O3"), // Es UNA "O" no un CERO.
	;

	private String key;

	private FiscalModelType(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	@Override
	public String getValue() {
		return getKey();
	}

	private static final String MSG_KEY_PREFIX = "aon_enum_fiscal_model_";
	private static final String DESCR_KEY_PREFIX = MSG_KEY_PREFIX + "descr_";	

	public String getName(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale);
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}
	
	public String getDescription(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale);
		return bundle.getString(DESCR_KEY_PREFIX + toString());
	}

}