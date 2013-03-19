package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) TEQPTIEM table codes.
 * Generation main class: com.esferalia.aon.payroll.contrata.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 *  TEQPTIEM	PERÍODO DE TIEMPO				
 *  ------------------------------------------------------------------------
 */ 
public enum TEQPTIEM implements IStringEnum {

	TEQPTIEM_A( "A", "JORNADA ANUAL", null, null ),
	TEQPTIEM_D( "D", "JORNADA DIARIA", null, null ),
	TEQPTIEM_M( "M", "JORNADA MENSUAL", null, null ),
	TEQPTIEM_S( "S", "JORNADA SEMANAL", null, null ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	TEQPTIEM( String value, String label, String startDate, String endDate ) {
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

	public static TEQPTIEM getEnumByValue(String expression) {
		for( TEQPTIEM o : TEQPTIEM.values() ) {
			if ( o.getValue().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}