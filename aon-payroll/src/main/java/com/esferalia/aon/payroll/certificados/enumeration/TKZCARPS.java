package com.esferalia.aon.payroll.certificados.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * Enumeration for represent Certific@2 (S.E.P.E.) TKZCARPS table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.CertificadosCodeTablesWriter
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * No description found
 *  ------------------------------------------------------------------------
 */ 
public enum TKZCARPS {

	TKZCARPS_1( "1", "ALTO CARGO DE ADMINISTRACION GENERAL DEL ESTADO, NO FUNCIONARIO Y SIN DERECHO A INDEMNIZACION POR EL CESE", null, null ),
	TKZCARPS_2( "2", "CARGO REPRESENTANTE DE SINDICATO CONSTITUIDO AL AMPARO DE LA LEY 11/1985, DE 2 DE AGOSTO, DE LIBERTAD SINDICAL", null, null ),
	TKZCARPS_3( "3", "MIEMBRO DE CORPORACION LOCAL Y RESTO DE CARGOS SINDICALES CONTEMPLADOS EN LA LEY 11/1985 , 2 DEAGOSTO, DE LIBERTAD SINDICAL", null, null ),
	;
	public static final String TABLE_NAME = "TKZCARPS";
	public static final String TABLE_DESCRIPTION = "No description found";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TKZCARPS( String code, String description, String startDate, String endDate ) {
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

	public static TKZCARPS getEnumByValue(String expression) {
		for( TKZCARPS o : TKZCARPS.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}