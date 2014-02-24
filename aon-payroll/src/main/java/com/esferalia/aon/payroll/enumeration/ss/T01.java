package com.esferalia.aon.payroll.enumeration.ss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.esferalia.aon.payroll.sepe.SSCodeTablesWriter.ISSEnum;
import org.apache.commons.lang.time.DateUtils;

/** 
 * Enumeration for represent SOCIAL SECURITY T01 table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.SSCodeTablesWriter
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * T01.txt
 *  ------------------------------------------------------------------------
 */ 
public enum T01 implements ISSEnum {

	T01_( "", "Envío en real", null, null ),
	T01_P( "P", "Pruebas", null, null ),
	T01_N( "N", "No sustitución - Sólo afecta a las liquidaciones complementarias del tipo L02, L03, L09 (del mes en curso no), L13, TP2 y A76. Cuando venga consignada esta marca, no se procederá a la sustitución automática de estas liquidaciones por otras presentadas en el mismo período y con idéntico CCC, período de liquida-ción y tipo de liquidación.", null, null ),

	;
	public static final String TABLE_NAME = "T01";
	public static final String TABLE_DESCRIPTION = "T01.txt";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	T01( String code, String description, String startDate, String endDate ) {
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

	public static T01 getEnumByValue(String expression) {
		for( T01 o : T01.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}