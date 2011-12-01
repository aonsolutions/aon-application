package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
* Enumeration for represent Contrata (S.E.P.E.) TCMCSEXO table codes.
*/ 
public enum TCMCSEXO implements IStringEnum {

	TCMCSEXO_1( "1", "HOMBRE", null, null ),
	TCMCSEXO_2( "2", "MUJER", null, null ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	TCMCSEXO( String value, String label, String startDate, String endDate ) {
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