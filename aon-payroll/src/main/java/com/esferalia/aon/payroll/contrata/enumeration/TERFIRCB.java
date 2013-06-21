package com.esferalia.aon.payroll.contrata.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) TERFIRCB table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 *  TERFIRCB	FIRMA COPIA BÁSICA				
 *  ------------------------------------------------------------------------
 */ 
public enum TERFIRCB {

	TERFIRCB_1( "1", "FIRMADAS POR LOS REPRESENTANTES LEGALES", null, null ),
	TERFIRCB_2( "2", "NO EXISTE REPRESENTACION LEGAL", null, null ),
	TERFIRCB_3( "3", "NO SE HA FACILITADO COPIA", null, null ),
	TERFIRCB_4( "4", "REHUSAN FIRMAR", null, null ),
	;
	public static final String TABLE_NAME = "TERFIRCB";
	public static final String TABLE_DESCRIPTION = " TERFIRCB	FIRMA COPIA BÁSICA				";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TERFIRCB( String code, String description, String startDate, String endDate ) {
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

	public static TERFIRCB getEnumByValue(String expression) {
		for( TERFIRCB o : TERFIRCB.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}