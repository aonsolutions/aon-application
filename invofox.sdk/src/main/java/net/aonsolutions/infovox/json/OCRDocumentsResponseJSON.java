package net.aonsolutions.infovox.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.infovox.model.OCRDocumentsResponse;

public class OCRDocumentsResponseJSON {
	
	private OCRDocumentsResponseJSON() {
	}
	
	public static List<OCRDocumentsResponse> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCRDocumentsResponseJSON::from)
			.toList();		
	}
	
	public static OCRDocumentsResponse from(JSONObject json) {
		if (json == null) return null; 
		return new OCRDocumentsResponse()
			.setHttpCode(OCRJSONUtils.getInteger(json, OCRNames.HTTP_CODE))
			.setDocuments(OCRDocumentJSON.from(OCRJSONUtils.getArray(json, OCRNames.RESULT)))
			.setError(OCRErrorJSON.from(OCRJSONUtils.getObject(json, OCRNames.ERROR)))
			.setSkip(OCRJSONUtils.getInteger(json, OCRNames.SKIP))		
			.setLimit(OCRJSONUtils.getInteger(json, OCRNames.LIMIT)) 
			.setCount(OCRJSONUtils.getInteger(json, OCRNames.COUNT))
		;
	}
	
	public static JSONArray to(List<OCRDocumentsResponse> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRDocumentsResponse> stream) {
		return stream
			.map(OCRDocumentsResponseJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRDocumentsResponse response) {
		if (response == null) return null;
		return new JSONObject()
			.putOpt(OCRNames.HTTP_CODE, response.getHttpCode().orElse(null))
			.putOpt(OCRNames.RESULT, response.getDocuments().map(OCRDocumentJSON::to).orElse(null))
			.putOpt(OCRNames.ERROR, response.getError().map( OCRErrorJSON::to).orElse(null))
			;
	}
}
