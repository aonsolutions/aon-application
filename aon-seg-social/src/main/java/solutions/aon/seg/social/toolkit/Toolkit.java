package solutions.aon.seg.social.toolkit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

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
	
	//FORMAT DATE TO STRING IN A SPECIFIC FORMAT
	public static Optional<String> formatDate(Date date, String format) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(format);	
		Optional<String> formattedDate = Optional.empty();		
		formattedDate = Optional.of(dateFormatter.format(date));
		return formattedDate;	
	}
	
	//GET DATE ARRAY [DD,MM,YYYY]
	public static Integer[] getDateArray(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return new Integer[] {calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.YEAR)};
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
	
	//SPLITS AN STRING AND RETURNS AN ARRAY
	public static String[] SplitString(String str, int i) {
		return new String[] {str.substring(0,i),str.substring(i)};
	}
	
	
	//CONVERTS A BYTE ARRAY INTO A PDF FILE ON THE PROJECT FOLDER PATH
	public static void buildPdf (byte[] arr_bytes, String docName) {
		File f=new File(docName+".pdf");
		try {
			FileOutputStream fos=new FileOutputStream(f);
			fos.write(arr_bytes);
			fos.close();
		} catch (FileNotFoundException e) {
			System.err.println("Archivo no encontrado");
		} catch (IOException e) {
			System.err.println("Error al escribir");
		}
		
	}
	
	
}
