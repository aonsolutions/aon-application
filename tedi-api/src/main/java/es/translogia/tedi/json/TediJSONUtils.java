package es.translogia.tedi.json;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.json.JSONObject;

public class TediJSONUtils {
	private final static SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	
	private TediJSONUtils() {
		
	}
	
	public static JSONObject put(JSONObject json, String key, Date date) {
		return json.put(key, TediJSONUtils.formatDate(date));
	}

	public static Double optDouble(JSONObject json, String key) {
		Double value = json.optDouble(key);
		return (value == null || Double.isNaN(value)) ? null : value;
	}

	public static Integer optInteger(JSONObject json, String key) {
		Number value = json.optNumber(key);
		return (value == null) ? null : value.intValue();
	}

	public static Date parseDate(String date) {
		return parseDate(date, DATE_TIME_FORMAT);
	}

	public static Date parseDate(String date, SimpleDateFormat format) {
		try {
			return (date == null || "".equals(date.trim())) ? null : format.parse(date);
		} catch (ParseException e) {
			System.err.printf( "ERROR: UNABLE to parse '"+date+"' date.\n");
			Arrays.stream(e.getStackTrace()).skip(2).limit(30).forEach( t -> System.err.println("\tat " + t ));
			return null;
		}
	}

	public static String formatDate(Date date) {
		return format(date, DATE_TIME_FORMAT);
	}

	public static String format(Date date, SimpleDateFormat format) {
		return (date == null) ? null : format.format(date);
	}
	
	public static void main(String[] args) {
		parseDate("Fri May 13 00:00:00 CEST 2022");
	}

}
