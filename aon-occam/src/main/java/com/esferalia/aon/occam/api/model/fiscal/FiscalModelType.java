package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum FiscalModelType implements Serializable{
	
	M111	("111","111", false), 
	M115	("115","115", false), 
	M123	("123","123", false), 
	M130	("130","130", false),
	M131	("131","131", false),
	M303_RG	("303","303 R.G.", false),
	M303_RS	("303","303 R.S.", false),
	M340	("340","340", false),
	M347	("347","347", true),
	M349	("349","349", false),
	M390	("390","390", true),
	M390_HF	("390","390 H.F.", true),
	M180	("180","180", true),
	M184	("184","184", true),
	M190	("190","190", true),
	M193	("193","193", true),
	M310	("310","310", false),
	M311	("311","311", false),
	M200	("200","200", true),
	M202	("202","202", false),
	M303    ("IVA","IVA", false)
	;

	private String value;
	private String name;
	private boolean yearly;
	
	private FiscalModelType(String value,String name, boolean yearly) {
		this.value = value;
		this.name = name;
		this.yearly = yearly;
	}
	public String getValue() {
		return value;
	}
	public String getName() {
		return name;
	}
	public boolean isYearly() {
		return yearly;
	}
	public static FiscalModelType safeValueOf(String value) {
		if (value == null) return null;
		for (FiscalModelType t : FiscalModelType.values()) {
			if (AonStringUtils.equals(value, t.getValue())) return t;
		}
		return null;
	}

	public boolean isMonthly(Administration admon) {
		return this != M130 && this != M131 && this != M202;
	}
	
	public boolean isOtherDeponentAllowedInSamePeriod() {
		return this == M130 || this == M131;
	}
	
}
