package net.aonsolutions.invofox.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.invofox.model.OCRWebhookResponse;

public class OCRWebhookResponseJSON {
	
	private OCRWebhookResponseJSON() {
	}
	
	public static List<OCRWebhookResponse> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCRWebhookResponseJSON::from)
			.toList();		
	}
	
	public static OCRWebhookResponse from(JSONObject json) {
		if (json == null) return null; 
		return new OCRWebhookResponse()
			.setHttpCode(OCRJSONUtils.getInteger(json, OCRNames.HTTP_CODE))
			.setWebhook(OCRWebhookJSON.from(OCRJSONUtils.getObject(json, OCRNames.RESULT)))
			.setError(OCRErrorJSON.from(OCRJSONUtils.getObject(json, OCRNames.ERROR)))
		;
	}
	
	public static JSONArray to(List<OCRWebhookResponse> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRWebhookResponse> stream) {
		return stream
			.map(OCRWebhookResponseJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRWebhookResponse response) {
		if (response == null) return null;
		return new JSONObject()
			.putOpt(OCRNames.HTTP_CODE, response.getHttpCode().orElse(null))
			.putOpt(OCRNames.RESULT, response.getWebhook().map(OCRWebhookJSON::to).orElse(null))
			.putOpt(OCRNames.ERROR, response.getError().map(OCRErrorJSON::to).orElse(null))
			;
	}
}
