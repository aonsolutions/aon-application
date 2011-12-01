package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
* Enumeration for represent Contrata (S.E.P.E.) TEWEINVE table codes.
*/ 
public enum TEWEINVE implements IStringEnum {

	TEWEINVE_1( "1", "ORGANISMO PUBLICO", null, null ),
	TEWEINVE_2( "2", "INSTITUCION SIN ANIMO DE LUCRO", null, null ),
	TEWEINVE_3( "3", "UNIVERSIDAD PUBLICA", null, null ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	TEWEINVE( String value, String label, String startDate, String endDate ) {
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