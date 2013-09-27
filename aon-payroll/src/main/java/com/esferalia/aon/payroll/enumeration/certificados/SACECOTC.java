package com.esferalia.aon.payroll.enumeration.certificados;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.esferalia.aon.payroll.sepe.SEPECodeTablesWriter.ISepeEnum;
import org.apache.commons.lang.time.DateUtils;

/** 
 * Enumeration for represent Certific@2 (S.E.P.E.) SACECOTC table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.CertificadosCodeTablesWriter
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * No description found
 *  ------------------------------------------------------------------------
 */ 
public enum SACECOTC implements ISepeEnum {

	SACECOTC_01  ( "01  ", null, null, null ),
	SACECOTC_02  ( "02  ", null, null, null ),
	SACECOTC_05  ( "05  ", null, null, null ),
	SACECOTC_10  ( "10  ", null, null, null ),
	SACECOTC_11  ( "11  ", null, null, null ),
	SACECOTC_12  ( "12  ", null, null, null ),
	SACECOTC_13  ( "13  ", null, null, null ),
	SACECOTC_14  ( "14  ", null, null, null ),
	SACECOTC_15  ( "15  ", null, null, null ),
	SACECOTC_16  ( "16  ", null, null, null ),
	SACECOTC_17  ( "17  ", null, null, null ),
	SACECOTC_18  ( "18  ", null, null, null ),
	SACECOTC_19  ( "19  ", null, null, null ),
	SACECOTC_20  ( "20  ", null, null, null ),
	SACECOTC_21  ( "21  ", null, null, null ),
	SACECOTC_22  ( "22  ", null, null, null ),
	SACECOTC_23  ( "23  ", null, null, null ),
	SACECOTC_24  ( "24  ", null, null, null ),
	SACECOTC_25  ( "25  ", null, null, null ),
	SACECOTC_26  ( "26  ", null, null, null ),
	SACECOTC_27  ( "27  ", null, null, null ),
	SACECOTC_28  ( "28  ", null, null, null ),
	SACECOTC_29  ( "29  ", null, null, null ),
	SACECOTC_30  ( "30  ", null, null, null ),
	SACECOTC_31  ( "31  ", null, null, null ),
	SACECOTC_32  ( "32  ", null, null, null ),
	SACECOTC_33  ( "33  ", null, null, null ),
	SACECOTC_34  ( "34  ", null, null, null ),
	SACECOTC_35  ( "35  ", null, null, null ),
	SACECOTC_36  ( "36  ", null, null, null ),
	SACECOTC_37  ( "37  ", null, null, null ),
	SACECOTC_40  ( "40  ", null, null, null ),
	SACECOTC_41  ( "41  ", null, null, null ),
	SACECOTC_45  ( "45  ", null, null, null ),
	SACECOTC_50  ( "50  ", null, null, null ),
	SACECOTC_51  ( "51  ", null, null, null ),
	SACECOTC_52  ( "52  ", null, null, null ),
	SACECOTC_55  ( "55  ", null, null, null ),
	SACECOTC_60  ( "60  ", null, null, null ),
	SACECOTC_61  ( "61  ", null, null, null ),
	SACECOTC_62  ( "62  ", null, null, null ),
	SACECOTC_63  ( "63  ", null, null, null ),
	SACECOTC_64  ( "64  ", null, null, null ),
	SACECOTC_65  ( "65  ", null, null, null ),
	SACECOTC_66  ( "66  ", null, null, null ),
	SACECOTC_67  ( "67  ", null, null, null ),
	SACECOTC_70  ( "70  ", null, null, null ),
	SACECOTC_71  ( "71  ", null, null, null ),
	SACECOTC_72  ( "72  ", null, null, null ),
	SACECOTC_73  ( "73  ", null, null, null ),
	SACECOTC_74  ( "74  ", null, null, null ),
	SACECOTC_75  ( "75  ", null, null, null ),
	SACECOTC_80  ( "80  ", null, null, null ),
	SACECOTC_85  ( "85  ", null, null, null ),
	SACECOTC_88  ( "88  ", null, null, null ),
	SACECOTC_90  ( "90  ", null, null, null ),
	SACECOTC_91  ( "91  ", null, null, null ),
	SACECOTC_92  ( "92  ", null, null, null ),
	SACECOTC_93  ( "93  ", null, null, null ),
	SACECOTC_95  ( "95  ", null, null, null ),
	SACECOTC_99  ( "99  ", null, null, null ),


	;
	public static final String TABLE_NAME = "SACECOTC";
	public static final String TABLE_DESCRIPTION = "No description found";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	SACECOTC( String code, String description, String startDate, String endDate ) {
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

	public static SACECOTC getEnumByValue(String expression) {
		for( SACECOTC o : SACECOTC.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}