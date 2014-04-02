package com.esferalia.aon.payroll.enumeration.ss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.esferalia.aon.payroll.sepe.SSCodeTablesWriter.ISSEnum;
import org.apache.commons.lang.time.DateUtils;

/** 
 * Enumeration for represent SOCIAL SECURITY T33 table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.SSCodeTablesWriter
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * T33.txt
 *  ------------------------------------------------------------------------
 */ 
public enum T33 implements ISSEnum {

	T33_4( "4", "Asistencia sanitaria de Administraciones Públicas", null, null ),
	T33_8( "8", "Cotización adicional Ex.-MUNPAL", null, null ),
	T33_6( "6", "Contratación inferior a 7 días - Para contratos de duración efectiva inferior de 7 días, a los que es de aplicación el incremento del 36% de la cotización empresarial por contingencias comunes, establecido en la Ley 12/2001", null, null ),
	T33_14( "14", "Cotización adicional Ex-Munpal y contratación inferior a 7 días - Se utilizará cuando coincida la cotización adicional por clave 8 y por clave 6", null, null ),
	T33_15( "15", "Cotización adicional Bomberos al servicio de las Administraciones y Organismos Públicos", null, null ),
	;
	public static final String TABLE_NAME = "T33";
	public static final String TABLE_DESCRIPTION = "T33.txt";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	T33( String code, String description, String startDate, String endDate ) {
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

	public static T33 getEnumByValue(String expression) {
		for( T33 o : T33.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}