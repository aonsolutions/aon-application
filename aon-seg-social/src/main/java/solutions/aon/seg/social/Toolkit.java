package solutions.aon.seg.social;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Toolkit {
	
	//LOGS AN ARRAY OF INFORMATION THROUGH CONSOLE
	public static void log(Object[] things) {
		for (Object th : things) 
			if(th != null) System.out.println(th.toString());
			else System.out.println(th);
	}
	
	//REMOVE EXTRA 0s FROM STRINGS
	public static String removeExtraZeros(String code) {
		String str = "";
		boolean extraZeros = true;
		for (int i = 0; i < code.length(); i++) 
			if(!extraZeros || code.charAt(i)!= '0') {
				extraZeros = false;
				str += code.charAt(i);		
			}
		return str;
	}
	
	//PARSE A DATE WITH AN SPECIFIC FORMAT
	public static Date parseDate(String dateStr,String format) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(format);	
		Date formattedDate = null;		
		
		try {
			formattedDate = dateFormatter.parse(dateStr);
			return formattedDate;
		} catch (ParseException e){return null;}		
	}
	
	//REMOVE NBFP CHARACTER FROMA A STRING
	public static String removeNBSP(String cadena) {
		String nbe=""+(char)160;
		cadena=cadena.replace(nbe, "");
		return cadena;
	}
	
	//RETURNS BOOLEAN FROM A STRING
	public static Boolean toBoolean(String bool) {
		if((bool.equalsIgnoreCase("SI"))||(bool.equalsIgnoreCase("SÍ"))) {
			return true;
		}
		else if(bool.equalsIgnoreCase("NO")) {
			return false;
		}
		return null;
	}
	//DIVIDES AFFILIATION NUMBER IN 2 STRING ARRAY, IF INTRODUCED SSN STRING CONTAINS LESS THAN 3 CHARACTERS IT RETURNS NULL IN BOTH POSSITIONS
	public static String[] splitSSN(String ssn) {
		String[] divided=new String[2];
		if(ssn.length()>2) {
			divided[0]=ssn.substring(0, 2);
			divided[1]=ssn.substring(2);
		}
		return divided;
	}
	
}
