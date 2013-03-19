package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) TFGGRCOT table codes.
 * Generation main class: com.esferalia.aon.payroll.contrata.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * *TFGGRCOT	GRUPOS DE COTIZACION						15-01-2013
 *  ------------------------------------------------------------------------
 */ 
public enum TFGGRCOT implements IStringEnum {

	TFGGRCOT_01( "01", "DIRECTORES,INGENIEROS", "20020101", "20131231" ),
	TFGGRCOT_02( "02", "PERITOS Y AYUDANTES", "20020101", "20131231" ),
	TFGGRCOT_03( "03", "JEFES ADMINISTRATIVOS", "20020101", "20131231" ),
	TFGGRCOT_04( "04", "AYUDANTES NO TITULADOS", "20020101", "20131231" ),
	TFGGRCOT_05( "05", "OFICIALES ADMINISTRATIVOS", "20020101", "20131231" ),
	TFGGRCOT_06( "06", "SUBALTERNOS", "20020101", "20131231" ),
	TFGGRCOT_07( "07", "AUXILIARES ADMINISTRATIVOS", "20020101", "20131231" ),
	TFGGRCOT_08( "08", "OFICIALES DE PRIMERA", "20020101", "20131231" ),
	TFGGRCOT_09( "09", "OFICIALES DE TERCERA", "20020101", "20131231" ),
	TFGGRCOT_10( "10", "PEONES", "20020101", "20131231" ),
	TFGGRCOT_11( "11", "APRENDICES 17 AÑOS", "20020101", "20131231" ),
	TFGGRCOT_12( "12", "APRENDICES < 17 AÑOS", "20020101", "20131231" ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	TFGGRCOT( String value, String label, String startDate, String endDate ) {
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

	public static TFGGRCOT getEnumByValue(String expression) {
		for( TFGGRCOT o : TFGGRCOT.values() ) {
			if ( o.getValue().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}