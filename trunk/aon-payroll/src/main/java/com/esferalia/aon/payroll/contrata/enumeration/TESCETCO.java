package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) TESCETCO table codes.
 * Generation main class: com.esferalia.aon.payroll.contrata.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * *TESCETCO	RELACIÓN CONTRACTUAL ET / CO / TE		
 *  ------------------------------------------------------------------------
 */ 
public enum TESCETCO implements IStringEnum {

	TESCETCO_E01( "E01", "CONTRATO TALLERES EMPLEO ALUMNO/TRABAJAD", "19990224", "0" ),
	TESCETCO_E02( "E02", "CONTRATO DE TALLERES DE EMPLEO PERSONAL", "19990224", "0" ),
	TESCETCO_O01( "O01", "CASA DE OFICIO ALUMNO / TRABAJADOR", "19880330", "0" ),
	TESCETCO_O02( "O02", "CASA DE OFICIO PERSONAL", "19880330", "0" ),
	TESCETCO_T01( "T01", "CONTRATO ESCUELA TALLER ALUMNO/TRABAJAD", "19880330", "0" ),
	TESCETCO_T02( "T02", "CONTRATO DE ESC.TALLER PERSONAL UPD", "19880330", "0" ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	TESCETCO( String value, String label, String startDate, String endDate ) {
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

	public static TESCETCO getEnumByValue(String expression) {
		for( TESCETCO o : TESCETCO.values() ) {
			if ( o.getValue().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}