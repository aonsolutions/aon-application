package com.esferalia.aon.payroll.enumeration.certificados;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.esferalia.aon.payroll.enumeration.IPayrollTablesEnum;
import org.apache.commons.lang.time.DateUtils;

/** 
 * Enumeration for represent Certific@2 (S.E.P.E.) DCODEDTC table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.CertificadosCodeTablesWriter
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * No description found
 *  ------------------------------------------------------------------------
 */ 
public enum DCODEDTC implements IPayrollTablesEnum {

	DCODEDTC_001( "001", null, null, null ),
	DCODEDTC_002( "002", null, null, null ),
	DCODEDTC_003( "003", null, null, null ),
	DCODEDTC_006( "006", null, null, null ),
	DCODEDTC_007( "007", null, null, null ),
	DCODEDTC_008( "008", null, null, null ),
	DCODEDTC_009( "009", null, null, null ),
	DCODEDTC_010( "010", null, null, null ),
	DCODEDTC_011( "011", null, null, null ),
	DCODEDTC_012( "012", null, null, null ),
	DCODEDTC_014( "014", null, null, null ),
	DCODEDTC_015( "015", null, null, null ),
	DCODEDTC_016( "016", null, null, null ),
	DCODEDTC_017( "017", null, null, null ),
	DCODEDTC_018( "018", null, null, null ),
	DCODEDTC_019( "019", null, null, null ),
	DCODEDTC_020( "020", null, null, null ),
	DCODEDTC_021( "021", null, null, null ),
	DCODEDTC_022( "022", null, null, null ),
	DCODEDTC_023( "023", null, null, null ),
	DCODEDTC_025( "025", null, null, null ),
	DCODEDTC_027( "027", null, null, null ),
	DCODEDTC_028( "028", null, null, null ),
	DCODEDTC_029( "029", null, null, null ),
	DCODEDTC_032( "032", null, null, null ),
	DCODEDTC_033( "033", null, null, null ),
	DCODEDTC_034( "034", null, null, null ),
	DCODEDTC_035( "035", null, null, null ),
	DCODEDTC_036( "036", null, null, null ),
	DCODEDTC_037( "037", null, null, null ),
	DCODEDTC_038( "038", null, null, null ),
	DCODEDTC_039( "039", null, null, null ),
	DCODEDTC_040( "040", null, null, null ),
	DCODEDTC_041( "041", null, null, null ),
	DCODEDTC_042( "042", null, null, null ),
	DCODEDTC_043( "043", null, null, null ),
	DCODEDTC_044( "044", null, null, null ),
	DCODEDTC_045( "045", null, null, null ),
	DCODEDTC_046( "046", null, null, null ),
	DCODEDTC_047( "047", null, null, null ),
	DCODEDTC_048( "048", null, null, null ),
	DCODEDTC_049( "049", null, null, null ),
	DCODEDTC_053( "053", null, null, null ),
	DCODEDTC_054( "054", null, null, null ),
	DCODEDTC_056( "056", null, null, null ),
	DCODEDTC_057( "057", null, null, null ),
	DCODEDTC_058( "058", null, null, null ),
	DCODEDTC_059( "059", null, null, null ),
	DCODEDTC_064( "064", null, null, null ),
	DCODEDTC_065( "065", null, null, null ),
	DCODEDTC_070( "070", null, null, null ),
	DCODEDTC_073( "073", null, null, null ),
	DCODEDTC_074( "074", null, null, null ),
	DCODEDTC_076( "076", null, null, null ),
	DCODEDTC_077( "077", null, null, null ),
	DCODEDTC_078( "078", null, null, null ),
	DCODEDTC_079( "079", null, null, null ),
	DCODEDTC_084( "084", null, null, null ),
	DCODEDTC_085( "085", null, null, null ),
	DCODEDTC_086( "086", null, null, null ),
	DCODEDTC_089( "089", null, null, null ),
	DCODEDTC_098( "098", null, null, null ),
	DCODEDTC_102( "102", null, null, null ),
	DCODEDTC_111( "111", null, null, null ),
	DCODEDTC_112( "112", null, null, null ),
	DCODEDTC_151( "151", null, null, null ),
	DCODEDTC_152( "152", null, null, null ),
	DCODEDTC_251( "251", null, null, null ),
	DCODEDTC_252( "252", null, null, null ),
	DCODEDTC_501( "501", null, null, null ),
	DCODEDTC_502( "502", null, null, null ),
	DCODEDTC_510( "510", null, null, null ),
	DCODEDTC_511( "511", null, null, null ),
	DCODEDTC_512( "512", null, null, null ),
	DCODEDTC_519( "519", null, null, null ),
	DCODEDTC_541( "541", null, null, null ),
	DCODEDTC_542( "542", null, null, null ),

	;
	public static final String TABLE_NAME = "DCODEDTC";
	public static final String TABLE_DESCRIPTION = "No description found";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	DCODEDTC( String code, String description, String startDate, String endDate ) {
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

	public static DCODEDTC getEnumByValue(String expression) {
		for( DCODEDTC o : DCODEDTC.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}