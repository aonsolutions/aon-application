package net.aonsolutions.invofox.json;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.invofox.model.OCRLogin;

public class OCRLoginJSON {
	
	private OCRLoginJSON() {
	}
	
	public static List<OCRLogin> from(JSONArray array) {
		if (array == null || array.isEmpty()) return Collections.emptyList();
		return OCRJSONUtils.stream(array)
			.map(OCRLoginJSON::from)
			.toList();		
	}
	
	public static OCRLogin from(JSONObject json) {
		if (json == null) return null; 
		return new OCRLogin()
			.setToken(OCRJSONUtils.getString(json, OCRNames.TOKEN))
			.setExpiration(OCRJSONUtils.getString(json, OCRNames.EXPIRATION))
			.setUpdatePassword(OCRJSONUtils.getBoolean(json, OCRNames.UPDATE_PASSWORD))
			.setUser(OCRUserJSON.from(OCRJSONUtils.getObject(json, OCRNames.USER)));
	}
	
	public static JSONArray to(List<OCRLogin> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRLogin> stream) {
		return stream
			.map(OCRLoginJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRLogin loginToken) {
		if (loginToken == null) return null;
		return new JSONObject()
			.putOpt(OCRNames.TOKEN, loginToken.getToken().orElse(null))
			.putOpt(OCRNames.EXPIRATION, loginToken.getExpiration().orElse(null))
		;
	}

	public static List<OCRLogin> from(Object rawObject) {
		if (rawObject instanceof JSONArray) {
			return from((JSONArray) rawObject);
		}
		if (rawObject instanceof JSONObject) {
			LinkedList<OCRLogin> list = new LinkedList<>();
			list.add(from( (JSONObject) rawObject));
			return list;
		}
		return Collections.emptyList();
	}
}
