package com.esferalia.aon.payroll.contrata.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) TEYTRELE table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 *  TEYTRELE	TIPO DE TRABAJADOR DE RELEVO			
 *  ------------------------------------------------------------------------
 */ 
public enum TEYTRELE {

	TEYTRELE_1( "1", "TRABAJADOR INSCRITO COMO DEMANDANTE", null, null ),
	TEYTRELE_2( "2", "TRABAJADOR CON CONTRATO DURAC.DETERMIN", null, null ),
	;
	public static final String TABLE_NAME = "TEYTRELE";
	public static final String TABLE_DESCRIPTION = " TEYTRELE	TIPO DE TRABAJADOR DE RELEVO			";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TEYTRELE( String code, String description, String startDate, String endDate ) {
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

	public static TEYTRELE getEnumByValue(String expression) {
		for( TEYTRELE o : TEYTRELE.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}