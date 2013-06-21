package com.esferalia.aon.payroll.contrata.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) TESCETCO table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * *TESCETCO	RELACIÓN CONTRACTUAL ET / CO / TE		
 *  ------------------------------------------------------------------------
 */ 
public enum TESCETCO {

	TESCETCO_E01( "E01", "CONTRATO TALLERES EMPLEO ALUMNO/TRABAJAD", "19990224", "0" ),
	TESCETCO_E02( "E02", "CONTRATO DE TALLERES DE EMPLEO PERSONAL", "19990224", "0" ),
	TESCETCO_O01( "O01", "CASA DE OFICIO ALUMNO / TRABAJADOR", "19880330", "0" ),
	TESCETCO_O02( "O02", "CASA DE OFICIO PERSONAL", "19880330", "0" ),
	TESCETCO_T01( "T01", "CONTRATO ESCUELA TALLER ALUMNO/TRABAJAD", "19880330", "0" ),
	TESCETCO_T02( "T02", "CONTRATO DE ESC.TALLER PERSONAL UPD", "19880330", "0" ),
	;
	public static final String TABLE_NAME = "TESCETCO";
	public static final String TABLE_DESCRIPTION = "*TESCETCO	RELACIÓN CONTRACTUAL ET / CO / TE		";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TESCETCO( String code, String description, String startDate, String endDate ) {
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

	public static TESCETCO getEnumByValue(String expression) {
		for( TESCETCO o : TESCETCO.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}