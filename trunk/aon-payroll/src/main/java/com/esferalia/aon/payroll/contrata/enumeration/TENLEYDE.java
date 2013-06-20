package com.esferalia.aon.payroll.contrata.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) TENLEYDE table codes.
 * Generation main class: com.esferalia.aon.payroll.contrata.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * *TENLEYDE	LEY FOMENTO DE LA CONTR. INDEFINIDA				29-02-2012
 *  ------------------------------------------------------------------------
 */ 
public enum TENLEYDE {

	TENLEYDE_01( "01", "LEY 63 / 1997", "19970517", "20010303" ),
	TENLEYDE_02( "02", "LEY 12 / 2001", "20010304", "20100617" ),
	TENLEYDE_03( "03", "REAL DECRETO LEY 10 / 2010", "20100618", "20100918" ),
	TENLEYDE_04( "04", "LEY 35 / 2010", "20100919", "20120211" ),
	;
	public static final String TABLE_NAME = "TENLEYDE";
	public static final String TABLE_DESCRIPTION = "*TENLEYDE	LEY FOMENTO DE LA CONTR. INDEFINIDA				29-02-2012";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TENLEYDE( String code, String description, String startDate, String endDate ) {
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

	public static TENLEYDE getEnumByValue(String expression) {
		for( TENLEYDE o : TENLEYDE.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}