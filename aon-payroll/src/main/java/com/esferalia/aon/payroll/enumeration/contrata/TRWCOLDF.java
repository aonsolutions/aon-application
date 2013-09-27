package com.esferalia.aon.payroll.enumeration.contrata;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.esferalia.aon.payroll.sepe.SEPECodeTablesWriter.ISepeEnum;
import org.apache.commons.lang.time.DateUtils;

/** 
 * Enumeration for represent Contrat@ (S.E.P.E.) TRWCOLDF table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.ContrataCodeTablesWriter
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * *TRWCOLDF	COLECTIVO DE DEDUCCION FISCAL					29-02-2012
 *  ------------------------------------------------------------------------
 */ 
public enum TRWCOLDF implements ISepeEnum {

	TRWCOLDF_01( "01", "PRIMER CONTRATO CON TRABAJADOR MENOR DE 30 AÑOS", "20120212", null ),
	TRWCOLDF_02( "02", "DESEMPLEADO BENEFICIARIO DE PRESTACIÓN CONTRIBUTIVA", "20120212", null ),
	TRWCOLDF_03( "03", "CONTRATO SIN DEDUCCIÓN FISCAL", "20120212", null ),
	;
	public static final String TABLE_NAME = "TRWCOLDF";
	public static final String TABLE_DESCRIPTION = "*TRWCOLDF	COLECTIVO DE DEDUCCION FISCAL					29-02-2012";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TRWCOLDF( String code, String description, String startDate, String endDate ) {
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
				return DateUtils.ceiling(sdf.parse(startDate), Calendar.DAY_OF_MONTH);
			}
		} catch (ParseException e) {
			// nothing to do
		}
		return null;
	}

	public Date getEndDate(){
		try {
			if(endDate!=null){
				return DateUtils.ceiling(sdf.parse(endDate), Calendar.DAY_OF_MONTH);
			}
		} catch (ParseException e) {
			// nothing to do
		}
		return null;
	}

	public boolean isActive(){
		Date now = new Date();
		now = DateUtils.ceiling(now, Calendar.DAY_OF_MONTH);
		if( (getStartDate()!=null && getStartDate().after(now)) || (getEndDate()!=null && getEndDate().before(now)) ){
			return false;
		}
		return true;
	}

	public static TRWCOLDF getEnumByValue(String expression) {
		for( TRWCOLDF o : TRWCOLDF.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}