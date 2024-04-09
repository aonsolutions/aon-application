package net.aonsolutions.invofox.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.invofox.model.OCRDocumentResponse;
import net.aonsolutions.invofox.model.OCRLoginTokenResponse;

public class OCRLoginTokenResponseJSON {
	
	private OCRLoginTokenResponseJSON() {
	}
	
	public static List<OCRLoginTokenResponse> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCRLoginTokenResponseJSON::from)
			.toList();		
	}
	
	public static OCRLoginTokenResponse from(JSONObject json) {
		if (json == null) return null; 
		return new OCRLoginTokenResponse()
			.setHttpCode(OCRJSONUtils.getInteger(json, OCRNames.HTTP_CODE))
			.setLoginToken(OCRLoginTokenJSON.from(OCRJSONUtils.getObject(json, OCRNames.RESULT)))
			.setError(OCRErrorJSON.from(OCRJSONUtils.getObject(json, OCRNames.ERROR)))
		;
	}
	
	public static JSONArray to(List<OCRLoginTokenResponse> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRLoginTokenResponse> stream) {
		return stream
			.map(OCRLoginTokenResponseJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRLoginTokenResponse response) {
		if (response == null) return null;
		return new JSONObject()
			.putOpt(OCRNames.HTTP_CODE, response.getHttpCode().orElse(null))
			.putOpt(OCRNames.RESULT, response.getLoginToken().map(OCRLoginTokenJSON::to).orElse(null))
			.putOpt(OCRNames.ERROR, response.getError().map(OCRErrorJSON::to).orElse(null))
			;
	}
}
