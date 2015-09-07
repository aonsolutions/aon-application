package com.esferalia.aon.gwt.office.client.values;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum LabelValue {
	BUG("bug", "bug"),
	DUPLICATE("duplicate", "duplicate"),
	ENHANCEMENT("enhancement", "enhancement"),
	HELP_WANTED("helpWanted", "helpWanted"),
	HOT_FIX("hotFix", "hotFix"),
	INVALID("invalid", "invalid"),
	QA_SUCCESS("qaSuccess", "qaSuccess"),
	QUESTION("question", "question"),
	WONT_FIX("wontFix", "wontFix")
	;
	
	private String value;
	private String name;
	
	private LabelValue(String value, String name) {
		this.value = value;
		this.name = name;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static LabelValue safeValueOf(String value) {
		if (value == null) return null;
		
		for (LabelValue t : LabelValue.values())
			if (AonStringUtils.equals(value, t.getValue()))
				return t;
		
		return null;
	}

}
