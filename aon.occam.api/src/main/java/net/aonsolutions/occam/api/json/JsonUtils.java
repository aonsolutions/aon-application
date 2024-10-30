package net.aonsolutions.occam.api.json;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

class JsonUtils {

	private JsonUtils() {
	}
	
	static boolean isEmpty(JSONObject json) {
		if (json == null) return true;
		return AonCollectionUtils.isEmpty(json.keySet());
	}
	
	static JSONObject getJSONObject(JSONObject json, String key) {
	return json != null && json.opt(key) != null 
			? json.optJSONObject(key) 
			: null;
}

	static Integer getInteger(JSONObject json, String key ) {
		if(json == null) return null;
		Number opt = json.optNumber(key, null);
		return null == opt ? null : AonNumberUtils.toInteger(opt);
	}
	
	static String getString(JSONObject json, String key ) {
		return json != null ? json.optString(key,null) : null;
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
	
	static byte getbyte(JSONObject json, String key ) {
		if(json == null) return 0;
		Number opt = json.optNumber(key, 0);
		return null == opt ? 0 : AonNumberUtils.toInteger(opt).byteValue();
	}
	
	static Date parseDate(JSONObject json, String key ) {
		return parseDate(json.optString(key));
	}
	static Timestamp parseDateTime(JSONObject json, String key ) {
		 return parseDateTime(json.optString(key));
	}
	static String formatDate(Date date) {
		SimpleDateFormat format = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
		return formatDate(date, format);
	}
	static String formatDateTime(Date date) {
		SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT);
		return formatDate(date, format);
	}
	// *********************************** [PRIVATE]
	private static final String SIMPLE_DATE_FORMAT = "dd/MM/yyyy";
	private static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
	private static Date parseDate(String date) {
		SimpleDateFormat format = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
		return parseDate(date, format);
	}
	private static Date parseDate(String date, SimpleDateFormat format) {
		try {
			return date == null ? null : format.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}
	private static String formatDate(Date date, SimpleDateFormat format) {
		return date == null ? null : format.format(date);
	}
	
	private static Timestamp parseDateTime(String date) {
		SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT);
		return parseDateTime(date, format);
	}
	private static Timestamp parseDateTime(String date, SimpleDateFormat format) {
		try {
			return date == null ? null : new Timestamp(format.parse(date).getTime());
		} catch (ParseException e) {
			return null;
		}
	}
	
	
	
	// ----------------------------------------------- USED?
//	private static JSONArray getJSONArray(JSONObject json, String key) {
//		return json.opt(key) != null ? json.optJSONArray(key) : new JSONArray();
//	}
//	
//	private static Boolean getBoolean(JSONObject json, String key ) {
//		String value = json != null && json.opt(key) != null ? json.optString(key,null) : null;
//		if (AonStringUtils.isNotBlank(value)) {
//			return Boolean.valueOf( json.optBoolean(key)); 
//		}
//		return null;
//	}
//	
//	private static Double getdouble(JSONObject json, String key ) {
//		Number n = AonNumberUtils.toDouble(json.optNumber(key, null));
//		return n==null?0:n.doubleValue();
//	}
//	
//	private static Short getShort(JSONObject json, String key) {
//		Number opt = json.optNumber(key, null);
//		return null == opt ? null : AonNumberUtils.toShort(opt).shortValue(); 
//	}
//	
//	private static int getint(JSONObject json, String key ) {
//		if(json == null) return 0;
//		Number opt = json.optNumber(key, null);
//		return null == opt ? 0 : AonNumberUtils.toInteger(opt).intValue();
//	}
//	
//	private static Byte getByte(JSONObject json, String key ) {
//		if(json == null) return null;
//		Number opt = json.optNumber(key, null);
//		return null == opt ? 0 : AonNumberUtils.toInteger(opt).byteValue();
//	}
//	
//	private static Stream<JSONObject> stream(JSONObject json, String key) {
//		return stream( getJSONArray(json, key) );
//	}
//	
//	private static Stream<JSONObject> stream( JSONArray array ) {
//		if (array == null) return Stream.empty();
//		return IntStream
//	    	.range(0,array.length())
//	    	.mapToObj(i -> array.getJSONObject(i));
//	}

}
