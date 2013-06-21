package com.esferalia.aon.payroll.contrata.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) THYDISLE table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * *THYDISLE	DISPOSICIONES LEGALES				
 *  ------------------------------------------------------------------------
 */ 
public enum THYDISLE {

	THYDISLE_001( "001", "LEY 45/2002 MAYORES DE 52 PERC.SUB.REASS", "20021214", "00000000" ),
	THYDISLE_002( "002", "LEY 45/2002 MAYORES DE 52 PERC.RESTO SUB", "20021214", "00000000" ),
	;
	public static final String TABLE_NAME = "THYDISLE";
	public static final String TABLE_DESCRIPTION = "*THYDISLE	DISPOSICIONES LEGALES				";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	THYDISLE( String code, String description, String startDate, String endDate ) {
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

	public static THYDISLE getEnumByValue(String expression) {
		for( THYDISLE o : THYDISLE.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}