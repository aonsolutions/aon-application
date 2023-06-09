package net.aonsolutions.infovox.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import net.aonsolutions.infovox.model.OCRResponse;
import net.aonsolutions.watson.client.util.AonCollectionUtils;
import net.aonsolutions.watson.server.AonObjectUtils;

public class OCRResponseJSON {
	
	private OCRResponseJSON() {
	}
	
	public static List<OCRResponse> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCRResponseJSON::from)
			.toList();		
	}
	
	public static OCRResponse from(JSONObject json) {
		if (json == null) return null; 
		return new OCRResponse()
			.setHttpCode(OCRJSONUtils.getInteger(json, OCRNames.HTTP_CODE))
			.setResult(OCRDocumentJSON.from(OCRJSONUtils.getObject(json, OCRNames.RESULT)))
			.setError(OCRErrorJSON.from(OCRJSONUtils.getObject(json, OCRNames.ERROR)))
		;
	}
	
	public static JSONArray to(List<OCRResponse> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRResponse> stream) {
		return stream
			.map(OCRResponseJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRResponse response) {
		if (response == null) return null;
		return new JSONObject()
			.putOpt(OCRNames.HTTP_CODE, response.getHttpCode().orElse(null))
			.putOpt(OCRNames.RESULT, AonObjectUtils.ifOptionalPresent(response.getResult(), OCRDocumentJSON::to ) )
			.putOpt(OCRNames.ERROR, AonObjectUtils.ifOptionalPresent(response.getError(), OCRErrorJSON::to ) )
			;
	}
}
