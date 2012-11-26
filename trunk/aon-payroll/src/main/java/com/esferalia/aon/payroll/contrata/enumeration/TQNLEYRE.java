package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
* Enumeration for represent Contrata (S.E.P.E.) TQNLEYRE table codes.
*/ 
public enum TQNLEYRE implements IStringEnum {

	TQNLEYRE_01( "01", "REAL DECRETO LEY 1/2011", "20110213", "20120212" ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	TQNLEYRE( String value, String label, String startDate, String endDate ) {
		this.value = value;
		this.label = label;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	@Override
	public String getValue() {
		return value;
	}

	public String getLabel() {
		return label;
	}

	public String getStartDate() {
		return startDate;
	}

	public String getEndDate() {
		return endDate;
	}
}