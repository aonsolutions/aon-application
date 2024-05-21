package es.translogia.tedi.json;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.json.JSONObject;

public class TediJSONUtils {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private static final SimpleDateFormat DATE_TIME_FORMAT2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'");
	
	private TediJSONUtils() {
		
	}
	public static JSONObject putDateTime(JSONObject json, String key, Date date) {
		return put(json, key, date, DATE_TIME_FORMAT);
	}
	
	public static JSONObject putDate(JSONObject json, String key, Date date) {
		return put(json, key, date, DATE_FORMAT);
	}

	private static JSONObject put(JSONObject json, String key, Date date, SimpleDateFormat format) {
		return json.put(key, TediJSONUtils.format(date, format));
	}

	public static Double optDouble(JSONObject json, String key) {
		Double value = json.optDouble(key);
		return (value == null || Double.isNaN(value)) ? null : value;
	}

	public static Integer optInteger(JSONObject json, String key) {
		Number value = json.optNumber(key);
		return (value == null) ? null : value.intValue();
	}

	public static Date parseDateTime(String date) {
		return parseDate(date, DATE_TIME_FORMAT);
	}
	public static Date parseDate(String date) {
		try {
			return (date == null || "".equals(date.trim())) ? null : DATE_FORMAT.parse(date);
		} catch (ParseException e0) {
			try {
				return (date == null || "".equals(date.trim())) ? null : DATE_TIME_FORMAT.parse(date);
			} catch (ParseException e1) {
				try {
					return (date == null || "".equals(date.trim())) ? null : DATE_TIME_FORMAT2.parse(date);
				} catch (ParseException e) {
					System.err.printf( "ERROR: UNABLE to parse '"+date+"' date.\n");
					Arrays.stream(e.getStackTrace()).skip(2).limit(30).forEach( t -> System.err.println("\tat " + t ));
					return null;
				}
			}
		}
	}

	private static Date parseDate(String date, SimpleDateFormat format) {
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
	
}
