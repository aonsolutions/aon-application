package com.esferalia.aon.payroll.contrata.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) TRXLEYDF table codes.
 * Generation main class: com.esferalia.aon.payroll.contrata.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * *TRXLEYDF	LEY DE DEDUCCION FISCAL						26-07-2012
 *  ------------------------------------------------------------------------
 */ 
public enum TRXLEYDF {

	TRXLEYDF_01( "01", "REAL DECRETO LEY 3 / 2012", "20120212", "20120707" ),
	TRXLEYDF_02( "02", "LEY 3 / 2012", "20120708", null ),
	;
	public static final String TABLE_NAME = "TRXLEYDF";
	public static final String TABLE_DESCRIPTION = "*TRXLEYDF	LEY DE DEDUCCION FISCAL						26-07-2012";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TRXLEYDF( String code, String description, String startDate, String endDate ) {
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

	public static TRXLEYDF getEnumByValue(String expression) {
		for( TRXLEYDF o : TRXLEYDF.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}