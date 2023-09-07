package net.aonsolutions.invofox.json;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.invofox.model.OCRLoginToken;

public class OCRLoginTokenJSON {
	
	private OCRLoginTokenJSON() {
	}
	
	public static List<OCRLoginToken> from(JSONArray array) {
		if (array == null || array.isEmpty()) return Collections.emptyList();
		return OCRJSONUtils.stream(array)
			.map(OCRLoginTokenJSON::from)
			.toList();		
	}
	
	public static OCRLoginToken from(JSONObject json) {
		if (json == null) return null; 
		return new OCRLoginToken()
			.setId(OCRJSONUtils.getString(json, OCRNames.ID))
			.setToken(OCRJSONUtils.getString(json, OCRNames.TOKEN))
			.setExpiration(OCRJSONUtils.getString(json, OCRNames.EXPIRATION))
			.setUser(OCRJSONUtils.getString(json, OCRNames.USER))
			.setCreation(OCRJSONUtils.getString(json, OCRNames.CREATION));
	}
	
	public static JSONArray to(List<OCRLoginToken> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRLoginToken> stream) {
		return stream
			.map(OCRLoginTokenJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRLoginToken loginToken) {
		if (loginToken == null) return null;
		return new JSONObject()
			.putOpt(OCRNames.ID, loginToken.getId().orElse(null))
			.putOpt(OCRNames.TOKEN, loginToken.getToken().orElse(null))
			.putOpt(OCRNames.EXPIRATION, loginToken.getExpiration().orElse(null))
			.putOpt(OCRNames.USER, loginToken.getUser().orElse(null))
			.putOpt(OCRNames.CREATION, loginToken.getCreation().orElse(null))
		;
	}

	public static List<OCRLoginToken> from(Object rawObject) {
		if (rawObject instanceof JSONArray) {
			return from((JSONArray) rawObject);
		}
		if (rawObject instanceof JSONObject) {
			LinkedList<OCRLoginToken> list = new LinkedList<>();
			list.add(from( (JSONObject) rawObject));
			return list;
		}
		return Collections.emptyList();
	}
}
