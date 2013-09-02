package com.esferalia.aon.payroll.contrata.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * Enumeration for represent Contrat@ (S.E.P.E.) TEVACTCL table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.ContrataCodeTablesWriter
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 *  TEVACTCL	ACTUACIONES DE CORPORACIONES LOCALES		
 *  ------------------------------------------------------------------------
 */ 
public enum TEVACTCL {

	TEVACTCL_A( "A", "ACTUACIONES ORDINARIAS", null, null ),
	TEVACTCL_B( "B", "ACTUACIONES EXTRAORDINARIAS, PIEC", null, null ),
	TEVACTCL_C( "C", "COMPLEMENTOS RENTAS (AEPSA)", null, null ),
	TEVACTCL_D( "D", "AEPSA (GENERADORES EMPLEO, ZRD, ARAGON)", null, null ),
	TEVACTCL_E( "E", "ACTUAC. ESPEC (SERV.INTEGR. EMPLEO)", null, null ),
	;
	public static final String TABLE_NAME = "TEVACTCL";
	public static final String TABLE_DESCRIPTION = " TEVACTCL	ACTUACIONES DE CORPORACIONES LOCALES		";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TEVACTCL( String code, String description, String startDate, String endDate ) {
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

	public static TEVACTCL getEnumByValue(String expression) {
		for( TEVACTCL o : TEVACTCL.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}