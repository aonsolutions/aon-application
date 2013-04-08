package com.esferalia.aon.payroll.contrata.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) TQNLEYRE table codes.
 * Generation main class: com.esferalia.aon.payroll.contrata.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * *TQNLEYRE	LEYES DE REDUCCIÓN						26-07-2012
 *  ------------------------------------------------------------------------
 */ 
public enum TQNLEYRE {

	TQNLEYRE_01( "01", "REAL DECRETO LEY 1/2011", "20110213", "20120212" ),
	TQNLEYRE_02( "02", "REAL DECRETO LEY 10/2011", "20110831", "20120211" ),
	TQNLEYRE_03( "03", "REAL DECRETO LEY 3 / 2012", "20120212", "20120707" ),
	TQNLEYRE_04( "04", "LEY 14 / 2011", "20120602", null ),
	TQNLEYRE_05( "05", "LEY 3 / 2012", "20120708", null ),
	;
	public static final String TABLE_NAME = "TQNLEYRE";
	public static final String TABLE_DESCRIPTION = "*TQNLEYRE	LEYES DE REDUCCIÓN						26-07-2012";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TQNLEYRE( String code, String description, String startDate, String endDate ) {
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

	public static TQNLEYRE getEnumByValue(String expression) {
		for( TQNLEYRE o : TQNLEYRE.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}