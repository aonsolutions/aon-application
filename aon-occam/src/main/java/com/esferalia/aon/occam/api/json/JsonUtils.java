package com.esferalia.aon.occam.api.json;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JsonUtils {

	private JsonUtils() {
	
	}
	
	private static final String ENCODING = "utf-8";
	
	public static String decode(JSONObject json, String key ) {
		String value = json.optString(key,null);
		if (AonStringUtils.isNotBlank(value)) {
			try {
				return URLDecoder.decode( value , ENCODING );
			} catch (UnsupportedEncodingException e) {
			}			
		}
		return value;
	}
	
	public static String getString(JSONObject json, String key ) {
		return json != null ? json.optString(key,null) : null;
	}
	
	public static String getString(JSONObject json, String key, String defaultValue ) {
		return json != null ? json.optString(key, defaultValue) : defaultValue;
	}
	
	public static String optString(JSONObject json, String key ) {
		return json != null ? json.optString(key, "") : "";
	}
	
	public static JSONObject getJSONObject(JSONObject json, String key) {
		return json != null && json.opt(key) != null 
				? json.optJSONObject(key) 
				: new JSONObject();
	}
	
	public static JSONArray getJSONArray(JSONObject json, String key) {
		return json.opt(key) != null ? json.optJSONArray(key) : new JSONArray();
	}
	
	public static Boolean getBoolean(JSONObject json, String key ) {
		String value = json != null && json.opt(key) != null ? json.optString(key,null) : null;
		if (AonStringUtils.isNotBlank(value)) {
			return Boolean.valueOf( json.optBoolean(key)); 
		}
		return null;
	}
	
	public static Boolean getboolean(JSONObject json, String key ) {
		String value = json != null && json.opt(key) != null ? json.optString(key,null) : null;
		if (AonStringUtils.isNotBlank(value)) {
			Boolean ret = Boolean.valueOf( json.optBoolean(key));
			return ret != null ? ret : false; 
		}
		return false;
	}

	public static Double getDouble(JSONObject json, String key ) {
		return AonNumberUtils.toDouble(  json.optNumber(key, null)); 
	}
	
	public static Double getdouble(JSONObject json, String key ) {
		Number n = AonNumberUtils.toDouble(json.optNumber(key, null));
		return n==null?0:n.doubleValue();
	}
	
	public static Short getShort(JSONObject json, String key) {
		Number n = AonNumberUtils.toDouble(json.optNumber(key, null));
		return n==null?0:n.shortValue();
	}
	
	public static Integer getInteger(JSONObject json, String key ) {
		if(json == null) return null;
		return json.opt(key) != null ? AonNumberUtils.toInteger(json.optNumber(key, null)) : null; 
	}
	
	public static Integer optInteger(JSONObject json, String key ) {
		return json != null ? AonNumberUtils.toInteger(json.optNumber(key, null)) : null; 
	}
	
	public static Integer getInt(JSONObject json, String key ) {
		Number n = AonNumberUtils.toInteger(  json.optNumber(key, null)); 
		return n==null?0:n.intValue();
	}
	
	public static Byte getByte(JSONObject json, String key ) {
		Number n = AonNumberUtils.toInteger(json.optNumber(key, null)); 
		return n == null ? 0 : n.byteValue();
	}
	
	public static Date getDate(JSONObject json, String key ) {
		Date d = null;
		if(json != null) { 
			String date = json.optString(key, null);
			if(AonStringUtils.isNotBlank(date)) {
				d = AonDateUtils.parse(date.replace("\"", ""));
				if(d == null) d = getDateTime(json, key);
			}
		}
		return d;
	}
	
	public static Date getDateFormat(JSONObject json, String key, String format ) {
		if(json == null) return null;
		String date = json.optString(key, null);
		Date d = AonDateUtils.parse(date, format);
		if(d == null) d = getDateTime(json, key);
		return d;
	}
	
	public static Date getDateTime(JSONObject json, String key ) {
		try {
			 long date = json.optLong(key);
			 return date >0 ? new Date(date) : null;
		} catch (Exception e) {
			return null;
		} 
	}
	
	public static String getDateJSON(Date date) {
		if (date != null) {
			return AonDateUtils.simpleFormat(date);
		}
		return null;
	}
	
	public static JSONObject putDate(JSONObject json, String key , Date date) {
		if (date != null) {
			String value = getDateJSON(date);
			json.put(key, value);
		}
		return json;
	}
	public static JSONObject putEnum(JSONObject json, String key , Enum<?> enumValue) {
		return json.put(key, enumValue==null?null:enumValue.ordinal());
	}
	
	public static boolean has(JSONObject json, String key) {
		return json.opt(key) != null;
	}
}
