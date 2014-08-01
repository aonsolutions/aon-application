package com.esferalia.aon.payroll.enumeration.ss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.esferalia.aon.payroll.sepe.SSCodeTablesWriter.ISSEnum;
import org.apache.commons.lang.time.DateUtils;

/** 
 * Enumeration for represent SOCIAL SECURITY T2 table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.SSCodeTablesWriter
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * T2.txt
 *  ------------------------------------------------------------------------
 */ 
public enum T2 implements ISSEnum {

	T2_0111( "0111", "Régimen General. - Colectivo general, Personal de vuelo y Estatuto minero.", null, null ),
	T2_0112( "0112", "Régimen General. Artistas.", null, null ),
	T2_0114( "0114", "Régimen General. Profesionales taurinos. - Futuro uso.", null, null ),
	T2_0131( "0131", "Régimen General. Sistema especial Resina - Futuro uso.", null, null ),
	T2_0132( "0132", "Régimen General. Sistema especial de Frutas, Hortalizas y conservas Vegetales", null, null ),
	T2_0134( "0134", "Régimen General. Sistema especial manipulado y empaquetado tomate fresco.", null, null ),
	T2_0135( "0135", "Régimen General. Sistema especial Industria Hostelera", null, null ),
	T2_0136( "0136", "Régimen General. Sistema especial trabajadores fijos discontinuos en empresas de exhibición de cine y salas de bailes, discotecas y salas fiesta", null, null ),
	T2_0137( "0137", "Régimen General. Sistema especial de trabajadores discontinuos en empresas de estudios de mercado y opinión publica.", null, null ),
	T2_0138( "0138", "Régimen General. Sistema Especial para Empleados de Hogar.", null, null ),
	T2_0163( "0163", "Régimen General. Sistema Especial Agrario. Actividad en CCC.", null, null ),
	T2_0613( "0613", "Régimen Especial Agrario (Jornadas Reales)", null, null ),
	T2_0811( "0811", "Régimen Especial de los trabajadores del Mar, Trabajadores por cuenta ajena grupo 1.", null, null ),
	T2_0812( "0812", "Régimen Especial de los trabajadores del Mar. Trabajadores por cuenta ajena grupo 2A.", null, null ),
	T2_0813( "0813", "Régimen Especial de los trabajadores del Mar. Trabajadores por cuenta ajena grupo 2B", null, null ),
	T2_0814( "0814", "Régimen Especial de los trabajadores del Mar. Trabajadores por cuenta ajena grupo 3 .", null, null ),
	T2_0821( "0821", "Régimen Especial de los trabajadores del Mar. Armadores y asimilados por cuenta ajena Grupo 1.", null, null ),
	T2_0822( "0822", "Régimen Especial Trabajadores del Mar. Armadores y asimilados por cuenta ajena Grupo 2A.", null, null ),
	T2_0823( "0823", "Régimen Especial Trabajadores del Mar. Armadores y asimilados por cuenta ajena Grupo 2B", null, null ),
	T2_0911( "0911", "Minería del carbón", null, null ),
	T2_3011( "3011", "Asistencia Sanitaria Concertada", null, null ),
	;
	public static final String TABLE_NAME = "T2";
	public static final String TABLE_DESCRIPTION = "T2.txt";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	T2( String code, String description, String startDate, String endDate ) {
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

	public static T2 getEnumByValue(String expression) {
		for( T2 o : T2.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}