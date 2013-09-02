package com.esferalia.aon.payroll.certificados.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * Enumeration for represent Certific@2 (S.E.P.E.) TKCSITEM table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.CertificadosCodeTablesWriter
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * No description found
 *  ------------------------------------------------------------------------
 */ 
public enum TKCSITEM {

	TKCSITEM_01( "01", "PERIODOS DE ACTIVIDAD DE TRABAJADOR FIJO DISCONTINUO POR FINALIZACION DE CAMPAÑA", null, null ),
	TKCSITEM_02( "02", "PERIODOS DE ACTIVIDAD DE TRABAJADOR FIJO DISCONTINUO Y FINALIZACION POR EXCEDENCIA DURANTE LA CAMPAÑA", null, null ),
	TKCSITEM_03( "03", "PERIODOS DE ACTIVIDAD DE TRABAJADOR FIJO DISCONTINUO DURANTE LA CAMPAÑA", null, null ),
	TKCSITEM_04( "04", "PERIODOS DE ACTIVIDAD DE TRABAJADOR FIJO DISCONTINUO POR INICIO DE CAMPAÑA", null, null ),
	TKCSITEM_05( "05", "FINALIZACIàN DE CAMPA¥A ANUAL", null, null ),

	;
	public static final String TABLE_NAME = "TKCSITEM";
	public static final String TABLE_DESCRIPTION = "No description found";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TKCSITEM( String code, String description, String startDate, String endDate ) {
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

	public static TKCSITEM getEnumByValue(String expression) {
		for( TKCSITEM o : TKCSITEM.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}