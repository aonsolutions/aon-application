package net.aonsolutions.occam.api.json;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.server.AonObjectUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AonJSONUtils {
	private static final Logger LOGGER = Logger.getLogger(AonJSONUtils.class.getName());
	
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
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
	
	public static Date getDate(JSONObject json, String key ) throws ParseException {
		if(json != null && AonStringUtils.isNotBlank(getString(json, key))) { 
			return DATE_FORMAT.parse(json.optString(key, null));
		}
		return null;
	}
	public static Date getSilentDate(JSONObject json, String key ) {
		try {
			return getDate(json, key);
		} catch (ParseException e) {
			LOGGER.warning("JSON Date Parse error " + e.getMessage() );
			return null;
		}
	}
	public static String formatDate(Date date) {
		if (date == null) return null;
		return DATE_FORMAT.format(date);
	}
	
	public static Date getDateTime(JSONObject json, String key ) throws ParseException {
		if(json != null && AonStringUtils.isNotBlank(getString(json, key))) { 
			return DATE_TIME_FORMAT.parse(json.optString(key, null));
		}
		return null;
	}
	public static Date getSilentDateTime(JSONObject json, String key ) {
		try {
			return getDateTime(json, key);
		} catch (ParseException e) {
			LOGGER.warning("JSON Date Parse error " + e.getMessage() );
			return null;
		}
	}
	public static String formatDateTime(Date date) {
		if (date == null) return null;
		return DATE_TIME_FORMAT.format(date);
	}

}
