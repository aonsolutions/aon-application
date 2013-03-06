package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
* Enumeration for represent Contrata (S.E.P.E.) TRWCOLDF table codes.
*/ 
public enum TRWCOLDF implements IStringEnum {

	TRWCOLDF_01( "01", "PRIMER CONTRATO CON TRABAJADOR MENOR DE 30 AÑOS", "20120212", null ),
	TRWCOLDF_02( "02", "DESEMPLEADO BENEFICIARIO DE PRESTACIÓN CONTRIBUTIVA", "20120212", null ),
	TRWCOLDF_03( "03", "CONTRATO SIN DEDUCCIÓN FISCAL", "20120212", null ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	TRWCOLDF( String value, String label, String startDate, String endDate ) {
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

	public static TRWCOLDF getEnumByValue(String expression) {
		for( TRWCOLDF o : TRWCOLDF.values() ) {
			if ( o.getValue().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}