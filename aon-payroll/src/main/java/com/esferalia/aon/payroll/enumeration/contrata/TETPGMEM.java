package com.esferalia.aon.payroll.enumeration.contrata;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.esferalia.aon.payroll.enumeration.IPayrollTablesEnum;
import org.apache.commons.lang.time.DateUtils;

/** 
 * Enumeration for represent Contrat@ (S.E.P.E.) TETPGMEM table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.ContrataCodeTablesWriter
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 *  TETPGMEM	PROGRAMA DE EMPLEO				
 *  ------------------------------------------------------------------------
 */ 
public enum TETPGMEM implements IPayrollTablesEnum {

	TETPGMEM_01( "01", "FOMENTO EMPLEO AGRARIO", null, null ),
	TETPGMEM_02( "02", "INSERCIÓN CORPORACIÓN LOCAL", null, null ),
	TETPGMEM_03( "03", "INSERCIÓN (ÓRGANOS ADMINISTRAC. ESTADO)", null, null ),
	TETPGMEM_04( "04", "INSERCIÓN (COMUNIDAD AUTÓNOMA)", null, null ),
	TETPGMEM_05( "05", "INSERCIÓN (ENTIDAD SIN ANIMO DE LUCRO)", null, null ),
	TETPGMEM_06( "06", "INSERCIÓN (UNIVERSIDAD)", null, null ),
	TETPGMEM_07( "07", "SUBSIDIO AGRARIO(ORGANISMOS INVERSORES)", null, null ),
	TETPGMEM_08( "08", "AGENTES DE EMPLEO Y DESARROLLO LOCAL", null, null ),
	TETPGMEM_09( "09", "ESTUDIOS Y CAMPAÑAS", null, null ),
	TETPGMEM_10( "10", "PROGRAMA DE EMPLEO I+E", null, null ),
	TETPGMEM_12( "12", "INTERES SOCIAL (CORPORACION LOCAL)", null, null ),
	TETPGMEM_13( "13", "INTERES SOCIAL (ORGANOS AD.ESTADO O CCAA)", null, null ),
	TETPGMEM_14( "14", "INTERES SOCIAL (COMUNIDAD AUTONOMA)", null, null ),
	TETPGMEM_15( "15", "INTERES SOCIAL (ENTIDAD SIN ANIMO DE LUCRO)", null, null ),
	TETPGMEM_16( "16", "INTERES SOCIAL (UNIVERSIDAD)", null, null ),
	;
	public static final String TABLE_NAME = "TETPGMEM";
	public static final String TABLE_DESCRIPTION = " TETPGMEM	PROGRAMA DE EMPLEO				";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TETPGMEM( String code, String description, String startDate, String endDate ) {
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

	public static TETPGMEM getEnumByValue(String expression) {
		for( TETPGMEM o : TETPGMEM.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}