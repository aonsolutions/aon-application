package com.esferalia.aon.payroll.enumeration.ss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.esferalia.aon.payroll.sepe.SSCodeTablesWriter.ISSEnum;
import org.apache.commons.lang.time.DateUtils;

/** 
 * Enumeration for represent SOCIAL SECURITY T68 table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.SSCodeTablesWriter
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * T68.txt
 *  ------------------------------------------------------------------------
 */ 
public enum T68 implements ISSEnum {

	T68_01( "01", "Falta de concurrencias de requisitos", null, null ),
	T68_02( "02", "Empresario deudor a la Seguridad Social", null, null ),
	T68_03( "03", "Alta en la empresa 24 meses previos con contrato indefinido", null, null ),
	T68_04( "04", "Alta empresa 6 meses previos contrato temporal", null, null ),
	T68_05( "05", "Alta 3 meses previos contrato indefinido", null, null ),
	T68_06( "06", "Administración Pública", null, null ),
	T68_07( "07", "Inexistencia contrato temporal previo", null, null ),
	T68_08( "08", "Extinción contrato bonificado últimos 12 meses", null, null ),
	T68_09( "09", "Empresa sin la condición de inserción", null, null ),
	T68_11( "11", "Deudor a la fecha de inicio del derecho", null, null ),
	T68_12( "12", "Alta posterior plazo reglamentario ingreso", null, null ),
	T68_20( "20", "Incumplimiento obligaciones tributarias", null, null ),
	T68_21( "21", "Deudas tributarias", null, null ),
	T68_22( "22", "Incumplimiento obligaciones tributarias + deudas tributarias", null, null ),
	T68_23( "23", "Baja censal detectada", null, null ),
	T68_24( "24", "Delito fiscal", null, null ),
	T68_25( "25", "Incumplimiento obligaciones tributarias + deuda tributaria + delito fiscal", null, null ),
	T68_26( "26", "Incumplimiento obligaciones tributarias + delito fiscal", null, null ),
	T68_27( "27", "Deudas tributarias + delito fiscal", null, null ),
	T68_28( "28", "Incumplimiento obligaciones tributarias + deuda tributaria + baja censal", null, null ),
	T68_29( "29", "Deudas tributarias + baja censal", null, null ),
	T68_30( "30", "Delito fiscal + baja censal", null, null ),
	T68_31( "31", "Incumplimiento obligaciones tributarias + deuda tributaria + delito fiscal + baja censal", null, null ),
	T68_32( "32", "Incumplimiento obligaciones tributarias + delito fiscal + baja censal", null, null ),
	T68_33( "33", "Deuda tributaria + delito fiscal + baja censal", null, null ),
	T68_34( "34", "Incumplimiento obligaciones tributarias + deuda tributaria + baja censal", null, null ),
	T68_40( "40", "No acredita derecho s/SPEE", null, null ),
	T68_41( "41", "Incumplimiento mantenimiento empleo", null, null ),
	T68_42( "42", "No aplicación condicio. C.E.E. - Subrogación de contrato", null, null ),
	T68_43( "43", "Plantilla trab. excede a la de deducción", null, null ),
	T68_45( "45", "Incumplimiento mantenimient. nivel empleo", null, null ),
	T68_46( "46", "Decisión extintiva improcedente/6 meses", null, null ),
	T68_48( "48", "Sanción accesoria infracción LISOS", null, null ),
	;
	public static final String TABLE_NAME = "T68";
	public static final String TABLE_DESCRIPTION = "T68.txt";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	T68( String code, String description, String startDate, String endDate ) {
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

	public static T68 getEnumByValue(String expression) {
		for( T68 o : T68.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}