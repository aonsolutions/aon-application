package net.aonsolutions.invofox.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.invofox.model.OCRApiKeyResponse;

public class OCRApiKeyResponseJSON {
	
	private OCRApiKeyResponseJSON() {
	}
	
	public static List<OCRApiKeyResponse> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCRApiKeyResponseJSON::from)
			.toList();		
	}
	
	public static OCRApiKeyResponse from(JSONObject json) {
		if (json == null) return null; 
		return new OCRApiKeyResponse()
			.setHttpCode(OCRJSONUtils.getInteger(json, OCRNames.HTTP_CODE))
			.setApiKey(OCRApiKeyJSON.from(OCRJSONUtils.getObject(json, OCRNames.RESULT)))
			.setError(OCRErrorJSON.from(OCRJSONUtils.getObject(json, OCRNames.ERROR)))
		;
	}
	
	public static JSONArray to(List<OCRApiKeyResponse> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRApiKeyResponse> stream) {
		return stream
			.map(OCRApiKeyResponseJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRApiKeyResponse response) {
		if (response == null) return null;
		return new JSONObject()
			.putOpt(OCRNames.HTTP_CODE, response.getHttpCode().orElse(null))
			.putOpt(OCRNames.RESULT, response.getApikey().map(OCRApiKeyJSON::to).orElse(null))
			.putOpt(OCRNames.ERROR, response.getError().map(OCRErrorJSON::to).orElse(null))
			;
	}
}
