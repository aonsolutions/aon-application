package com.esferalia.aon.payroll.contrata.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) TQOCOLRE table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * *TQOCOLRE	COLECTIVOS DE REDUCCIÓN						14-06-2012 					 
 *  ------------------------------------------------------------------------
 */ 
public enum TQOCOLRE {

	TQOCOLRE_01( "01", "DESEMPLEADOS CON EDAD IGUAL O INFERIOR A 30 AÑOS", "20110213", "20120212" ),
	TQOCOLRE_02( "02", "DESEMPLEADOS AL MENOS 12 MESES EN 18 ANTERIORES", "20110213", "20120212" ),
	TQOCOLRE_03( "03", "DESEMPLEADOS MAYORES DE 20 AÑOS INSCRITOS A 16/08/2011", "20110831", "20120211" ),
	TQOCOLRE_04( "04", "DESEMPLEADOS INSCRITOS EN OFICINA DE EMPLEO", "20120212", null ),
	TQOCOLRE_05( "05", "TRANSFORMACIÓN DE CONTRATO DE FORMACIÓN – HOMBRE", "20120212", null ),
	TQOCOLRE_06( "06", "TRANSFORMACIÓN DE CONTRATO DE FORMACIÓN – MUJER", "20120212", null ),
	TQOCOLRE_07( "07", "CONTRATO PREDOCTORAL", "20120602", null ),
	;
	public static final String TABLE_NAME = "TQOCOLRE";
	public static final String TABLE_DESCRIPTION = "*TQOCOLRE	COLECTIVOS DE REDUCCIÓN						14-06-2012 					 ";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TQOCOLRE( String code, String description, String startDate, String endDate ) {
		this.code = code;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public Date getStartDate(){
		try {
			if(startDate!=null){
				return sdf.parse(startDate);
			}
		} catch (ParseException e) {
			// nothing to do
		}
		return null;
	}

	public Date getEndDate(){
		try {
			if(endDate!=null){
				return sdf.parse(endDate);
			}
		} catch (ParseException e) {
			// nothing to do
		}
	return null;
	}

	public static TQOCOLRE getEnumByValue(String expression) {
		for( TQOCOLRE o : TQOCOLRE.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}