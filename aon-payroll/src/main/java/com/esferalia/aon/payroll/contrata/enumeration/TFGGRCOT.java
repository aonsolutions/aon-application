package com.esferalia.aon.payroll.contrata.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) TFGGRCOT table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * *TFGGRCOT	GRUPOS DE COTIZACION						15-01-2013
 *  ------------------------------------------------------------------------
 */ 
public enum TFGGRCOT {

	TFGGRCOT_01( "01", "DIRECTORES,INGENIEROS", "20020101", "20131231" ),
	TFGGRCOT_02( "02", "PERITOS Y AYUDANTES", "20020101", "20131231" ),
	TFGGRCOT_03( "03", "JEFES ADMINISTRATIVOS", "20020101", "20131231" ),
	TFGGRCOT_04( "04", "AYUDANTES NO TITULADOS", "20020101", "20131231" ),
	TFGGRCOT_05( "05", "OFICIALES ADMINISTRATIVOS", "20020101", "20131231" ),
	TFGGRCOT_06( "06", "SUBALTERNOS", "20020101", "20131231" ),
	TFGGRCOT_07( "07", "AUXILIARES ADMINISTRATIVOS", "20020101", "20131231" ),
	TFGGRCOT_08( "08", "OFICIALES DE PRIMERA", "20020101", "20131231" ),
	TFGGRCOT_09( "09", "OFICIALES DE TERCERA", "20020101", "20131231" ),
	TFGGRCOT_10( "10", "PEONES", "20020101", "20131231" ),
	TFGGRCOT_11( "11", "APRENDICES 17 AÑOS", "20020101", "20131231" ),
	TFGGRCOT_12( "12", "APRENDICES < 17 AÑOS", "20020101", "20131231" ),
	;
	public static final String TABLE_NAME = "TFGGRCOT";
	public static final String TABLE_DESCRIPTION = "*TFGGRCOT	GRUPOS DE COTIZACION						15-01-2013";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TFGGRCOT( String code, String description, String startDate, String endDate ) {
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

	public static TFGGRCOT getEnumByValue(String expression) {
		for( TFGGRCOT o : TFGGRCOT.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}