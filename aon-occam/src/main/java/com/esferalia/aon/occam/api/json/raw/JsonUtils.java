package com.esferalia.aon.occam.api.json.raw;

import java.util.Date;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JsonUtils {

	private JsonUtils() {
	
	}
	
	static String getString(JSONObject json, String key ) {
		return json != null ? json.optString(key,null) : null;
	}
	
	static JSONObject getJSONObject(JSONObject json, String key) {
		return json != null && json.opt(key) != null 
				? json.optJSONObject(key) 
				: null;
	}
	
	private static JSONArray getJSONArray(JSONObject json, String key) {
		return json.opt(key) != null ? json.optJSONArray(key) : new JSONArray();
	}
	
	static Boolean getBoolean(JSONObject json, String key ) {
		String value = json != null && json.opt(key) != null ? json.optString(key,null) : null;
		if (AonStringUtils.isNotBlank(value)) {
			return Boolean.valueOf( json.optBoolean(key)); 
		}
		return null;
	}
	
	static boolean getboolean(JSONObject json, String key ) {
		if (json == null ) return false;
		if (key == null ) return false;
		if (json.opt(key) == null) return false;
		String value =  json.optString(key,null);
		if (AonStringUtils.isNotBlank(value)) {
			return json.getBoolean(key);
		}
		return false;
	}

	static Double getdouble(JSONObject json, String key ) {
		Number n = AonNumberUtils.toDouble(json.optNumber(key, null));
		return n==null?0:n.doubleValue();
	}
	
	static Short getShort(JSONObject json, String key) {
		Number opt = json.optNumber(key, null);
		return null == opt ? null : AonNumberUtils.toShort(opt).shortValue(); 
	}
	
	static Integer getInteger(JSONObject json, String key ) {
		if(json == null) return null;
		Number opt = json.optNumber(key, null);
		return null == opt ? null : AonNumberUtils.toInteger(opt);
	}
	
	static int getint(JSONObject json, String key ) {
		if(json == null) return 0;
		Number opt = json.optNumber(key, null);
		return null == opt ? 0 : AonNumberUtils.toInteger(opt).intValue();
	}
	
	static Date getDate(JSONObject json, String key ) {
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
	
	private static Date getDateTime(JSONObject json, String key ) {
		try {
			 long date = json.optLong(key);
			 return date >0 ? new Date(date) : null;
		} catch (Exception e) {
			return null;
		} 
	}
	
	static Stream<JSONObject> stream(JSONObject json, String key) {
		return stream( getJSONArray(json, key) );
	}
	
	static Stream<JSONObject> stream( JSONArray array ) {
		if (array == null) return Stream.empty();
		return IntStream
	    	.range(0,array.length())
	    	.mapToObj(i -> array.getJSONObject(i));
	}
}
