package net.aonsolutions.occam.api.json;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.server.AonObjectUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AonJSONUtils {

	private AonJSONUtils() {
	
	}
	
	public static Stream<JSONObject> stream(JSONArray array) {
		return IntStream.range(0, array.length())
			.mapToObj(array::getJSONObject);
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
}
