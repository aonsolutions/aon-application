package com.esferalia.aon.occam.api.json;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JsonUtils {
	private static SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
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
		return json.optString(key,null);
	}
	
	public static Boolean getBoolean(JSONObject json, String key ) {
		String value = json.optString(key,null);
		if (AonStringUtils.isNotBlank(value)) {
			return Boolean.valueOf( json.optBoolean(key)); 
		}
		return null;
	}

	public static Double getDouble(JSONObject json, String key ) {
		return AonNumberUtils.toDouble(  json.optNumber(key, null)); 
	}
	
	public static Integer getInteger(JSONObject json, String key ) {
		return AonNumberUtils.toInteger(  json.optNumber(key, null)); 
	}
	
	public static Date getDate(JSONObject json, String key ) {
		try {
			String date = json.optString(key, null);
			return date == null ? null : FORMATTER.parse(date);
		} catch (Exception e) {
			System.out.println( json.optString(key, null));
			throw new AonCoreException("Formato incorrecto de fecha");
		} 
	}
	public static JSONObject putDate(JSONObject json, String key , Date date) {
		if (date != null) {
			String value = FORMATTER.format(date);
			json.put(key, value);
		}
		return json;
	}
	public static JSONObject putEnum(JSONObject json, String key , Enum<?> enumValue) {
		return json.put(key, enumValue==null?null:enumValue.ordinal());
	}
}
