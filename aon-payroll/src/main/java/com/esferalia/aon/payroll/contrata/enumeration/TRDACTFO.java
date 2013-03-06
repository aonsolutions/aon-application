package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
* Enumeration for represent Contrata (S.E.P.E.) TRDACTFO table codes.
*/ 
public enum TRDACTFO implements IStringEnum {

	TRDACTFO_A( "A", "CERTIFICACIÓN ACADÉMICA / ACREDITACIÓN PARCIAL ACUMULABLE", "20120212", null ),
	TRDACTFO_C( "C", "CERTIFICADO DE PROFESIONALIDAD", "20110831", "20131231" ),
	TRDACTFO_P( "P", "TÍTULO DE FORMACIÓN PROFESIONAL", "20110831", "20131231" ),
	TRDACTFO_O( "O", "OCUPACIÓN OBJETO DEL CONTRATO", "20110831", null ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	TRDACTFO( String value, String label, String startDate, String endDate ) {
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

	public static TRDACTFO getEnumByValue(String expression) {
		for( TRDACTFO o : TRDACTFO.values() ) {
			if ( o.getValue().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}