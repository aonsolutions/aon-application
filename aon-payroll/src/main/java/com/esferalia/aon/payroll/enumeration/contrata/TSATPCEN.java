package com.esferalia.aon.payroll.enumeration.contrata;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.esferalia.aon.payroll.enumeration.IPayrollTablesEnum;
import org.apache.commons.lang.time.DateUtils;

/** 
 * Enumeration for represent Contrat@ (S.E.P.E.) TSATPCEN table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.ContrataCodeTablesWriter
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * *TSATPCEN	TIPOS DE CENTRO							22-03-2012
 *  ------------------------------------------------------------------------
 */ 
public enum TSATPCEN implements IPayrollTablesEnum {

	TSATPCEN_E( "E", "CENTRO DEL SISTEMA EDUCATIVO", "20110831", null ),
	TSATPCEN_C( "C", "CENTRO ACREDITADO POR LA COMUNIDAD AUTÓNOMA", "20110831", null ),
	TSATPCEN_S( "S", "CENTRO ACREDITADO POR EL SEPE", "20110831", null ),
	;
	public static final String TABLE_NAME = "TSATPCEN";
	public static final String TABLE_DESCRIPTION = "*TSATPCEN	TIPOS DE CENTRO							22-03-2012";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TSATPCEN( String code, String description, String startDate, String endDate ) {
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

	public static TSATPCEN getEnumByValue(String expression) {
		for( TSATPCEN o : TSATPCEN.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}