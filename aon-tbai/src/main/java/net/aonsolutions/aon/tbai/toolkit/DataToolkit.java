package net.aonsolutions.aon.tbai.toolkit;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Pattern;

public class DataToolkit {
	// FORMAT DATE
	public static Optional<String> formatDate(Date date, String format) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
		Optional<String> formattedDate;
		formattedDate = Optional.of(dateFormatter.format(date));
		return formattedDate;
	}
	
	//PARSE A DATE WITH AN SPECIFIC FORMAT
	public static Date parseDate(String dateStr,String format) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(format);	
		Date formattedDate;
		
		try {
			formattedDate = dateFormatter.parse(dateStr);
			return formattedDate;
		} catch (Exception e){return null;}		
	}

	//IS SOMETHINNG EMPTY
	public static boolean isEmpty(Object o) {
		
		if(o == null) return true;
		if(o instanceof String) {
			String s = String.valueOf(o);
			return s.trim().equals("");
		}
		
		return false;
	}
	
	//IS NOT EMPTY
	public static boolean isPresent(Object o){
		return !isEmpty(o);
	}
	
	//IDENTIFY NIF FORMATS
	public static boolean checkNIF(String ipf) {
		Pattern nif  = Pattern.compile("^\\d{8}[a-zA-Z]{1}$", Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
		Pattern nie  = Pattern.compile("^[XxTtYyZz]{1}\\d{7}[a-zA-Z]{1}$", Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
		Pattern cif  = Pattern.compile("^[a-zA-Z]{1}\\d{7}[0-9]{1}$", Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
		
		Map<Pattern, Integer> patterns = new HashMap<Pattern, Integer>();
		patterns.put(nif, 1);
		patterns.put(nie, 6);
		patterns.put(cif, 4);
		
		String identity = "";
		for (Entry<Pattern, Integer> entry : patterns.entrySet()) {if ( entry.getKey().matcher(ipf).matches()) { identity = entry.getValue().toString(); break; }}
		return isPresent(identity);
	}
}
