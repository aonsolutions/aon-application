package com.esferalia.aon.payroll.contrata.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) TEIINTER table codes.
 * Generation main class: com.esferalia.aon.payroll.contrata.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * *TEIINTER	CAUSA OBJETO DE LA INTERINIDAD					
 *  ------------------------------------------------------------------------
 */ 
public enum TEIINTER {

	TEIINTER_A( "A", "TRABAJADOR CON DERECHO RESERVA DE PUESTO", "19800315", "0" ),
	TEIINTER_B( "B", "TRABAJADOR POR MATERNIDAD SIN BONIFICACION DE CUOTAS", "19800315", "0" ),
	TEIINTER_C( "C", "EXCEDENCIA CUIDADO HIJO PERCEPTOR PRESTA", "19950501", "19991106" ),
	TEIINTER_D( "D", "EXCEDENCIA CUIDADO HIJO NO PERCEP.PRESTA", "19950501", "19970516" ),
	TEIINTER_E( "E", "TRABAJADOR PROCESO DE SELECCION/PROMOCION", "19800315", "0" ),
	TEIINTER_F( "F", "MATERNIDAD CON BONIFICACION DE CUOTAS", "19980906", "0" ),
	TEIINTER_G( "G", "ADOPCION", "19980906", "0" ),
	TEIINTER_H( "H", "ACOGIMIENTO", "19980906", "0" ),
	TEIINTER_I( "I", "RIESGO DURANTE EMBARAZO", "19991107", "0" ),
	TEIINTER_J( "J", "TRABAJ.EN FORMACION POR PERCEPTOR PRESTA", "20020526", "0" ),
	TEIINTER_K( "K", "MINUSVALIDOS DESEMPLEADOS POR MINUSV.INCAP.TEMP", "20021214", "0" ),
	TEIINTER_L( "L", "EXCEDENCIA CUIDADO FAMILIAR PERCEP.PREST", "19991107", "0" ),
	TEIINTER_M( "M", "SUSTITUCIÓN VÍCTIMAS VIOLENCIA DE GÉNERO", "20050128", "0" ),
	TEIINTER_N( "N", "PATERNIDAD", "20070324", "0" ),
	TEIINTER_O( "O", "RIESGO DURANTE LA LACTANCIA NATURAL", "20070324", "0" ),
	;
	public static final String TABLE_NAME = "TEIINTER";
	public static final String TABLE_DESCRIPTION = "*TEIINTER	CAUSA OBJETO DE LA INTERINIDAD					";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TEIINTER( String code, String description, String startDate, String endDate ) {
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

	public static TEIINTER getEnumByValue(String expression) {
		for( TEIINTER o : TEIINTER.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}