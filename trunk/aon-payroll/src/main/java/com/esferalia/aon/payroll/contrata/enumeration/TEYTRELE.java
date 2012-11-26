package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
* Enumeration for represent Contrata (S.E.P.E.) TEYTRELE table codes.
*/ 
public enum TEYTRELE implements IStringEnum {

	TEYTRELE_1( "1", "TRABAJADOR INSCRITO COMO DEMANDANTE", null, null ),
	TEYTRELE_2( "2", "TRABAJADOR CON CONTRATO DURAC.DETERMIN", null, null ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	TEYTRELE( String value, String label, String startDate, String endDate ) {
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