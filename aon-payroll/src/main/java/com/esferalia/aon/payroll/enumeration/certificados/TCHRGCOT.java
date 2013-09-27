package com.esferalia.aon.payroll.enumeration.certificados;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.esferalia.aon.payroll.sepe.SEPECodeTablesWriter.ISepeEnum;
import org.apache.commons.lang.time.DateUtils;

/** 
 * Enumeration for represent Certific@2 (S.E.P.E.) TCHRGCOT table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.CertificadosCodeTablesWriter
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * No description found
 *  ------------------------------------------------------------------------
 */ 
public enum TCHRGCOT implements ISepeEnum {

	TCHRGCOT_0111( "0111", null, null, null ),
	TCHRGCOT_0112( "0112", null, null, null ),
	TCHRGCOT_0113( "0113", null, null, null ),
	TCHRGCOT_0114( "0114", null, null, null ),
	TCHRGCOT_0115( "0115", null, null, null ),
	TCHRGCOT_0121( "0121", null, null, null ),
	TCHRGCOT_0131( "0131", null, null, null ),
	TCHRGCOT_0132( "0132", null, null, null ),
	TCHRGCOT_0133( "0133", null, null, null ),
	TCHRGCOT_0134( "0134", null, null, null ),
	TCHRGCOT_0135( "0135", null, null, null ),
	TCHRGCOT_0136( "0136", null, null, null ),
	TCHRGCOT_0137( "0137", null, null, null ),
	TCHRGCOT_0140( "0140", null, null, null ),
	TCHRGCOT_0151( "0151", null, null, null ),
	TCHRGCOT_0152( "0152", null, null, null ),
	TCHRGCOT_0160( "0160", null, null, null ),
	TCHRGCOT_0170( "0170", null, null, null ),
	TCHRGCOT_0180( "0180", null, null, null ),
	TCHRGCOT_0521( "0521", null, null, null ),
	TCHRGCOT_0522( "0522", null, null, null ),
	TCHRGCOT_0611( "0611", null, null, null ),
	TCHRGCOT_0613( "0613", null, null, null ),
	TCHRGCOT_0721( "0721", null, null, null ),
	TCHRGCOT_0800( "0800", null, null, null ),
	TCHRGCOT_0811( "0811", null, null, null ),
	TCHRGCOT_0812( "0812", null, null, null ),
	TCHRGCOT_0813( "0813", null, null, null ),
	TCHRGCOT_0814( "0814", null, null, null ),
	TCHRGCOT_0821( "0821", null, null, null ),
	TCHRGCOT_0822( "0822", null, null, null ),
	TCHRGCOT_0823( "0823", null, null, null ),
	TCHRGCOT_0825( "0825", null, null, null ),
	TCHRGCOT_0899( "0899", null, null, null ),
	TCHRGCOT_0911( "0911", null, null, null ),
	TCHRGCOT_1211( "1211", null, null, null ),
	TCHRGCOT_1221( "1221", null, null, null ),
	TCHRGCOT_1911( "1911", null, null, null ),
	TCHRGCOT_3011( "3011", null, null, null ),
	TCHRGCOT_4008( "4008", null, null, null ),

	;
	public static final String TABLE_NAME = "TCHRGCOT";
	public static final String TABLE_DESCRIPTION = "No description found";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TCHRGCOT( String code, String description, String startDate, String endDate ) {
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

	public static TCHRGCOT getEnumByValue(String expression) {
		for( TCHRGCOT o : TCHRGCOT.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}