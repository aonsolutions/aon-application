package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) TQOCOLRE table codes.
 * Generation main class: com.esferalia.aon.payroll.contrata.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * *TQOCOLRE	COLECTIVOS DE REDUCCIÓN						14-06-2012 					 
 *  ------------------------------------------------------------------------
 */ 
public enum TQOCOLRE implements IStringEnum {

	TQOCOLRE_01( "01", "DESEMPLEADOS CON EDAD IGUAL O INFERIOR A 30 AÑOS", "20110213", "20120212" ),
	TQOCOLRE_02( "02", "DESEMPLEADOS AL MENOS 12 MESES EN 18 ANTERIORES", "20110213", "20120212" ),
	TQOCOLRE_03( "03", "DESEMPLEADOS MAYORES DE 20 AÑOS INSCRITOS A 16/08/2011", "20110831", "20120211" ),
	TQOCOLRE_04( "04", "DESEMPLEADOS INSCRITOS EN OFICINA DE EMPLEO", "20120212", null ),
	TQOCOLRE_05( "05", "TRANSFORMACIÓN DE CONTRATO DE FORMACIÓN – HOMBRE", "20120212", null ),
	TQOCOLRE_06( "06", "TRANSFORMACIÓN DE CONTRATO DE FORMACIÓN – MUJER", "20120212", null ),
	TQOCOLRE_07( "07", "CONTRATO PREDOCTORAL", "20120602", null ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	TQOCOLRE( String value, String label, String startDate, String endDate ) {
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

	public static TQOCOLRE getEnumByValue(String expression) {
		for( TQOCOLRE o : TQOCOLRE.values() ) {
			if ( o.getValue().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}