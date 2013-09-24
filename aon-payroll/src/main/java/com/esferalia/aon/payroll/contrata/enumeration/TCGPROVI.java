package com.esferalia.aon.payroll.contrata.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

/** 
 * Enumeration for represent Contrat@ (S.E.P.E.) TCGPROVI table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.ContrataCodeTablesWriter
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 *  TCGPROVI	PROVINCIA
 *  ------------------------------------------------------------------------
 */ 
public enum TCGPROVI {

	TCGPROVI_01( "01", "ALAVA", null, null ),
	TCGPROVI_02( "02", "ALBACETE", null, null ),
	TCGPROVI_03( "03", "ALICANTE", null, null ),
	TCGPROVI_04( "04", "ALMERIA", null, null ),
	TCGPROVI_05( "05", "AVILA", null, null ),
	TCGPROVI_06( "06", "BADAJOZ", null, null ),
	TCGPROVI_07( "07", "ILLES BALEARS", null, null ),
	TCGPROVI_08( "08", "BARCELONA", null, null ),
	TCGPROVI_09( "09", "BURGOS", null, null ),
	TCGPROVI_10( "10", "CACERES", null, null ),
	TCGPROVI_11( "11", "CADIZ", null, null ),
	TCGPROVI_12( "12", "CASTELLON", null, null ),
	TCGPROVI_13( "13", "CIUDAD REAL", null, null ),
	TCGPROVI_14( "14", "CORDOBA", null, null ),
	TCGPROVI_15( "15", "A CORUÑA", null, null ),
	TCGPROVI_16( "16", "CUENCA", null, null ),
	TCGPROVI_17( "17", "GIRONA", null, null ),
	TCGPROVI_18( "18", "GRANADA", null, null ),
	TCGPROVI_19( "19", "GUADALAJARA", null, null ),
	TCGPROVI_20( "20", "GUIPUZCOA", null, null ),
	TCGPROVI_21( "21", "HUELVA", null, null ),
	TCGPROVI_22( "22", "HUESCA", null, null ),
	TCGPROVI_23( "23", "JAEN", null, null ),
	TCGPROVI_24( "24", "LEON", null, null ),
	TCGPROVI_25( "25", "LLEIDA", null, null ),
	TCGPROVI_26( "26", "LA RIOJA", null, null ),
	TCGPROVI_27( "27", "LUGO", null, null ),
	TCGPROVI_28( "28", "MADRID", null, null ),
	TCGPROVI_29( "29", "MALAGA", null, null ),
	TCGPROVI_30( "30", "MURCIA", null, null ),
	TCGPROVI_31( "31", "NAVARRA", null, null ),
	TCGPROVI_32( "32", "OURENSE", null, null ),
	TCGPROVI_33( "33", "ASTURIAS", null, null ),
	TCGPROVI_34( "34", "PALENCIA", null, null ),
	TCGPROVI_35( "35", "LAS PALMAS", null, null ),
	TCGPROVI_36( "36", "PONTEVEDRA", null, null ),
	TCGPROVI_37( "37", "SALAMANCA", null, null ),
	TCGPROVI_38( "38", "STA.CRUZ DE TENERIFE", null, null ),
	TCGPROVI_39( "39", "CANTABRIA", null, null ),
	TCGPROVI_40( "40", "SEGOVIA", null, null ),
	TCGPROVI_41( "41", "SEVILLA", null, null ),
	TCGPROVI_42( "42", "SORIA", null, null ),
	TCGPROVI_43( "43", "TARRAGONA", null, null ),
	TCGPROVI_44( "44", "TERUEL", null, null ),
	TCGPROVI_45( "45", "TOLEDO", null, null ),
	TCGPROVI_46( "46", "VALENCIA", null, null ),
	TCGPROVI_47( "47", "VALLADOLID", null, null ),
	TCGPROVI_48( "48", "VIZCAYA", null, null ),
	TCGPROVI_49( "49", "ZAMORA", null, null ),
	TCGPROVI_50( "50", "ZARAGOZA", null, null ),
	TCGPROVI_51( "51", "CEUTA", null, null ),
	TCGPROVI_52( "52", "MELILLA", null, null ),
	TCGPROVI_60( "60", "SERVICIOS CENTRALES", null, null ),
	;
	public static final String TABLE_NAME = "TCGPROVI";
	public static final String TABLE_DESCRIPTION = " TCGPROVI	PROVINCIA";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TCGPROVI( String code, String description, String startDate, String endDate ) {
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

	public static TCGPROVI getEnumByValue(String expression) {
		for( TCGPROVI o : TCGPROVI.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}