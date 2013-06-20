package com.esferalia.aon.payroll.contrata.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) STDIDETC table codes.
 * Generation main class: com.esferalia.aon.payroll.contrata.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 *  STDIDETC	TIPO DE DOCUMENTO IDENTIFICATIVO	 	
 *  ------------------------------------------------------------------------
 */ 
public enum STDIDETC {

	STDIDETC_D( "D", "D.N.I", null, null ),
	STDIDETC_E( "E", "NUMERO IDENTIFICATIVO EXTRANJERO", null, null ),
	STDIDETC_U( "U", "CIUDADANOS DE LA UE/EEE SIN NIE", null, null ),
	STDIDETC_W( "W", "CIUD.QUE NO PERTENECEN A UE/EEE.SIN NIE", null, null ),
	;
	public static final String TABLE_NAME = "STDIDETC";
	public static final String TABLE_DESCRIPTION = " STDIDETC	TIPO DE DOCUMENTO IDENTIFICATIVO	 	";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	STDIDETC( String code, String description, String startDate, String endDate ) {
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

	public static STDIDETC getEnumByValue(String expression) {
		for( STDIDETC o : STDIDETC.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}