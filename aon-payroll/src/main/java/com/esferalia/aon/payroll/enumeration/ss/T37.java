package com.esferalia.aon.payroll.enumeration.ss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.esferalia.aon.payroll.enumeration.IPayrollTablesEnum;
import org.apache.commons.lang.time.DateUtils;

/** 
 * Enumeration for represent SOCIAL SECURITY T37 table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.SSCodeTablesWriter
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * T37.txt
 *  ------------------------------------------------------------------------
 */ 
public enum T37 implements IPayrollTablesEnum {

	T37_1( "1", "Desempleado inscrito en la oficina de empleo.", null, null ),
	T37_2( "2", "Desempleado inscrito en la oficina de empleo durante más de 12 meses", null, null ),
	T37_3( "3", "Desempleado subsidio REA", null, null ),
	T37_4( "4", "Beneficiario prestación desempleo - contributiva o asistencial - durante más de un año.", null, null ),
	T37_5( "5", "Desempleado inscrito en la oficina de empleo durante más de 6 meses", null, null ),
	T37_6( "6", "Beneficiario prestación desempleo - contributiva o asistencial - al que falta un año o más de percepción de la prestación.", null, null ),
	T37_7( "7", "Beneficiario subsidio desempleo. Mayor de 52 años.", null, null ),
	T37_8( "8", "Desempleado excedente sector textil falta 1 año de percepción de la prestación", null, null ),
	T37_T( "T", "Desempleado excedente sector textil", null, null ),
	T37_U( "U", "Desempleado + 6 meses excedente sector textil", null, null ),
	T37_F( "F", "Desempleado inscrito en la oficina de empleo. Carga familiar", null, null ),
	T37_G( "G", "Desempleado inscrito en la oficina de empleo + 6 meses. Carga familiar", null, null ),
	T37_A( "A", "Beneficiario Prestación Desempleo", null, null ),
	T37_M( "M", "Desempleado contrato de trabajo CTP <333", null, null ),
	T37_B( "B", "Desempleado con problemas de empleabilidad", null, null ),
	T37_C( "C", "Desempleado 01.01.2011", null, null ),
	T37_D( "D", "Desempleado 01.01.2011 12 meses en 18 meses", null, null ),
	T37_E( "E", "Desempleado 16.08.2011", null, null ),
	T37_H( "H", "Desempleado sin experiencia laboral o inferior a 3 meses", null, null ),
	T37_I( "I", "Desempleado procedente de otro sector de actividad", null, null ),
	T37_J( "J", "Desempleado sin experiencia laboral o inferior a 3 meses. Empleo joven", null, null ),
	T37_K( "K", "Desempleado sin título oficial enseñanza", null, null ),
	;
	public static final String TABLE_NAME = "T37";
	public static final String TABLE_DESCRIPTION = "T37.txt";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	T37( String code, String description, String startDate, String endDate ) {
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

	public static T37 getEnumByValue(String expression) {
		for( T37 o : T37.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}