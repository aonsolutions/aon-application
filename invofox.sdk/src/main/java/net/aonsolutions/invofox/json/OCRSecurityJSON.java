package net.aonsolutions.invofox.json;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.invofox.model.OCRSecurity;

public class OCRSecurityJSON {
	
	private OCRSecurityJSON() {
	}
	
	public static List<OCRSecurity> from(JSONArray array) {
		if (array == null || array.isEmpty()) return Collections.emptyList();
		return OCRJSONUtils.stream(array)
			.map(OCRSecurityJSON::from)
			.toList();		
	}
	
	public static OCRSecurity from(JSONObject json) {
		if (json == null) return null; 
		return new OCRSecurity()
			.setAlgorithm(OCRJSONUtils.getString(json, OCRNames.ALGORITHM))
			.setSecret(OCRJSONUtils.getString(json, OCRNames.SECRET));
	}
	
	public static JSONArray to(List<OCRSecurity> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRSecurity> stream) {
		return stream
			.map(OCRSecurityJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRSecurity security) {
		if (security == null) return null;
		return new JSONObject()
				.put(OCRNames.ALGORITHM, security.getAlgorithm())
				.put(OCRNames.SECRET, security.getSecret())
		
		;
	}

	public static List<OCRSecurity> from(Object rawObject) {
		if (rawObject instanceof JSONArray) {
			return from((JSONArray) rawObject);
		}
		if (rawObject instanceof JSONObject) {
			LinkedList<OCRSecurity> list = new LinkedList<>();
			list.add(from( (JSONObject) rawObject));
			return list;
		}
		return Collections.emptyList();
	}
}
