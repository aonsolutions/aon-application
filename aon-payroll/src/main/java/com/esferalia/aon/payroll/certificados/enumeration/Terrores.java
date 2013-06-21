package com.esferalia.aon.payroll.certificados.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * Enumeration for represent Certificados (S.E.P.E.) Terrores table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.CertificadosCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * No description found
 *  ------------------------------------------------------------------------
 */ 
public enum Terrores {

	;
	public static final String TABLE_NAME = "Terrores";
	public static final String TABLE_DESCRIPTION = "No description found";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	Terrores( String code, String description, String startDate, String endDate ) {
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

	public static Terrores getEnumByValue(String expression) {
		for( Terrores o : Terrores.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}