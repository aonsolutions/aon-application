package com.esferalia.aon.payroll.enumeration.contrata;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.esferalia.aon.payroll.sepe.SEPECodeTablesWriter.ISepeEnum;
import org.apache.commons.lang.time.DateUtils;

/** 
 * Enumeration for represent Contrat@ (S.E.P.E.) TEUECCLL table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.ContrataCodeTablesWriter
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 *  TEUECCLL	CORPORACIONES LOCALES				
 *  ------------------------------------------------------------------------
 */ 
public enum TEUECCLL implements ISepeEnum {

	TEUECCLL_1( "1", "AYUNTAMIENTOS", null, null ),
	TEUECCLL_2( "2", "DIPUTACIONES Y CABILDOS", null, null ),
	TEUECCLL_3( "3", "ENTIDADES DEPENDIENTES DE CORP.LOCALES", null, null ),
	TEUECCLL_4( "4", "OTRAS ENTIDADES LOCALES", null, null ),
	TEUECCLL_5( "5", "MANCOMUNIDADES Y COMARCAS", null, null ),
	;
	public static final String TABLE_NAME = "TEUECCLL";
	public static final String TABLE_DESCRIPTION = " TEUECCLL	CORPORACIONES LOCALES				";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TEUECCLL( String code, String description, String startDate, String endDate ) {
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

	public static TEUECCLL getEnumByValue(String expression) {
		for( TEUECCLL o : TEUECCLL.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}