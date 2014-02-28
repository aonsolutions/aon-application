package com.esferalia.aon.payroll.enumeration.ss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.esferalia.aon.payroll.sepe.SSCodeTablesWriter.ISSEnum;
import org.apache.commons.lang.time.DateUtils;

/** 
 * Enumeration for represent SOCIAL SECURITY T18 table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.SSCodeTablesWriter
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * T18.txt
 *  ------------------------------------------------------------------------
 */ 
public enum T18 implements ISSEnum {

	T18_1( "1", "Ingenieros y Licenciados. Personal de alta dirección no incluido en el art. 1.3.c) del Estatuto de los Trabajadores", null, null ),
	T18_2( "2", "Ingenieros Técnicos, Peritos y Ayudantes titulados", null, null ),
	T18_3( "3", "Jefes administrativos y de taller", null, null ),
	T18_4( "4", "Ayudantes no titulados", null, null ),
	T18_5( "5", "Oficiales administrativos", null, null ),
	T18_6( "6", "Subalternos", null, null ),
	T18_7( "7", "Auxiliares administrativos", null, null ),
	T18_8( "8", "Oficiales de Primera y Segunda", null, null ),
	T18_9( "9", "Oficiales de Tercera y Especialistas", null, null ),
	T18_10( "10", "Peones", null, null ),
	T18_11( "11", "Trabajadores menores de 18 años", null, null ),
	;
	public static final String TABLE_NAME = "T18";
	public static final String TABLE_DESCRIPTION = "T18.txt";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	T18( String code, String description, String startDate, String endDate ) {
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

	public static T18 getEnumByValue(String expression) {
		for( T18 o : T18.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}