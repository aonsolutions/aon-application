package com.esferalia.aon.gwt.common.client.json;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class JsonGWTUtils implements Serializable {
	
	public static final String SIMPLE_DATE_FORMAT = "dd/MM/yyyy";
	public static final String SIMPLE_DATE_FORMAT2 = "dd-MM-yyyy";
	public static final String SIMPLE_DATE_FORMAT3 = "yyyy/MM/dd";
	public static final String SIMPLE_DATE_FORMAT4 = "yyyy-MM-dd";
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String DATE_TIME_FORMAT_AUX = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	private JsonGWTUtils() {
	
	}
	
	public static String getString(JSONObject json, String key ) {
		return json != null ? (json.get(key) != null ? json.get(key).isString().stringValue() : null) : null;
	}
	
	public static String getString(JSONObject json, String key, String defaultValue ) {
		return json != null ? (json.get(key) != null ? json.get(key).isString().stringValue() : defaultValue) : defaultValue;
	}
	
	public static String optString(JSONObject json, String key ) {
		return json != null ? (json.get(key) != null ? json.get(key).isString().stringValue() : "") : "";
	}
	
	public static JSONObject getJSONObject(JSONObject json, String key) {
		return json != null && json.get(key) != null 
				? json.get(key).isObject()
				: new JSONObject();
	}
	
	public static JSONArray getJSONArray(JSONObject json, String key) {
		return json.get(key) != null ? json.get(key).isArray() : new JSONArray();
	}
	
	public static Boolean getBoolean(JSONObject json, String key ) {
		String value = json != null && json.get(key) != null ? json.get(key).isString().stringValue() : null;
		if (AonStringUtils.isNotBlank(value)) {
			return Boolean.valueOf(json.get(key).toString()); 
		}
		return null;
	}
	
	public static boolean getboolean(JSONObject json, String key ) {
		String value = json != null && json.get(key) != null ? json.get(key).isString().stringValue() : null;
		if (AonStringUtils.isNotBlank(value)) {
			Boolean ret = Boolean.valueOf(json.get(key).toString());
			return ret != null ? ret : false; 
		}
		return false;
	}

	public static Double getDouble(JSONObject json, String key ) {
		return AonNumberUtils.toDouble(json.get(key) != null ? json.get(key).isNumber().doubleValue() : null); 
	}
	
	public static Double getdouble(JSONObject json, String key ) {
		Number n = AonNumberUtils.toDouble(json.get(key) != null ? json.get(key).isNumber().doubleValue() : null);
		return n==null?0:n.doubleValue();
	}
	
	public static Short getShort(JSONObject json, String key) {
		Number n = AonNumberUtils.toDouble(json.get(key) != null ? json.get(key).isNumber().doubleValue() : null);
		return n==null?0:n.shortValue();
	}
	
	public static Integer getInteger(JSONObject json, String key ) {
		if(json == null) return null;
		return json.get(key) != null ? AonNumberUtils.toInteger(json.get(key).isNumber().toString()) : null; 
	}
	
	public static Integer optInteger(JSONObject json, String key ) {
		return json != null ? AonNumberUtils.toInteger(json.get(key) != null ? json.get(key).isNumber().toString() : null) : null; 
	}
	
	public static Integer getInt(JSONObject json, String key ) {
		Number n = AonNumberUtils.toInteger(json.get(key) != null ? json.get(key).isNumber().toString() : null); 
		return n==null?0:n.intValue();
	}
	
	public static Byte getByte(JSONObject json, String key ) {
		Number n = AonNumberUtils.toInteger(json.get(key) != null ? json.get(key).isNumber().toString() : null); 
		return n == null ? 0 : n.byteValue();
	}
	
	public static Date getDate(JSONObject json, String key ) {
		Date d = null;
		if(json != null) { 
			String date = json.get(key) != null ? json.get(key).isString().stringValue() : null;
			if(AonStringUtils.isNotBlank(date)) {
				d = parse(date.replace("\"", ""));
				if(d == null) d = getDateTime(json, key);
			}
		}
		return d;
	}
	
	public static Date getDateFormat(JSONObject json, String key, String format ) {
		if(json == null) return null;
		String date = json.get(key) != null ? json.get(key).isString().stringValue() : null;
		Date d = parse(date, format);
		if(d == null) d = getDateTime(json, key);
		return d;
	}
	
	public static Date getDateTime(JSONObject json, String key ) {
		try {
			 long date = json.get(key) != null ? (long) json.get(key).isNumber().doubleValue() : null;
			 return date >0 ? new Date(date) : null;
		} catch (Exception e) {
			return null;
		} 
	}
	
	public static String getDateJSON(Date date) {
		if (date != null) {
			return simpleFormat(date);
		}
		return null;
	}
	
	public static JSONObject putDate(JSONObject json, String key , Date date) {
		if (date != null) {
			String value = getDateJSON(date);
			json.put(key, new JSONString(value));
		}
		return json;
	}
	public static JSONObject putEnum(JSONObject json, String key , Enum<?> enumValue) {
		return json.put(key, new JSONNumber( enumValue==null ? null : enumValue.ordinal() )).isObject();
	}
	
	public static boolean has(JSONObject json, String key) {
		return json.get(key) != null;
	}
	
	private static String format(Date date, String pattern) {
		DateTimeFormat format = DateTimeFormat.getFormat(pattern);
		return date == null ? null : format.format(date);
	}
	
	private static Date parse(String date, String pattern) {
		DateTimeFormat format = DateTimeFormat.getFormat(pattern);
		return parse(date, format);
	}
	
	private static Date parse(String date, DateTimeFormat format) {
		try {
			return date == null ? null : format.parse(date);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
    
	private static String simpleFormat(Date date) {
		return date == null ? null : format(date, SIMPLE_DATE_FORMAT);
	}
	
	private static Date parse(String date) {
		if(date == null) return null;
		Date d = dateTimeParse(date);
		if(d == null) d = simpleParse(date);
		return d;
	}
	
	private static Date simpleParse(String date) {
		if(AonStringUtils.isBlank(date)) return null;
		date = date.replace(" ", "");
		Date d = null;
		if(isSimpleDateFormat(date)) d = parse(date, SIMPLE_DATE_FORMAT);
		if(d == null && isSimpleDateFormat2(date)) d = parse(date, SIMPLE_DATE_FORMAT2);
		if(d == null && isSimpleDateFormat3(date)) d = parse(date, SIMPLE_DATE_FORMAT3);
		if(d == null && isSimpleDateFormat4(date)) d = parse(date, SIMPLE_DATE_FORMAT4);
		return d;
	}
	
	private static Date dateTimeParse(String date) {
		if(date == null) return null;
		Date d = parse(date, DATE_TIME_FORMAT);
		if(d == null) d = parse(date, DATE_TIME_FORMAT_AUX);
		return d;
	}
	
	private static boolean isSimpleDateFormat(String str) {
		return !AonStringUtils.isBlank(str) && str.length() == 10 && str.contains("/") && str.indexOf("/") == 2;
	}
	
	private static boolean isSimpleDateFormat2(String str) {
		return !AonStringUtils.isBlank(str) && str.length() == 10 && str.contains("-") && str.indexOf("-") == 2;
	}

	private static boolean isSimpleDateFormat3(String str) {
		return !AonStringUtils.isBlank(str) && str.length() == 10 && str.contains("/") && str.indexOf("/") == 4;
	}
	
	private static boolean isSimpleDateFormat4(String str) {
		return !AonStringUtils.isBlank(str) && str.length() == 10 && str.contains("-") && str.indexOf("-") == 4;
	}
}
