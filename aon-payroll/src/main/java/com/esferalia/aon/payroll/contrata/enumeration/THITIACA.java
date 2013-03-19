package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) THITIACA table codes.
 * Generation main class: com.esferalia.aon.payroll.contrata.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 *  THITIACA	TITULACIÓN ACADÉMICA						15-01-2013 
 *  ------------------------------------------------------------------------
 */ 
public enum THITIACA implements IStringEnum {

		;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	THITIACA( String value, String label, String startDate, String endDate ) {
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

	public static THITIACA getEnumByValue(String expression) {
		for( THITIACA o : THITIACA.values() ) {
			if ( o.getValue().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}