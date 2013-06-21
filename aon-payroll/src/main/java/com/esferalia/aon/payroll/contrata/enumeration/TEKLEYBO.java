package com.esferalia.aon.payroll.contrata.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) TEKLEYBO table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * *TEKLEYBO	LEY BONIFICACIÓN						11-09-2012	
 *  ------------------------------------------------------------------------
 */ 
public enum TEKLEYBO {

	TEKLEYBO_01( "01", "LEY 64 / 1997", "19970517", "19990516" ),
	TEKLEYBO_02( "02", "LEY 50 / 1998", "19990101", "19991231" ),
	TEKLEYBO_03( "03", "LEY 55 / 1999", "20000101", "20010303" ),
	TEKLEYBO_04( "04", "REAL DECRETO LEY 5 / 2001", "20010304", "20010710" ),
	TEKLEYBO_05( "05", "REAL DECRETO LEY 1451 / 1983", "19830605", "20060630" ),
	TEKLEYBO_06( "06", "REAL DECRETO LEY 1368 / 1985", "19850718", "20060630" ),
	TEKLEYBO_07( "07", "LEY 42 / 1994", "19950101", "20060630" ),
	TEKLEYBO_08( "08", "REAL DECRETO LEY 11 / 1998", "19980906", "0" ),
	TEKLEYBO_09( "09", "LEY 39 / 1999", "19991107", "0" ),
	TEKLEYBO_10( "10", "LEY 12 / 2001", "20010711", "20011231" ),
	TEKLEYBO_11( "11", "LEY 24 / 2001", "20020101", "20021231" ),
	TEKLEYBO_12( "12", "REAL DECRETO LEY 5 / 2002", "20020526", "20021213" ),
	TEKLEYBO_13( "13", "LEY 45 / 2002", "20021214", "0" ),
	TEKLEYBO_14( "14", "LEY 53 / 2002", "20030101", "20031231" ),
	TEKLEYBO_15( "15", "LEY 62 / 2003", "20040101", "20041231" ),
	TEKLEYBO_16( "16", "REAL DECRETO LEY 1 / 1995", "19950501", "19991106" ),
	TEKLEYBO_17( "17", "LEY 2 / 2004", "20050101", "20051231" ),
	TEKLEYBO_18( "18", "LEY ORGÁNICA 1 / 2004", "20050128", "0" ),
	TEKLEYBO_19( "19", "LEY 35 / 2002", "20020713", "0" ),
	TEKLEYBO_20( "20", "LEY 30 / 2005", "20060101", "20060630" ),
	TEKLEYBO_21( "21", "REAL DECRETO 290 / 2004", "20040222", "20060630" ),
	TEKLEYBO_22( "22", "REAL DECRETO 63 / 2006", "20060204", "20120731" ),
	TEKLEYBO_23( "23", "REAL DECRETO LEY 5 / 2006", "20060701", "20061230" ),
	TEKLEYBO_24( "24", "ORDEN TAS / 3243 / 2006", "20061022", "20081231" ),
	TEKLEYBO_25( "25", "LEY 43 / 2006", "20061231", "0" ),
	TEKLEYBO_26( "26", "LEY 44 / 2007", "20080113", "0" ),
	TEKLEYBO_27( "27", "REAL DECRETO LEGISLATIVO 1 / 1994", "19940901", "0" ),
	TEKLEYBO_28( "28", "REAL DECRETO 1975 / 2008", "20081203", "20100617" ),
	TEKLEYBO_29( "29", "REAL DECRETO 100 / 2009", "20090225", "20110224" ),
	TEKLEYBO_30( "30", "REAL DECRETO 100 / 2009", "20090227", "20091231" ),
	TEKLEYBO_31( "31", "REAL DECRETO LEY 2 / 2009", "20090308", "20091231" ),
	TEKLEYBO_32( "32", "REAL DECRETO 1300 / 2009", "20090820", "20091231" ),
	TEKLEYBO_33( "33", "LEY 27 / 2009", "20100101", "20100617" ),
	TEKLEYBO_34( "34", "REAL DECRETO 1678 / 2009", "20091201", "20111130" ),
	TEKLEYBO_35( "35", "REAL DECRETO 1679 / 2009", "20091201", "20111130" ),
	TEKLEYBO_36( "36", "LEY ORGANICA 3 / 2007", "20070324", "0" ),
	TEKLEYBO_37( "37", "REAL DECRETO LEY 10 / 2010", "20100618", "20100918" ),
	TEKLEYBO_38( "38", "LEY 35 / 2010", "20100919", "20111231" ),
	TEKLEYBO_39( "39", "REAL DECRETO LEY 3 / 2012", "20120212", "20120707" ),
	TEKLEYBO_40( "40", "LEY 3 / 2012", "20120708", "0" ),
	;
	public static final String TABLE_NAME = "TEKLEYBO";
	public static final String TABLE_DESCRIPTION = "*TEKLEYBO	LEY BONIFICACIÓN						11-09-2012	";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TEKLEYBO( String code, String description, String startDate, String endDate ) {
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
				return sdf.parse(startDate);
			}
		} catch (ParseException e) {
			// nothing to do
		}
		return null;
	}

	public Date getEndDate(){
		try {
			if(endDate!=null){
				return sdf.parse(endDate);
			}
		} catch (ParseException e) {
			// nothing to do
		}
	return null;
	}

	public static TEKLEYBO getEnumByValue(String expression) {
		for( TEKLEYBO o : TEKLEYBO.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}