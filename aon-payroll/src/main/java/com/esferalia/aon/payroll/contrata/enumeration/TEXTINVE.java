package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
* Enumeration for represent Contrata (S.E.P.E.) TEXTINVE table codes.
*/ 
public enum TEXTINVE implements IStringEnum {

	TEXTINVE_1( "1", "INVESTIGADOR", null, null ),
	TEXTINVE_2( "2", "CIENTIFICO O TECNICO", null, null ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	TEXTINVE( String value, String label, String startDate, String endDate ) {
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

	public static TEXTINVE getEnumByValue(String expression) {
		for( TEXTINVE o : TEXTINVE.values() ) {
			if ( o.getValue().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}