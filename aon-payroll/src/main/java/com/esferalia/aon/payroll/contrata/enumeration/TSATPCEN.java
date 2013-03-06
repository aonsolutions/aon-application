package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
* Enumeration for represent Contrata (S.E.P.E.) TSATPCEN table codes.
*/ 
public enum TSATPCEN implements IStringEnum {

	TSATPCEN_E( "E", "CENTRO DEL SISTEMA EDUCATIVO", "20110831", null ),
	TSATPCEN_C( "C", "CENTRO ACREDITADO POR LA COMUNIDAD AUTÓNOMA", "20110831", null ),
	TSATPCEN_S( "S", "CENTRO ACREDITADO POR EL SEPE", "20110831", null ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	TSATPCEN( String value, String label, String startDate, String endDate ) {
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

	public static TSATPCEN getEnumByValue(String expression) {
		for( TSATPCEN o : TSATPCEN.values() ) {
			if ( o.getValue().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}