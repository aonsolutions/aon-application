package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) TEJINDIS table codes.
 * Generation main class: com.esferalia.aon.payroll.contrata.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 *  TEJINDIS	INDICADOR DISCAPACIDAD										
 *  ------------------------------------------------------------------------
 */ 
public enum TEJINDIS implements IStringEnum {

	TEJINDIS_C( "C", "DISCAPACITADOS EN CENTROS ESPEC.EMPLEO", null, null ),
	TEJINDIS_E( "E", "ENCLAVES LABORALES DISC.INTELECT.>=33%", null, null ),
	TEJINDIS_F( "F", "ENCLAVES LABORALES DISC.FÍS./SENS.>=65%", null, null ),
	TEJINDIS_G( "G", "ENCLAVES LABORALES MUJERES DISCAP.>=33%", null, null ),
	TEJINDIS_S( "S", "DISCAPACITADOS", null, null ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	TEJINDIS( String value, String label, String startDate, String endDate ) {
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

	public static TEJINDIS getEnumByValue(String expression) {
		for( TEJINDIS o : TEJINDIS.values() ) {
			if ( o.getValue().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}