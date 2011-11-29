package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
* Enumeration for represent Contrata (S.E.P.E.) TQOCOLRE table codes.
*/ 
public enum TQOCOLRE implements IStringEnum {

	TQOCOLRE_01( "01", "DESEMPLEADOS CON EDAD IGUAL O INFERIOR A 30 AÑOS", "20110213", "20120212" ),
	TQOCOLRE_02( "02", "DESEMPLEADOS AL MENOS 12 MESES EN 18 ANTERIORES", "20110213", "20120212" ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	TQOCOLRE( String value, String label, String startDate, String endDate ) {
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