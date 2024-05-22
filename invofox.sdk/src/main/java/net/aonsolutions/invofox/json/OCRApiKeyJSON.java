package net.aonsolutions.invofox.json;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.invofox.model.OCRApiKey;

public class OCRApiKeyJSON {
	
	private OCRApiKeyJSON() {
	}
	
	public static List<OCRApiKey> from(JSONArray array) {
		if (array == null || array.isEmpty()) return Collections.emptyList();
		return OCRJSONUtils.stream(array)
			.map(OCRApiKeyJSON::from)
			.toList();		
	}
	
	public static OCRApiKey from(JSONObject json) {
		if (json == null) return null; 
		return new OCRApiKey()
			.setId(OCRJSONUtils.getString(json, OCRNames.ID))
			.setName(OCRJSONUtils.getString(json, OCRNames.NAME))
			.setKey(OCRJSONUtils.getString(json, OCRNames.KEY))
			.setCreation(OCRJSONUtils.getString(json, OCRNames.CREATION))
			.setActive("active".equalsIgnoreCase(OCRJSONUtils.getString(json, OCRNames.STATE)))
			;
	}
	
	public static JSONArray to(List<OCRApiKey> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRApiKey> stream) {
		return stream
			.map(OCRApiKeyJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRApiKey apikey) {
		if (apikey == null) return null;
		return new JSONObject()
				.put(OCRNames.ID, apikey.getId())
				.put(OCRNames.NAME, apikey.getName())
				.put(OCRNames.KEY, apikey.getKey())
				.put(OCRNames.CREATION, apikey.getCreation())
				.put(OCRNames.STATE, "active")
		;
	}

	public static List<OCRApiKey> from(Object rawObject) {
		if (rawObject instanceof JSONArray) {
			return from((JSONArray) rawObject);
		}
		if (rawObject instanceof JSONObject) {
			LinkedList<OCRApiKey> list = new LinkedList<>();
			list.add(from( (JSONObject) rawObject));
			return list;
		}
		return Collections.emptyList();
	}
}
