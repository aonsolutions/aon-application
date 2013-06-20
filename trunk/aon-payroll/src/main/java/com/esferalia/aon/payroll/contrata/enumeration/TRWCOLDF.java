package com.esferalia.aon.payroll.contrata.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) TRWCOLDF table codes.
 * Generation main class: com.esferalia.aon.payroll.contrata.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * *TRWCOLDF	COLECTIVO DE DEDUCCION FISCAL					29-02-2012
 *  ------------------------------------------------------------------------
 */ 
public enum TRWCOLDF {

	TRWCOLDF_01( "01", "PRIMER CONTRATO CON TRABAJADOR MENOR DE 30 AÑOS", "20120212", null ),
	TRWCOLDF_02( "02", "DESEMPLEADO BENEFICIARIO DE PRESTACIÓN CONTRIBUTIVA", "20120212", null ),
	TRWCOLDF_03( "03", "CONTRATO SIN DEDUCCIÓN FISCAL", "20120212", null ),
	;
	public static final String TABLE_NAME = "TRWCOLDF";
	public static final String TABLE_DESCRIPTION = "*TRWCOLDF	COLECTIVO DE DEDUCCION FISCAL					29-02-2012";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TRWCOLDF( String code, String description, String startDate, String endDate ) {
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

	public static TRWCOLDF getEnumByValue(String expression) {
		for( TRWCOLDF o : TRWCOLDF.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}