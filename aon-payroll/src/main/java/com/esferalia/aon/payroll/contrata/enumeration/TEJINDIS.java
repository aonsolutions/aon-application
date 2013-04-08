package com.esferalia.aon.payroll.contrata.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) TEJINDIS table codes.
 * Generation main class: com.esferalia.aon.payroll.contrata.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 *  TEJINDIS	INDICADOR DISCAPACIDAD										
 *  ------------------------------------------------------------------------
 */ 
public enum TEJINDIS {

	TEJINDIS_C( "C", "DISCAPACITADOS EN CENTROS ESPEC.EMPLEO", null, null ),
	TEJINDIS_E( "E", "ENCLAVES LABORALES DISC.INTELECT.>=33%", null, null ),
	TEJINDIS_F( "F", "ENCLAVES LABORALES DISC.FÍS./SENS.>=65%", null, null ),
	TEJINDIS_G( "G", "ENCLAVES LABORALES MUJERES DISCAP.>=33%", null, null ),
	TEJINDIS_S( "S", "DISCAPACITADOS", null, null ),
	;
	public static final String TABLE_NAME = "TEJINDIS";
	public static final String TABLE_DESCRIPTION = " TEJINDIS	INDICADOR DISCAPACIDAD										";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TEJINDIS( String code, String description, String startDate, String endDate ) {
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

	public static TEJINDIS getEnumByValue(String expression) {
		for( TEJINDIS o : TEJINDIS.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}