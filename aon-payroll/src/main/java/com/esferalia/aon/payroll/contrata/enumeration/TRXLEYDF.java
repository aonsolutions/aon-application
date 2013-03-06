package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
* Enumeration for represent Contrata (S.E.P.E.) TRXLEYDF table codes.
*/ 
public enum TRXLEYDF implements IStringEnum {

	TRXLEYDF_01( "01", "REAL DECRETO LEY 3 / 2012", "20120212", "20120707" ),
	TRXLEYDF_02( "02", "LEY 3 / 2012", "20120708", null ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	TRXLEYDF( String value, String label, String startDate, String endDate ) {
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

	public static TRXLEYDF getEnumByValue(String expression) {
		for( TRXLEYDF o : TRXLEYDF.values() ) {
			if ( o.getValue().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}