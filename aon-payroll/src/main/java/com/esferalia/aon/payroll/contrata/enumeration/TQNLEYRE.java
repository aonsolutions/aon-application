package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
* Enumeration for represent Contrata (S.E.P.E.) TQNLEYRE table codes.
*/ 
public enum TQNLEYRE implements IStringEnum {

	TQNLEYRE_01( "01", "REAL DECRETO LEY 1/2011", "20110213", "20120212" ),
	TQNLEYRE_02( "02", "REAL DECRETO LEY 10/2011", "20110831", "20120211" ),
	TQNLEYRE_03( "03", "REAL DECRETO LEY 3 / 2012", "20120212", "20120707" ),
	TQNLEYRE_04( "04", "LEY 14 / 2011", "20120602", null ),
	TQNLEYRE_05( "05", "LEY 3 / 2012", "20120708", null ),
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

	public static TQNLEYRE getEnumByValue(String expression) {
		for( TQNLEYRE o : TQNLEYRE.values() ) {
			if ( o.getValue().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}