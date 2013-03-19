package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) TEUECCLL table codes.
 * Generation main class: com.esferalia.aon.payroll.contrata.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 *  TEUECCLL	CORPORACIONES LOCALES				
 *  ------------------------------------------------------------------------
 */ 
public enum TEUECCLL implements IStringEnum {

	TEUECCLL_1( "1", "AYUNTAMIENTOS", null, null ),
	TEUECCLL_2( "2", "DIPUTACIONES Y CABILDOS", null, null ),
	TEUECCLL_3( "3", "ENTIDADES DEPENDIENTES DE CORP.LOCALES", null, null ),
	TEUECCLL_4( "4", "OTRAS ENTIDADES LOCALES", null, null ),
	TEUECCLL_5( "5", "MANCOMUNIDADES Y COMARCAS", null, null ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	TEUECCLL( String value, String label, String startDate, String endDate ) {
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

	public static TEUECCLL getEnumByValue(String expression) {
		for( TEUECCLL o : TEUECCLL.values() ) {
			if ( o.getValue().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}