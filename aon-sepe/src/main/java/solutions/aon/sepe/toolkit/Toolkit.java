package solutions.aon.sepe.toolkit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlDefinitionTerm;

import solutions.aon.sepe.exceptions.SepeException;



public class Toolkit {
	
	//LOGS AN ARRAY OF INFORMATION THROUGH CONSOLE
	public static void log(Object[] things) {
		for (Object th : things) 
			if(th != null) System.out.println(th.toString());
			else System.out.println("[NOT FOUND]");
	}
	
	//REMOVE EXTRA 0s FROM STRINGS
	public static String removeExtraZeros(String code) {
		StringBuilder str = new StringBuilder();
		boolean extraZeros = true;
		for (int i = 0; i < code.length(); i++) 
			if(!extraZeros || code.charAt(i)!= '0') {
				extraZeros = false;
				str.append(code.charAt(i));
			}
		return str.toString();
	}
	
	//PARSE A DATE WITH AN SPECIFIC FORMAT
	public static Date parseDate(String dateStr,String format) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(format);	
		Date formattedDate;
		
		try {
			formattedDate = dateFormatter.parse(dateStr);
			return formattedDate;
		} catch (ParseException e){return null;}		
	}
	
	//FORMAT DATE TO STRING IN A SPECIFIC FORMAT
	public static Optional<String> formatDate(Date date, String format) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(format);	
		Optional<String> formattedDate;
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
		String nbe = "" + (char)160;
		cadena = cadena.replace(nbe, "");
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
	
	//SPLITS AN STRING BY 2 AND RETURNS AN ARRAY
	public static String[] SplitString(String str, int i) throws SepeException {
		try {return new String[] {str.substring(0,i),str.substring(i)};}
		catch(StringIndexOutOfBoundsException e) {throw new SepeException(e.getMessage());}
	}
	
	//SPLITS AN ARRAY MULTIPLE TIMES AND RETURNS AN ARRAY
	public static ArrayList<String> splitString_m(String str, int... indexes ) throws Exception {
		try {
			ArrayList<String> result = new ArrayList<>();
			int ant = 0;
			for (int i : indexes) {
				String cut = str.substring(ant,i);
				ant = i;
				result.add(cut);
			}
			result.add(str.substring(ant));
			
			return result;
		}
		catch(StringIndexOutOfBoundsException e) {throw new SepeException(e.getMessage());}
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
	
	//BUILD A FILE FROM ARRAY OF BYTES
	public static void buildFile (byte[] arr_bytes, String docName) {
		File f=new File(docName);
		try {
			FileOutputStream fos=new FileOutputStream(f);
			fos.write(arr_bytes);
			fos.close();
		}
		catch (FileNotFoundException e) {System.err.println("Archivo no encontrado");}
		catch (IOException e) {System.err.println("Error al escribir");}
		
	}
	
	//ADD DAYS TO A DATE
    public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); 
        return cal.getTime();
    }
	
	//GET AN UREACHABLE DATE
	public static Date getUnreachableDate() {
		return addDays(new Date(), 10);
	}

	//RETURNS IF A DATE IS FUTURE
	public static boolean isFuture(Date to) {
		int r = to.compareTo(new Date());
		return r > 0;
	}
	
	//SPLITS A FLOAT/DOUBLE 
	public static String[] splitDecimal(double dc,int decimals){
		String dc_str = dc + "";
		return new String[]{ dc_str.substring(0, dc_str.indexOf(".")), dc_str.substring(dc_str.indexOf(".")+1, dc_str.indexOf(".") + decimals)};
	}
	
	//HANDLES EMPTY DATA 
	public static void verifyData(Object[] data) throws SepeException {
		for (Object o : data) 
			if(o ==  null || (o instanceof String && ((String) o).trim().equals(""))) throw new SepeException("UnfilledMandatory");
	}

	//REMOVE WEIRD CARACTERS
	public static String removeWeirdCharacters(String str) {
		str = removeNBSP(str.replaceAll(""+((char)32), " ").trim());
		return str;
	}

	//REMOVE SPACES, ACTUALLY
	public static String noSpaces(String str){
		return str.replaceAll("\\s","");
	}

	//ADD A CHARACTER X TIMES TO A STRING LEFT SIDE
	public static String appendStringLeft(String str, String append, int times){
		StringBuilder strBuilder = new StringBuilder(str);
		for (int i = 0; i < times; i++) strBuilder.insert(0, append);
		return strBuilder.toString();
	}

	//FILL A STRING WITH A CHARACTER (LEFT SIDE)
	public static String fillStringLeft(String str, String append, int max){
		StringBuilder strBuilder = new StringBuilder(str);
		while (strBuilder.length() < max) strBuilder.insert(0, append);
		return strBuilder.toString();
	}
	
	public static String[] formatDate(Date fecha) {
		String dia="";
		String mes="";
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(fecha);
		String anio = ""+(calendar.get(Calendar.YEAR));
		if(calendar.get(Calendar.DATE)<10) dia="0"+calendar.get(Calendar.DATE);
		else dia=""+calendar.get(Calendar.DATE);
		if((calendar.get(Calendar.MONTH)+1)<10) mes="0"+(calendar.get(Calendar.MONTH)+1);
		else mes=""+(calendar.get(Calendar.MONTH)+1);
		return new String[] {dia, mes , anio};
	}
	
	public static String identity(String ipf) {
		ipf = removeExtraZeros(ipf);
		Pattern nif  = Pattern.compile(
					"^\\d{8}[a-zA-Z]{1}$"
					, Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
		
		Pattern nie  = Pattern.compile(
				"^[XxTtYyZz]{1}\\d{7}[a-zA-Z]{1}$"
			, Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
		
		Pattern cif  = Pattern.compile(
				"^[a-zA-Z]{1}\\d{7}[0-9]{1}$"
				, Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
				//  -------- NIE PATTERN 
				// -------- (1) --> X0000000X
		
		
		Map<Pattern, Integer> patterns = new HashMap<Pattern, Integer>();
		patterns.put(nif, 1);
		patterns.put(nie, 6);
		patterns.put(cif, 4);
		
		String identity = "";
		for (Entry<Pattern, Integer> entry : patterns.entrySet()) {
			if ( entry.getKey().matcher(ipf).matches()) { identity = entry.getValue().toString(); break; }
		}
		return identity;
	}
	
	public static DomNode getNextSibling(DomNode dt) {
		return dt.getNextElementSibling() instanceof HtmlDefinitionTerm 
		? dt.getNextElementSibling().getNextElementSibling() 
		: dt.getNextElementSibling();
	}
	
	public static String[] dateString(Date fecha) {
		String dia="";
		String mes="";
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(fecha);
		String anio = ""+(calendar.get(Calendar.YEAR));
		if(calendar.get(Calendar.DATE)<10) dia="0"+calendar.get(Calendar.DATE);
		else dia=""+calendar.get(Calendar.DATE);
		if((calendar.get(Calendar.MONTH)+1)<10) mes="0"+(calendar.get(Calendar.MONTH)+1);
		else mes=""+(calendar.get(Calendar.MONTH)+1);
		return new String[] {dia, mes , anio};
	}

}
