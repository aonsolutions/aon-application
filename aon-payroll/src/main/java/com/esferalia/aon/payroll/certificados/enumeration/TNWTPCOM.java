package com.esferalia.aon.payroll.certificados.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

/** 
 * Enumeration for represent Certific@2 (S.E.P.E.) TNWTPCOM table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.CertificadosCodeTablesWriter
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 * No description found
 *  ------------------------------------------------------------------------
 */ 
public enum TNWTPCOM {

	TNWTPCOM_001( "001", null, null, null ),
	TNWTPCOM_002( "002", null, null, null ),
	TNWTPCOM_003( "003", null, null, null ),
	TNWTPCOM_004( "004", null, null, null ),
	TNWTPCOM_005( "005", null, null, null ),
	TNWTPCOM_006( "006", null, null, null ),
	TNWTPCOM_007( "007", null, null, null ),
	TNWTPCOM_008( "008", null, null, null ),
	TNWTPCOM_009( "009", null, null, null ),
	TNWTPCOM_010( "010", null, null, null ),
	TNWTPCOM_011( "011", null, null, null ),
	TNWTPCOM_012( "012", null, null, null ),
	TNWTPCOM_013( "013", null, null, null ),
	TNWTPCOM_014( "014", null, null, null ),
	TNWTPCOM_015( "015", null, null, null ),
	TNWTPCOM_016( "016", null, null, null ),
	TNWTPCOM_017( "017", null, null, null ),
	TNWTPCOM_018( "018", null, null, null ),
	TNWTPCOM_019( "019", null, null, null ),
	TNWTPCOM_020( "020", null, null, null ),
	TNWTPCOM_021( "021", null, null, null ),
	TNWTPCOM_022( "022", null, null, null ),
	TNWTPCOM_023( "023", null, null, null ),
	TNWTPCOM_024( "024", null, null, null ),
	TNWTPCOM_025( "025", null, null, null ),
	TNWTPCOM_026( "026", null, null, null ),
	TNWTPCOM_027( "027", null, null, null ),
	TNWTPCOM_028( "028", null, null, null ),
	TNWTPCOM_029( "029", null, null, null ),
	TNWTPCOM_030( "030", null, null, null ),
	TNWTPCOM_031( "031", null, null, null ),
	TNWTPCOM_032( "032", null, null, null ),
	TNWTPCOM_033( "033", null, null, null ),
	TNWTPCOM_034( "034", null, null, null ),
	TNWTPCOM_035( "035", null, null, null ),
	TNWTPCOM_036( "036", null, null, null ),
	TNWTPCOM_037( "037", null, null, null ),
	TNWTPCOM_038( "038", null, null, null ),
	TNWTPCOM_039( "039", null, null, null ),
	TNWTPCOM_040( "040", null, null, null ),
	TNWTPCOM_041( "041", null, null, null ),
	TNWTPCOM_042( "042", null, null, null ),
	TNWTPCOM_043( "043", null, null, null ),
	TNWTPCOM_044( "044", null, null, null ),
	TNWTPCOM_045( "045", null, null, null ),
	TNWTPCOM_046( "046", null, null, null ),
	TNWTPCOM_047( "047", null, null, null ),
	TNWTPCOM_048( "048", null, null, null ),
	TNWTPCOM_049( "049", null, null, null ),
	TNWTPCOM_050( "050", null, null, null ),
	TNWTPCOM_051( "051", null, null, null ),
	TNWTPCOM_052( "052", null, null, null ),
	TNWTPCOM_053( "053", null, null, null ),
	TNWTPCOM_054( "054", null, null, null ),
	TNWTPCOM_055( "055", null, null, null ),
	TNWTPCOM_056( "056", null, null, null ),
	TNWTPCOM_057( "057", null, null, null ),
	TNWTPCOM_058( "058", null, null, null ),
	TNWTPCOM_059( "059", null, null, null ),
	TNWTPCOM_060( "060", null, null, null ),
	TNWTPCOM_061( "061", null, null, null ),
	TNWTPCOM_062( "062", null, null, null ),
	TNWTPCOM_063( "063", null, null, null ),
	TNWTPCOM_064( "064", null, null, null ),
	TNWTPCOM_065( "065", null, null, null ),
	TNWTPCOM_066( "066", null, null, null ),
	TNWTPCOM_067( "067", null, null, null ),
	TNWTPCOM_068( "068", null, null, null ),
	TNWTPCOM_069( "069", null, null, null ),
	TNWTPCOM_070( "070", null, null, null ),
	TNWTPCOM_071( "071", null, null, null ),
	TNWTPCOM_072( "072", null, null, null ),
	TNWTPCOM_073( "073", null, null, null ),
	TNWTPCOM_074( "074", null, null, null ),
	TNWTPCOM_075( "075", null, null, null ),
	TNWTPCOM_076( "076", null, null, null ),
	TNWTPCOM_077( "077", null, null, null ),
	TNWTPCOM_078( "078", null, null, null ),
	TNWTPCOM_079( "079", null, null, null ),
	TNWTPCOM_080( "080", null, null, null ),
	TNWTPCOM_081( "081", null, null, null ),
	TNWTPCOM_082( "082", null, null, null ),
	TNWTPCOM_083( "083", null, null, null ),
	TNWTPCOM_084( "084", null, null, null ),
	TNWTPCOM_085( "085", null, null, null ),
	TNWTPCOM_086( "086", null, null, null ),
	TNWTPCOM_087( "087", null, null, null ),
	TNWTPCOM_088( "088", null, null, null ),
	TNWTPCOM_089( "089", null, null, null ),
	TNWTPCOM_090( "090", null, null, null ),
	TNWTPCOM_091( "091", null, null, null ),
	TNWTPCOM_092( "092", null, null, null ),
	TNWTPCOM_093( "093", null, null, null ),
	TNWTPCOM_094( "094", null, null, null ),
	TNWTPCOM_095( "095", null, null, null ),
	TNWTPCOM_096( "096", null, null, null ),
	TNWTPCOM_097( "097", null, null, null ),
	TNWTPCOM_098( "098", null, null, null ),
	TNWTPCOM_100( "100", null, null, null ),
	TNWTPCOM_101( "101", null, null, null ),
	TNWTPCOM_102( "102", null, null, null ),
	TNWTPCOM_109( "109", null, null, null ),
	TNWTPCOM_130( "130", null, null, null ),
	TNWTPCOM_131( "131", null, null, null ),
	TNWTPCOM_139( "139", null, null, null ),
	TNWTPCOM_141( "141", null, null, null ),
	TNWTPCOM_150( "150", null, null, null ),
	TNWTPCOM_151( "151", null, null, null ),
	TNWTPCOM_152( "152", null, null, null ),
	TNWTPCOM_153( "153", null, null, null ),
	TNWTPCOM_154( "154", null, null, null ),
	TNWTPCOM_155( "155", null, null, null ),
	TNWTPCOM_156( "156", null, null, null ),
	TNWTPCOM_157( "157", null, null, null ),
	TNWTPCOM_181( "181", null, null, null ),
	TNWTPCOM_182( "182", null, null, null ),
	TNWTPCOM_183( "183", null, null, null ),
	TNWTPCOM_184( "184", null, null, null ),
	TNWTPCOM_185( "185", null, null, null ),
	TNWTPCOM_186( "186", null, null, null ),
	TNWTPCOM_189( "189", null, null, null ),
	TNWTPCOM_200( "200", null, null, null ),
	TNWTPCOM_209( "209", null, null, null ),
	TNWTPCOM_230( "230", null, null, null ),
	TNWTPCOM_231( "231", null, null, null ),
	TNWTPCOM_239( "239", null, null, null ),
	TNWTPCOM_241( "241", null, null, null ),
	TNWTPCOM_250( "250", null, null, null ),
	TNWTPCOM_251( "251", null, null, null ),
	TNWTPCOM_252( "252", null, null, null ),
	TNWTPCOM_253( "253", null, null, null ),
	TNWTPCOM_254( "254", null, null, null ),
	TNWTPCOM_255( "255", null, null, null ),
	TNWTPCOM_256( "256", null, null, null ),
	TNWTPCOM_257( "257", null, null, null ),
	TNWTPCOM_289( "289", null, null, null ),
	TNWTPCOM_300( "300", null, null, null ),
	TNWTPCOM_309( "309", null, null, null ),
	TNWTPCOM_330( "330", null, null, null ),
	TNWTPCOM_331( "331", null, null, null ),
	TNWTPCOM_350( "350", null, null, null ),
	TNWTPCOM_351( "351", null, null, null ),
	TNWTPCOM_352( "352", null, null, null ),
	TNWTPCOM_353( "353", null, null, null ),
	TNWTPCOM_354( "354", null, null, null ),
	TNWTPCOM_355( "355", null, null, null ),
	TNWTPCOM_356( "356", null, null, null ),
	TNWTPCOM_357( "357", null, null, null ),
	TNWTPCOM_389( "389", null, null, null ),
	TNWTPCOM_401( "401", null, null, null ),
	TNWTPCOM_402( "402", null, null, null ),
	TNWTPCOM_403( "403", null, null, null ),
	TNWTPCOM_408( "408", null, null, null ),
	TNWTPCOM_410( "410", null, null, null ),
	TNWTPCOM_418( "418", null, null, null ),
	TNWTPCOM_420( "420", null, null, null ),
	TNWTPCOM_421( "421", null, null, null ),
	TNWTPCOM_430( "430", null, null, null ),
	TNWTPCOM_431( "431", null, null, null ),
	TNWTPCOM_441( "441", null, null, null ),
	TNWTPCOM_450( "450", null, null, null ),
	TNWTPCOM_451( "451", null, null, null ),
	TNWTPCOM_452( "452", null, null, null ),
	TNWTPCOM_457( "457", null, null, null ),
	TNWTPCOM_500( "500", null, null, null ),
	TNWTPCOM_501( "501", null, null, null ),
	TNWTPCOM_502( "502", null, null, null ),
	TNWTPCOM_503( "503", null, null, null ),
	TNWTPCOM_508( "508", null, null, null ),
	TNWTPCOM_510( "510", null, null, null ),
	TNWTPCOM_518( "518", null, null, null ),
	TNWTPCOM_520( "520", null, null, null ),
	TNWTPCOM_530( "530", null, null, null ),
	TNWTPCOM_531( "531", null, null, null ),
	TNWTPCOM_540( "540", null, null, null ),
	TNWTPCOM_541( "541", null, null, null ),
	TNWTPCOM_550( "550", null, null, null ),
	TNWTPCOM_551( "551", null, null, null ),
	TNWTPCOM_552( "552", null, null, null ),
	TNWTPCOM_557( "557", null, null, null ),
	TNWTPCOM_970( "970", null, null, null ),
	TNWTPCOM_980( "980", null, null, null ),
	TNWTPCOM_990( "990", null, null, null ),


	;
	public static final String TABLE_NAME = "TNWTPCOM";
	public static final String TABLE_DESCRIPTION = "No description found";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private String code;
	private String description;
	private String startDate;
	private String endDate;

	TNWTPCOM( String code, String description, String startDate, String endDate ) {
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

	public static TNWTPCOM getEnumByValue(String expression) {
		for( TNWTPCOM o : TNWTPCOM.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}