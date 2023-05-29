package net.aonsolutions.occam.json;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import net.aonsolutions.watson.client.util.AonNumberUtils;
import net.aonsolutions.watson.client.util.AonStringUtils;
import net.aonsolutions.watson.server.AonObjectUtils;

public class AonJSONUtils {
	private static final String DATE_PATTERN = "yyyy-MM-dd";
	private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
	
	private AonJSONUtils() {
	}
	
	public static Stream<JSONObject> stream(JSONArray array) {
		return IntStream.range(0, array.length())
			.mapToObj(array::getJSONObject);
	}
	
	public static JSONObject getObject(JSONObject json, String key ) {
		if(json == null) return null;
		return AonObjectUtils.ifNotNullDo(json.opt(key)
			, t -> json.optJSONObject(key, null));
	}
	
	public static JSONArray getArray(JSONObject json, String key ) {
		if(json == null) return null;
		return AonObjectUtils.ifNotNullDo(json.opt(key)
			, t -> json.optJSONArray(key));
	}

	public static Integer getInteger(JSONObject json, String key ) {
		if(json == null) return null;
		return AonObjectUtils.ifNotNullDo(json.opt(key)
			, t -> AonNumberUtils.toInteger(json.optNumber(key, null)));
	}
	
	public static Double getDouble(JSONObject json, String key ) {
		if(json == null) return null;
		return AonObjectUtils.ifNotNullDo(json.opt(key)
			, t -> AonNumberUtils.toDouble(json.optNumber(key, null)));
	}

	public static String getString(JSONObject json, String key ) {
		if(json == null) return null;
		return json.optString(key,null);
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
