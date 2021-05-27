package com.esferalia.aon.pms.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum MealPlan implements IResourceable, IStringEnum {

	SA("SA","AL"),
	AD("AD","H/D"),
	MP("MP","M/P"),
	PC("PC","P/C"),
	TI("TI","TI"),
	TIP("TIP","TIB");

	private String value;
	private String crsValue;

	private MealPlan(String value, String crsValue) {
		this.value = value;
		this.crsValue = crsValue;
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public String getCrsValue() {
		return crsValue;
	}
	public void setCrsValue(String crsValue) {
		this.crsValue = crsValue;
	}

    private static final String MSG_KEY_PREFIX = "aon_enum_meal_plan_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }

}