package com.esferalia.aon.payroll.contrata.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) TEXTINVE table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * *TEXTINVE	TIPO DE TRABAJADOR INVESTIGACIÓN				
 *  ------------------------------------------------------------------------
 */ 
public enum TEXTINVE {

	TEXTINVE_1( "1", "INVESTIGADOR", null, null ),
	TEXTINVE_2( "2", "CIENTIFICO O TECNICO", null, null ),
	;
	public static final String TABLE_NAME = "TEXTINVE";
	public static final String TABLE_DESCRIPTION = "*TEXTINVE	TIPO DE TRABAJADOR INVESTIGACIÓN				";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TEXTINVE( String code, String description, String startDate, String endDate ) {
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

	public static TEXTINVE getEnumByValue(String expression) {
		for( TEXTINVE o : TEXTINVE.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}