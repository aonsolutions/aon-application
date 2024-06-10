package net.aonsolutions.invofox.json;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import net.aonsolutions.invofox.model.OCREnvironment;
import net.aonsolutions.invofox.model.OCREnvironmentsResponse;

public class OCREnvironmentsResponseJSON {
	
	private OCREnvironmentsResponseJSON() {
	}
	
	public static List<OCREnvironmentsResponse> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCREnvironmentsResponseJSON::from)
			.toList();		
	}
	
	public static OCREnvironmentsResponse from(JSONObject json) {
		if (json == null) return null; 
		return new OCREnvironmentsResponse()
			.setHttpCode(OCRJSONUtils.getInteger(json, OCRNames.HTTP_CODE))
			.setEnvironments(toJSON(json))
			.setError(OCRErrorJSON.from(OCRJSONUtils.getObject(json, OCRNames.ERROR)))
		;
	}
	
	private static List<OCREnvironment> toJSON(JSONObject json) {
		List<OCREnvironment> list = new LinkedList<>();
		JSONObject result = OCRJSONUtils.getObject(json, OCRNames.RESULT);
		JSONArray envs = OCRJSONUtils.getArray(result, OCRNames.ENVS);
		for(Integer i =0; i < envs.length(); i++) {
			OCREnvironment environment = new OCREnvironment();
			JSONObject env = envs.getJSONObject(i);
			environment.setId(OCRJSONUtils.getString(env, OCRNames.ID));
			environment.setName(OCRJSONUtils.getString(env, OCRNames.NAME));
			JSONArray apikeys = OCRJSONUtils.getArray(env, "apiKeys");
			for(Integer j = 0; j < apikeys.length(); j++) {
				environment.addApiKey(OCRApiKeyJSON.from(apikeys.getJSONObject(j)));
			}
			
			JSONArray webhooks = OCRJSONUtils.getArray(env, OCRNames.WEBHOOKS);
			for(Integer k=0; k < webhooks.length(); k++) {
				environment.addWebhook(OCRWebhookJSON.from(webhooks.getJSONObject(k)));
			}
			list.add(environment);
		}
			
		

		
		return list;
	}
}
