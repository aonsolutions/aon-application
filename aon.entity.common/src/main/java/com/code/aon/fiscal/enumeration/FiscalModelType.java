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
	M310("310");

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

	private static final String BASE_NAME = "com.code.aon.fiscal.i18n.messages";
	private static final String MSG_KEY_PREFIX = "aon_enum_fiscal_model_";

	public String getName(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale);
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}

}