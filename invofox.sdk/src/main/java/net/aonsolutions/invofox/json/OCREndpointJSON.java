package net.aonsolutions.invofox.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.invofox.model.OCREndpoint;

public class OCREndpointJSON {
	
	private OCREndpointJSON() {
	}
	
	public static List<OCREndpoint> from(JSONArray array) {
		if (array == null || array.isEmpty()) return Collections.emptyList();
		return OCRJSONUtils.stream(array)
			.map(OCREndpointJSON::from)
			.toList();		
	}
	
	public static OCREndpoint from(JSONObject json) {
		if (json == null) return null; 
		List<String> headers = new ArrayList<>();
		JSONArray headersArray = OCRJSONUtils.getArray(json, OCRNames.HEADERS);
		for (int i=0; i<headersArray.length(); i++) {
		    headers.add( headersArray.getString(i) );
		}
		return new OCREndpoint()
			.setHeaders(headers)
			.setMethod(OCRJSONUtils.getString(json, OCRNames.METHOD))
			.setUrl(OCRJSONUtils.getString(json, OCRNames.URL));
	}
	
	public static JSONArray to(List<OCREndpoint> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCREndpoint> stream) {
		return stream
			.map(OCREndpointJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCREndpoint endpoint) {
		if (endpoint == null) return null;
		return new JSONObject()
			.put(OCRNames.HEADERS, new JSONArray())
			.put(OCRNames.METHOD, endpoint.getMethod())
			.put(OCRNames.URL, endpoint.getUrl())
		;
	}

	public static List<OCREndpoint> from(Object rawObject) {
		if (rawObject instanceof JSONArray) {
			return from((JSONArray) rawObject);
		}
		if (rawObject instanceof JSONObject) {
			LinkedList<OCREndpoint> list = new LinkedList<>();
			list.add(from( (JSONObject) rawObject));
			return list;
		}
		return Collections.emptyList();
	}
}
