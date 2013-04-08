package com.esferalia.aon.payroll.contrata.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) THPCOLFO table codes.
 * Generation main class: com.esferalia.aon.payroll.contrata.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * *THPCOLFO	COLECTIVOS CONTRATOS DE FORMACIÓN				26-07-2012
 *  ------------------------------------------------------------------------
 */ 
public enum THPCOLFO {

	THPCOLFO_01( "01", "DESEMPLEADO MINUSVALIDO", "20010304", "0" ),
	THPCOLFO_02( "02", "EXTRANJERO 2 PROS.AÑOS.PERMISO TRABAJO", "20010304", "20060614" ),
	THPCOLFO_03( "03", "DESEMPLEADO + 3 AÑOS SIN ACTIV. LABORAL", "20010304", "20060614" ),
	THPCOLFO_04( "04", "DESEMPLEADO EN SITUACION DE EXCL.SOCIAL", "20010304", "20060614" ),
	THPCOLFO_05( "05", "CONTRATOS ESC.TALLER, C.OFICIO,T.EMPLEO", "20010304", "0" ),
	THPCOLFO_06( "06", "CONTRATOS ESCUELA TALLER Y CASAS OFICIO", "20060701", "0" ),
	THPCOLFO_07( "07", "CONTRATOS TALLERES DE EMPLEO", "20060701", "0" ),
	THPCOLFO_08( "08", "ENTRE 25 y 30 AÑOS SIN CUALIFICACION PROFESIONAL", "20110831", "20120211" ),
	THPCOLFO_09( "09", "MENORES DE 30 AÑOS MIENTRAS TASA DE DESEMPLEO > 15%", "20120212", "0" ),
	THPCOLFO_10( "10", "CONTRATO EXCLUÍDO SOCIAL EN EMPRESA INSERCIÓN", "20120708", "0" ),
	;
	public static final String TABLE_NAME = "THPCOLFO";
	public static final String TABLE_DESCRIPTION = "*THPCOLFO	COLECTIVOS CONTRATOS DE FORMACIÓN				26-07-2012";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	THPCOLFO( String code, String description, String startDate, String endDate ) {
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

	public static THPCOLFO getEnumByValue(String expression) {
		for( THPCOLFO o : THPCOLFO.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}