package net.aonsolutions.invofox.json;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.invofox.model.OCRWebhook;

public class OCRWebhookJSON {
	
	private OCRWebhookJSON() {
	}
	
	public static List<OCRWebhook> from(JSONArray array) {
		if (array == null || array.isEmpty()) return Collections.emptyList();
		return OCRJSONUtils.stream(array)
			.map(OCRWebhookJSON::from)
			.toList();		
	}
	
	public static OCRWebhook from(JSONObject json) {
		if (json == null) return null; 
		return new OCRWebhook()
			.setId(OCRJSONUtils.getString(json, OCRNames.ID))
			.setEndpoint(OCREndpointJSON.from(OCRJSONUtils.getObject(json, OCRNames.ENDPOINT)));
	}
	
	public static JSONArray to(List<OCRWebhook> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRWebhook> stream) {
		return stream
			.map(OCRWebhookJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRWebhook webhook) {
		if (webhook == null) return null;
		JSONArray events = new JSONArray();
		webhook.getEvents().stream().forEach(r -> events.put(r.getName()));
		return new JSONObject()
				.put(OCRNames.ID, webhook.getId())
				.put(OCRNames.ENDPOINT, OCREndpointJSON.to(webhook.getEndpoint()))
				.put(OCRNames.EVENTS, events)
				.put(OCRNames.SECURITY, OCRSecurityJSON.to(webhook.getSecurity()))
				.put(OCRNames.STATE, "active")
		;
	}

	public static List<OCRWebhook> from(Object rawObject) {
		if (rawObject instanceof JSONArray) {
			return from((JSONArray) rawObject);
		}
		if (rawObject instanceof JSONObject) {
			LinkedList<OCRWebhook> list = new LinkedList<>();
			list.add(from( (JSONObject) rawObject));
			return list;
		}
		return Collections.emptyList();
	}
}
