package com.esferalia.aon.payroll.enumeration.ss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.esferalia.aon.payroll.sepe.SSCodeTablesWriter.ISSEnum;
import org.apache.commons.lang.time.DateUtils;

/** 
 * Enumeration for represent SOCIAL SECURITY T49 table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.SSCodeTablesWriter
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * T49.txt
 *  ------------------------------------------------------------------------
 */ 
public enum T49 implements ISSEnum {

	T49_01( "01", "Bonificación INEM", null, null ),
	T49_02( "02", "Bonificación Hacienda embarcaciones Zona Especial de Canarias", null, null ),
	T49_03( "03", "Reducción", null, null ),
	T49_04( "04", "Incremento de tipos", null, null ),
	T49_05( "05", "Incremento de cuota", null, null ),
	T49_06( "06", "Decremento de tipos", null, null ),
	T49_07( "07", "Exoneración", null, null ),
	T49_08( "08", "Colaboración", null, null ),
	T49_09( "09", "Exclusiones", null, null ),
	T49_10( "10", "Decremento de cuotas", null, null ),
	T49_11( "11", "Cotización adicional", null, null ),
	T49_12( "12", "Reducción a cargo del INEM", null, null ),
	T49_13( "13", "Bonificación SPEE Prog Fomento de Empleo - Porcentaje", null, null ),
	T49_14( "14", "Diferimiento en el ingreso de cuotas", null, null ),
	T49_15( "15", "Aportación no obligatoria. Subs. corto plazo - Identifica situaciones de IT con pago directo por parte de la entidad gestora o colaboradora, maternidad y riesgo durante el embarazo. Baja desde", null, null ),
	T49_16( "16", "Bonificación SPEE Prog Fomento de Empleo - Cuantía - Futuro uso.", null, null ),
	T49_17( "17", "Aportación no obligatoria - Suspensión Regulación Empleo - Futuro uso.", null, null ),
	T49_18( "18", "Aportación no obligatoria - Suspensión Regulación Empleo Parcial - Futuro uso.", null, null ),
	T49_19( "19", "Bonificación SPEE con cargo a Hacienda", null, null ),
	T49_20( "20", "Período sin reribución", null, null ),
	T49_21( "21", "IT.CC.Pago delegado", null, null ),
	T49_22( "22", "IT.CC.Pago directo", null, null ),
	T49_23( "23", "IT.AT.Pago delegado", null, null ),
	T49_24( "24", "IT.AT.Pago directo", null, null ),
	T49_25( "25", "IT.CC.Pago delegado diferido", null, null ),
	T49_26( "26", "IT.CC.Pago delegado diferido mes anterior", null, null ),
	T49_27( "27", "IT.AT.Pago delegado diferido", null, null ),
	T49_28( "28", "IT.AT.Pago delegado diferido mes anterior.", null, null ),
	T49_29( "29", "IT.CC.Colaboradoras excluidas 15 días", null, null ),
	T49_30( "30", "IT.AT.Colaboradoras excluidas", null, null ),
	T49_31( "31", "Maternidad/paternidad tiempo completo", null, null ),
	T49_32( "32", "Maternidad tiempo parcial", null, null ),
	T49_33( "33", "Paternidad tiempo parcial", null, null ),
	T49_34( "34", "Riesgo embarazo/lactancia", null, null ),
	T49_35( "35", "Funcionarios - permiso sin sueldo", null, null ),
	T49_36( "36", "Funcionarios - suspensión provisional", null, null ),
	T49_37( "37", "Exoneración E.R.E. derivado de fuerza mayor", null, null ),
	T49_38( "38", "Moratoria", null, null ),
	T49_39( "39", "Decremento BBCC", null, null ),
	T49_40( "40", "Tipo cotización especial. SEA", null, null ),
	T49_41( "41", "Bonificación Programa Fomento de Empleo. Cuantía diaria", null, null ),
	T49_42( "42", "Red.Cuota SS - cuantía", null, null ),
	T49_43( "43", "Tarifa plana", null, null ),
	T49_44( "44", "Cotización especial solidaridad", null, null ),
	;
	public static final String TABLE_NAME = "T49";
	public static final String TABLE_DESCRIPTION = "T49.txt";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	T49( String code, String description, String startDate, String endDate ) {
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

	public static T49 getEnumByValue(String expression) {
		for( T49 o : T49.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}