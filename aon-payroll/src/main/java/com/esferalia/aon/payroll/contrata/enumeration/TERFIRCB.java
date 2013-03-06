package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
* Enumeration for represent Contrata (S.E.P.E.) TERFIRCB table codes.
*/ 
public enum TERFIRCB implements IStringEnum {

	TERFIRCB_1( "1", "FIRMADAS POR LOS REPRESENTANTES LEGALES", null, null ),
	TERFIRCB_2( "2", "NO EXISTE REPRESENTACION LEGAL", null, null ),
	TERFIRCB_3( "3", "NO SE HA FACILITADO COPIA", null, null ),
	TERFIRCB_4( "4", "REHUSAN FIRMAR", null, null ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	TERFIRCB( String value, String label, String startDate, String endDate ) {
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

	public static TERFIRCB getEnumByValue(String expression) {
		for( TERFIRCB o : TERFIRCB.values() ) {
			if ( o.getValue().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}