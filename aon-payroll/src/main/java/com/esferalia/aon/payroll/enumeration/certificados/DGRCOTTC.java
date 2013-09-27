package com.esferalia.aon.payroll.enumeration.certificados;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.esferalia.aon.payroll.sepe.SEPECodeTablesWriter.ISepeEnum;
import org.apache.commons.lang.time.DateUtils;

/** 
 * Enumeration for represent Certific@2 (S.E.P.E.) DGRCOTTC table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.CertificadosCodeTablesWriter
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * No description found
 *  ------------------------------------------------------------------------
 */ 
public enum DGRCOTTC implements ISepeEnum {

	DGRCOTTC_01( "01", "DIRECTORES, INGENIEROS", null, null ),

	DGRCOTTC_02( "02", "PERITOS Y AYUDANTES", null, null ),

	DGRCOTTC_03( "03", "JEFES ADMINISTRATIVOS", null, null ),

	DGRCOTTC_04( "04", "AYUDANTES NO TITULADOS", null, null ),

	DGRCOTTC_05( "05", "OFICIALES ADMINISTRATIVOS", null, null ),

	DGRCOTTC_06( "06", "SUBALTERNOS", null, null ),

	DGRCOTTC_07( "07", "AUXILIARES ADMINISTRATIVOS", null, null ),

	DGRCOTTC_08( "08", "OFICIALES DE PRIMERA", null, null ),

	DGRCOTTC_09( "09", "OFICIALES DE TERCERA", null, null ),

	DGRCOTTC_10( "10", "PEONES", null, null ),

	DGRCOTTC_11( "11", "APRENDICES 17 AÑOS", null, null ),

	DGRCOTTC_12( "12", "APRENDICES < 17 AÑOS", null, null ),
	;
	public static final String TABLE_NAME = "DGRCOTTC";
	public static final String TABLE_DESCRIPTION = "No description found";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	DGRCOTTC( String code, String description, String startDate, String endDate ) {
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

	public static DGRCOTTC getEnumByValue(String expression) {
		for( DGRCOTTC o : DGRCOTTC.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}