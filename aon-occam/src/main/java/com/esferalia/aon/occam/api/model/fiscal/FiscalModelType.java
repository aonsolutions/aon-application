package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum FiscalModelType implements Serializable{
	
	M111	("111","111"), 
	M115	("115","115"), 
	M123	("123","123"), 
	M130	("130","130"),
	M131	("131","131"),
	M303_RG	("303","303 R.G."),
	M303_RS	("303","303 R.S."),
	M340	("340","340"),
	M347	("347","347"),
	M349	("349","349"),
	M390	("390","390"),
	M390_HF	("390","390 H.F."),
	M180	("180","180"),
	M184	("184","184"),
	M190	("190","190"),
	M193	("193","193"),
	M310	("310","310"),
	M311	("311","311"),
	M200	("200","200"),
	M202	("202","202")
	;

	private String value;
	private String name;
	
	private FiscalModelType(String value,String name) {
		this.value = value;
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public String getName() {
		return name;
	}
	
	public static FiscalModelType safeValueOf(String value) {
		if (value == null) return null;
		for (FiscalModelType t : FiscalModelType.values()) {
			if (AonStringUtils.equals(value, t.getValue())) return t;
		}
		return null;
	}
	public String getName(Administration admon, Period period) {
		if (this == M111) {
			if (period.isQuarterPeriod() && (admon == Administration.ALAVA 
				|| admon == Administration.BIZKAIA
				|| admon == Administration.GIPUZKOA) ) {
				return "110";
			} else if (admon == Administration.NAVARRA) {
				return (period.isMonthPeriod()?"745":"715");	
			}
		} else if (this == M115) {
			if (admon == Administration.ALAVA) {
				return "115-A";
			} else  if (admon == Administration.NAVARRA) {
				return (period.isMonthPeriod()?"760":"759");	
			}
		}
		return name;
	}
	
	
}