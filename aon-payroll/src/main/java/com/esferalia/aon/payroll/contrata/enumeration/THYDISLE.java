package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) THYDISLE table codes.
 * Generation main class: com.esferalia.aon.payroll.contrata.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * *THYDISLE	DISPOSICIONES LEGALES				
 *  ------------------------------------------------------------------------
 */ 
public enum THYDISLE implements IStringEnum {

	THYDISLE_001( "001", "LEY 45/2002 MAYORES DE 52 PERC.SUB.REASS", "20021214", "00000000" ),
	THYDISLE_002( "002", "LEY 45/2002 MAYORES DE 52 PERC.RESTO SUB", "20021214", "00000000" ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	THYDISLE( String value, String label, String startDate, String endDate ) {
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

	public static THYDISLE getEnumByValue(String expression) {
		for( THYDISLE o : THYDISLE.values() ) {
			if ( o.getValue().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}