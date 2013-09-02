package com.esferalia.aon.payroll.contrata.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * Enumeration for represent Contrat@ (S.E.P.E.) TEQPTIEM table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.ContrataCodeTablesWriter
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 *  TEQPTIEM	PERÍODO DE TIEMPO				
 *  ------------------------------------------------------------------------
 */ 
public enum TEQPTIEM {

	TEQPTIEM_A( "A", "JORNADA ANUAL", null, null ),
	TEQPTIEM_D( "D", "JORNADA DIARIA", null, null ),
	TEQPTIEM_M( "M", "JORNADA MENSUAL", null, null ),
	TEQPTIEM_S( "S", "JORNADA SEMANAL", null, null ),
	;
	public static final String TABLE_NAME = "TEQPTIEM";
	public static final String TABLE_DESCRIPTION = " TEQPTIEM	PERÍODO DE TIEMPO				";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TEQPTIEM( String code, String description, String startDate, String endDate ) {
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

	public static TEQPTIEM getEnumByValue(String expression) {
		for( TEQPTIEM o : TEQPTIEM.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}