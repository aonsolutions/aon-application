package com.esferalia.aon.pms.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum CreditCardType implements IResourceable, IStringEnum {

	AX("AX"),
	BC("BC"),
	CA("CA"),
	DC("DC"),
	DS("DS"),
	E("E"),
	EC("EC"),
	IK("IK"),
	JC("JC"),
	MC("MC"),
	R("R"),
	T("T"),
	TO("TO"),
	VI("VI"),
	UN("UN");

	private String value;

	private CreditCardType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

    private static final String MSG_KEY_PREFIX = "aon_enum_credit_card_type_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }

}