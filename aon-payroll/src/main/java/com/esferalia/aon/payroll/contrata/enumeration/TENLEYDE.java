package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) TENLEYDE table codes.
 * Generation main class: com.esferalia.aon.payroll.contrata.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * *TENLEYDE	LEY FOMENTO DE LA CONTR. INDEFINIDA				29-02-2012
 *  ------------------------------------------------------------------------
 */ 
public enum TENLEYDE implements IStringEnum {

	TENLEYDE_01( "01", "LEY 63 / 1997", "19970517", "20010303" ),
	TENLEYDE_02( "02", "LEY 12 / 2001", "20010304", "20100617" ),
	TENLEYDE_03( "03", "REAL DECRETO LEY 10 / 2010", "20100618", "20100918" ),
	TENLEYDE_04( "04", "LEY 35 / 2010", "20100919", "20120211" ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	TENLEYDE( String value, String label, String startDate, String endDate ) {
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

	public static TENLEYDE getEnumByValue(String expression) {
		for( TENLEYDE o : TENLEYDE.values() ) {
			if ( o.getValue().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}