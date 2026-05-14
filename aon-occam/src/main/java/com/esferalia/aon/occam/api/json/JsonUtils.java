package com.esferalia.aon.occam.api.json;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
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

	public static boolean isJSONObject(JSONObject json, String key) {
		return getJSONObject(json, key) != null;
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
	
	public static Boolean getBooleanNumber(JSONObject json, String key ) {
		Integer i = getInteger( json, key);
		return i == null? null : AonNumberUtils.equals( i, 1);
	}

	public static boolean getboolean(JSONObject json, String key ) {
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
		String doubleString = getString(json, key);
		if(!AonStringUtils.isBlank(doubleString) && ".".equals(doubleString.substring(0, 1))) {
			doubleString = "0" + doubleString;
			Number n = AonNumberUtils.toDouble(doubleString);
			return n == null ? 0 : n.doubleValue();
		} else {
			Number n = AonNumberUtils.toDouble(json.optNumber(key, null));
			return n == null ? 0 : n.doubleValue();
		}
	}
	
	public static Short getShort(JSONObject json, String key) {
		Number opt = json.optNumber(key, null);
		return AonNumberUtils.toShort(opt); 
	}
	
	public static short getshort(JSONObject json, String key) {
		Number opt = json.optNumber(key, null);
		return AonNumberUtils.toshort(opt); 
	}

	public static Integer getInteger(JSONObject json, String key ) {
		if(json == null) return null;
		Number opt = json.optNumber(key, null);
		return null == opt ? null : AonNumberUtils.toInteger(opt);
	}
	
	/**
	 * @deprecated Same implementation as getInteger(JSONObject json, String key ) 
	 * @use getInteger(JSONObject json, String key )
	 */
	@Deprecated
	public static Integer optInteger(JSONObject json, String key ) {
		if(json == null) return null;
		Number opt = json.optNumber(key, null);
		return null == opt ? null : AonNumberUtils.toInteger(opt);
	}
	
	public static Integer getInt(JSONObject json, String key ) {
		if(json == null) return null;
		Number opt = json.optNumber(key, null);
		return null == opt ? 0 : AonNumberUtils.toInteger(opt).intValue();
	}
	
	public static Byte getByte(JSONObject json, String key ) {
		if(json == null) return null;
		Number opt = json.optNumber(key, null);
		return null == opt ? 0 : AonNumberUtils.toInteger(opt).byteValue();
	}
	
	public static byte getbyte(JSONObject json, String key ) {
		if(json == null) return 0;
		Number opt = json.optNumber(key, 0);
		return null == opt ? 0 : AonNumberUtils.toInteger(opt).byteValue();
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
	
	public static Optional<Date> optDate(JSONObject json, String key ) {
		return Optional.ofNullable( getDate(json, key) );
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
	
	public static String getDateTimeJSON(Date date) {
		if (date != null) {
			return AonDateUtils.dateTimeFormat(date);
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
	
	public static <T extends Enum<?>> T getEnumFromOrdinal(JSONObject json, String key, Class<T> enumClass ) {
		Integer ordinal = getInteger( json, key);
		if (ordinal == null) return null;
		T[] constants = enumClass.getEnumConstants();
		if (constants != null && ordinal >=0 && ordinal < constants.length) {
			return constants[ordinal];
		}
		return null;
		
	}
	
	public static boolean has(JSONObject json, String key) {
		return json.opt(key) != null;
	}
	public static Stream<JSONObject> stream( JSONArray array ) {
		if ( isEmpty(array)) return Stream.empty();
		return IntStream
	    	.range(0,array.length())
	    	.mapToObj(i -> array.getJSONObject(i));
	}

	public static boolean isEmpty(JSONObject json) {
		if (json == null) return true;
		return AonCollectionUtils.isEmpty(json.keySet());
	}
	public static boolean isNotEmpty(JSONObject json) {
		return !isEmpty(json);
	}
	public static boolean isEmpty(JSONArray jsonArray) {
		return (jsonArray == null || jsonArray.length() == 0);
	}
	public static JSONArray nullIfEmpty(JSONArray jsonArray) {
		return isEmpty(jsonArray) ? null : jsonArray;
	}

	public static class JSONArrayCollector {
		private JSONArrayCollector() {
		}
		
	    public static <T> Collector<T, JSONArray, JSONArray> toJSONArray() {
	        return Collector.of(
	            JSONArray::new,                  // supplier
	            JSONArray::put,                  // accumulator
	            (left, right) -> {               // combiner
	                for (int i = 0; i < right.length(); i++) {
	                    left.put(right.get(i));
	                }
	                return left;
	            },
	            Collector.Characteristics.IDENTITY_FINISH
	        );
	    }
	}

	public static Integer[] getIntegerArray(JSONObject json, String key) {
		if(json == null) return null;
		JSONArray jsonArray = json.optJSONArray(key);
		if (jsonArray == null || jsonArray.length() == 0) return null;
		Integer[] arr = new Integer[jsonArray.length()];
		for (int i = 0; i < jsonArray.length(); i++) {
		    arr[i] = jsonArray.isNull(i) ? null : jsonArray.getInt(i);
		}
		return arr;
	}
	
}
