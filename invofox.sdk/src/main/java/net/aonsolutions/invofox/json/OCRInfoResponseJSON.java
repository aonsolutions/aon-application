package net.aonsolutions.invofox.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.invofox.model.OCRInfoResponse;

public class OCRInfoResponseJSON {
	
	private OCRInfoResponseJSON() {
	}
	
	public static List<OCRInfoResponse> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCRInfoResponseJSON::from)
			.toList();		
	}
	
	public static OCRInfoResponse from(JSONObject json) {
		if (json == null) return null; 
		return new OCRInfoResponse()
			.setHttpCode(OCRJSONUtils.getInteger(json, OCRNames.HTTP_CODE))
			.setPages(OCRPageJSON.from(OCRJSONUtils.getArray(json, OCRNames.RESULT)))
			.setError(OCRErrorJSON.from(OCRJSONUtils.getObject(json, OCRNames.ERROR)))
			.setSkip(OCRJSONUtils.getInteger(json, OCRNames.SKIP))		
			.setLimit(OCRJSONUtils.getInteger(json, OCRNames.LIMIT)) 
			.setCount(OCRJSONUtils.getInteger(json, OCRNames.COUNT))
		;
	}
	
	public static JSONArray to(List<OCRInfoResponse> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRInfoResponse> stream) {
		return stream
			.map(OCRInfoResponseJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRInfoResponse response) {
		if (response == null) return null;
		return new JSONObject()
			.putOpt(OCRNames.HTTP_CODE, response.getHttpCode().orElse(null))
			.putOpt(OCRNames.RESULT, response.getPages().map(OCRPageJSON::to).orElse(null))
			.putOpt(OCRNames.ERROR, response.getError().map(OCRErrorJSON::to).orElse(null))
			;
	}
}
