package com.esferalia.aon.payroll.certificados.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * Enumeration for represent Certific@2 (S.E.P.E.) DCSPCPTC table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.CertificadosCodeTablesWriter
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * No description found
 *  ------------------------------------------------------------------------
 */ 
public enum DCSPCPTC {

	DCSPCPTC_1( "1", null, null, null ),
	DCSPCPTC_2( "2", null, null, null ),
	DCSPCPTC_3( "3", null, null, null ),
	DCSPCPTC_4( "4", null, null, null ),
	DCSPCPTC_5( "5", null, null, null ),
	DCSPCPTC_6( "6", null, null, null ),
	DCSPCPTC_7( "7", null, null, null ),
	DCSPCPTC_8( "8", null, null, null ),

	;
	public static final String TABLE_NAME = "DCSPCPTC";
	public static final String TABLE_DESCRIPTION = "No description found";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	DCSPCPTC( String code, String description, String startDate, String endDate ) {
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

	public static DCSPCPTC getEnumByValue(String expression) {
		for( DCSPCPTC o : DCSPCPTC.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}