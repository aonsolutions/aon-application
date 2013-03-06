package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
* Enumeration for represent Contrata (S.E.P.E.) TRCMODFO table codes.
*/ 
public enum TRCMODFO implements IStringEnum {

	TRCMODFO_D( "D", "A DISTANCIA", "20110831", "20131231" ),
	TRCMODFO_M( "M", "MIXTA", "20110831", "20131231" ),
	TRCMODFO_P( "P", "PRESENCIAL", "20110831", "20131231" ),
	TRCMODFO_T( "T", "TELEFORMACIÓN", "20110831", null ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	TRCMODFO( String value, String label, String startDate, String endDate ) {
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

	public static TRCMODFO getEnumByValue(String expression) {
		for( TRCMODFO o : TRCMODFO.values() ) {
			if ( o.getValue().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}