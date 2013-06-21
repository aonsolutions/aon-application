package com.esferalia.aon.payroll.contrata.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) TAUCOMAU table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 *  TAUCOMAU	COMUNIDAD AUTÓNOMA				
 *  ------------------------------------------------------------------------
 */ 
public enum TAUCOMAU {

	TAUCOMAU_01( "01", "ANDALUCIA", null, null ),
	TAUCOMAU_02( "02", "ARAGON", null, null ),
	TAUCOMAU_03( "03", "ASTURIAS", null, null ),
	TAUCOMAU_04( "04", "BALEARES", null, null ),
	TAUCOMAU_05( "05", "CANARIAS", null, null ),
	TAUCOMAU_06( "06", "CANTABRIA", null, null ),
	TAUCOMAU_07( "07", "CASTILLA LA MANCHA", null, null ),
	TAUCOMAU_08( "08", "CASTILLA LEON", null, null ),
	TAUCOMAU_09( "09", "CATALUÑA", null, null ),
	TAUCOMAU_10( "10", "COMUNIDAD VALENCIANA", null, null ),
	TAUCOMAU_11( "11", "EXTREMADURA", null, null ),
	TAUCOMAU_12( "12", "GALICIA", null, null ),
	TAUCOMAU_13( "13", "MADRID", null, null ),
	TAUCOMAU_14( "14", "MURCIA", null, null ),
	TAUCOMAU_15( "15", "NAVARRA", null, null ),
	TAUCOMAU_16( "16", "PAIS VASCO", null, null ),
	TAUCOMAU_17( "17", "LA RIOJA", null, null ),
	TAUCOMAU_18( "18", "CEUTA", null, null ),
	TAUCOMAU_19( "19", "MELILLA", null, null ),
	TAUCOMAU_99( "99", "ESTADO ESPAÑOL", null, null ),
	;
	public static final String TABLE_NAME = "TAUCOMAU";
	public static final String TABLE_DESCRIPTION = " TAUCOMAU	COMUNIDAD AUTÓNOMA				";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TAUCOMAU( String code, String description, String startDate, String endDate ) {
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

	public static TAUCOMAU getEnumByValue(String expression) {
		for( TAUCOMAU o : TAUCOMAU.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}