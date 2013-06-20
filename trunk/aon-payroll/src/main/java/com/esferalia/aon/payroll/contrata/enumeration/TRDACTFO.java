package com.esferalia.aon.payroll.contrata.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) TRDACTFO table codes.
 * Generation main class: com.esferalia.aon.payroll.contrata.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * *TRDACTFO	ACTIVIDAD FORMATIVA						15-01-2013
 *  ------------------------------------------------------------------------
 */ 
public enum TRDACTFO {

	TRDACTFO_A( "A", "CERTIFICACIÓN ACADÉMICA / ACREDITACIÓN PARCIAL ACUMULABLE", "20120212", null ),
	TRDACTFO_C( "C", "CERTIFICADO DE PROFESIONALIDAD", "20110831", "20131231" ),
	TRDACTFO_P( "P", "TÍTULO DE FORMACIÓN PROFESIONAL", "20110831", "20131231" ),
	TRDACTFO_O( "O", "OCUPACIÓN OBJETO DEL CONTRATO", "20110831", null ),
	;
	public static final String TABLE_NAME = "TRDACTFO";
	public static final String TABLE_DESCRIPTION = "*TRDACTFO	ACTIVIDAD FORMATIVA						15-01-2013";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TRDACTFO( String code, String description, String startDate, String endDate ) {
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

	public static TRDACTFO getEnumByValue(String expression) {
		for( TRDACTFO o : TRDACTFO.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}