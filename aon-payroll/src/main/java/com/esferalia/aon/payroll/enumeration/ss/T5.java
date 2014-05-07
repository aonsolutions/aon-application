package com.esferalia.aon.payroll.enumeration.ss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.esferalia.aon.payroll.sepe.SSCodeTablesWriter.ISSEnum;
import org.apache.commons.lang.time.DateUtils;

/** 
 * Enumeration for represent SOCIAL SECURITY T5 table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.SSCodeTablesWriter
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * T5.txt
 *  ------------------------------------------------------------------------
 */ 
public enum T5 implements ISSEnum {

	T5_L00( "L00", "Normal", null, null ),
	T5_L02( "L02", "Complementaria por salarios tramitación normal (Existencia de sentencia)", null, null ),
	T5_L03( "L03", "Complementaria abono salarios con carácter retroactivo (excepto 02) - Incremento de bases y/o salarios. Por ejemplo: atrasos de convenio, etc. (Ver ref. 1610 fecha de control).", null, null ),
	T5_L04( "L04", "Complementaria por reintegro de prestaciones y/o deducciones - Prestaciones y/o deducciones aplicadas indebidamente", null, null ),
	T5_L09( "L09", "Otras complementarias", null, null ),
	T5_L10( "L10", "Expediente de regulación empleo (parcial)", "0", "20030301" ),
	T5_L11( "L11", "Expediente de regulación empleo (total)", "0", "20030301" ),
	T5_L12( "L12", "Regularización anual ( Artistas, Toreros y Sist. Esp. Resina) - Futuro uso", "99990101", null ),
	T5_L13( "L13", "Vacaciones retribuidas y no disfrutadas", null, null ),
	T5_L15( "L15", "Rectificaciones compensación y/o deducción - Futuro uso", "99990101", null ),
	T5_A70( "A70", "Admones. publicas. Personal funcionario con permiso sin sueldo", null, null ),
	T5_A71( "A71", "Admones. publicas. susp. sin retrib.", null, null ),
	T5_A72( "A72", "Admones. publicas. serv. militar o p.s.s., sin trienios o con trienios aportación empresarial Baja A73 Admones. publicas. serv. militar o p.s.s., con trienios aportación del trabajador Baja", null, null ),
	T5_A74( "A74", "Admones. publicas. susp. no firme definitivamente. Aportación empresa resto contingencias.", null, null ),
	T5_A75( "A75", "Admones. publicas. susp. no firmes definitivamente. Aportación trabajador todas contingencias", null, null ),
	T5_A76( "A76", "Admones. públicas liquidación por compensación", null, null ),
	T5_TP2( "TP2", "Pluriempleo. Liquidación por protección no común", null, null ),
	;
	public static final String TABLE_NAME = "T5";
	public static final String TABLE_DESCRIPTION = "T5.txt";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	T5( String code, String description, String startDate, String endDate ) {
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

	public static T5 getEnumByValue(String expression) {
		for( T5 o : T5.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}