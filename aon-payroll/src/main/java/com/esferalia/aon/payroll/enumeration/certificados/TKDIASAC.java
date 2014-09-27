package com.esferalia.aon.payroll.enumeration.certificados;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.esferalia.aon.payroll.enumeration.IPayrollTablesEnum;
import org.apache.commons.lang.time.DateUtils;

/** 
 * Enumeration for represent Certific@2 (S.E.P.E.) TKDIASAC table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.CertificadosCodeTablesWriter
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * No description found
 *  ------------------------------------------------------------------------
 */ 
public enum TKDIASAC implements IPayrollTablesEnum {

	TKDIASAC_01( "01", "SIN ACTIVIDAD", null, null ),
	TKDIASAC_02( "02", "SIN ACTIVIDAD POR NO ACUDIR AL LLAMAMIENTO", null, null ),
	TKDIASAC_03( "03", "DIAS DE ACTIVIDAD", null, null ),
	TKDIASAC_04( "04", "I.T., MATERNIDAD", null, null ),
	TKDIASAC_05( "05", "VACACIONES Y DESCANSOS RETRIBUIDOS", null, null ),
	;
	public static final String TABLE_NAME = "TKDIASAC";
	public static final String TABLE_DESCRIPTION = "No description found";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TKDIASAC( String code, String description, String startDate, String endDate ) {
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

	public static TKDIASAC getEnumByValue(String expression) {
		for( TKDIASAC o : TKDIASAC.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}