package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) STDIDETC table codes.
 * Generation main class: com.esferalia.aon.payroll.contrata.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 *  STDIDETC	TIPO DE DOCUMENTO IDENTIFICATIVO	 	
 *  ------------------------------------------------------------------------
 */ 
public enum STDIDETC implements IStringEnum {

	STDIDETC_D( "D", "D.N.I", null, null ),
	STDIDETC_E( "E", "NUMERO IDENTIFICATIVO EXTRANJERO", null, null ),
	STDIDETC_U( "U", "CIUDADANOS DE LA UE/EEE SIN NIE", null, null ),
	STDIDETC_W( "W", "CIUD.QUE NO PERTENECEN A UE/EEE.SIN NIE", null, null ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	STDIDETC( String value, String label, String startDate, String endDate ) {
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

	public static STDIDETC getEnumByValue(String expression) {
		for( STDIDETC o : STDIDETC.values() ) {
			if ( o.getValue().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}