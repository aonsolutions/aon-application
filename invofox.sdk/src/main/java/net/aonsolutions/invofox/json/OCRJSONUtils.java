package net.aonsolutions.invofox.json;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class OCRJSONUtils {
	private static final String DATE_PATTERN = "yyyy-MM-dd";
	private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
	
	private OCRJSONUtils() {
	}
	
	public static Stream<JSONObject> stream(JSONArray array) {
		return IntStream.range(0, array.length())
			.mapToObj(array::getJSONObject);
	}
	
	public static JSONObject getObject(JSONObject json, String key ) {
		if(json == null || json.opt(key) == null) return null;
		return json.optJSONObject(key,null);
	}
	
	public static JSONArray getArray(JSONObject json, String key ) {
		if(json == null || json.opt(key) == null) return null;
		return json.optJSONArray(key);
	}
	
	public static Object getRawObject(JSONObject json, String key ) {
		if(json == null) return null;
		return json.opt(key);
	}

	public static Integer getInteger(JSONObject json, String key ) {
		if(json == null || json.opt(key) == null) return null;
		return AonNumberUtils.toInteger(json.optNumber(key));
	}
	
	public static Double getDouble(JSONObject json, String key ) {
		if(json == null || json.opt(key) == null) return null;
		return AonNumberUtils.toDouble(json.optNumber(key));
	}

	public static String getString(JSONObject json, String key ) {
		if(json == null || json.opt(key) == null) return null;
		return json.optString(key,null);
	}
	
	public static String[] getStringArray(JSONObject json, String key ) {
		if(json == null) return null;
		JSONArray arr = getArray(json, key);
		if(arr == null) return null;
		String[] retAray = new String[arr.length()];
		for (int i = 0; i < arr.length(); i++) {
			retAray[i] = (String) arr.get(i);
		}
		return retAray;
	}

	public static Integer[] getIntegerArray(JSONObject json, String key ) {
		if(json == null) return null;
		JSONArray arr = getArray(json, key);
		if(arr == null) return null;
		Integer[] retAray = new Integer[arr.length()];
		for (int i = 0; i < arr.length(); i++) {
			retAray[i] = (Integer) arr.get(i);
		}
		return retAray;
	}

	public static BigDecimal[] getBigDecimalArray(JSONObject json, String key ) {
		if(json == null) return null;
		JSONArray arr = getArray(json, key);
		if(arr == null) return null;
		BigDecimal[] retAray = new BigDecimal[arr.length()];
		for (int i = 0; i < arr.length(); i++) {
			Number number = (Number) arr.get(i);
			if (number instanceof Integer
				|| number instanceof Long
	            || number instanceof Short
	            || number instanceof Byte) {
				retAray[i] = BigDecimal.valueOf(number.longValue());
	        } else {
	        	retAray[i] = BigDecimal.valueOf(number.doubleValue());
	        }
		}
		return retAray;
	}

	public static BigDecimal getNumber(JSONObject json, String key ) {
		if(json == null) return null;
		return json.optBigDecimal(key,null);
	}
	
	public static boolean getBoolean(JSONObject json, String key ) {
		if (json != null && AonStringUtils.isNotBlank(getString(json, key))) {
			return Boolean.valueOf( json.optBoolean(key)); 
		}
		return false;
	}
	
	public static Date getDate(JSONObject json, String key ) {
		if(json != null && AonStringUtils.isNotBlank(getString(json, key))) {
			LocalDate ld = LocalDate.parse( json.optString(key, null), DATE_FORMATTER);
			return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
		}
		return null;
	}
	public static String formatDate(Date date) {
		if (date == null) return null;
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(DATE_FORMATTER);
	}
	
	public static Date getDateTime(JSONObject json, String key ) {
		if(json != null && AonStringUtils.isNotBlank(getString(json, key))) { 
			LocalDateTime ld = LocalDateTime.parse( json.optString(key, null), DATE_TIME_FORMATTER);
			return Date.from(ld.atZone(ZoneId.systemDefault()).toInstant());
		}
		return null;
	}
	public static String formatDateTime(Date date) {
		if (date == null) return null;
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(DATE_TIME_FORMATTER);
	}

}
