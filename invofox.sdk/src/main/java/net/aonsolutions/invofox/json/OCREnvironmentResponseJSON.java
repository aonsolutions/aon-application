package net.aonsolutions.invofox.json;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import net.aonsolutions.invofox.model.OCREnvironment;
import net.aonsolutions.invofox.model.OCREnvironmentResponse;

public class OCREnvironmentResponseJSON {
	
	private OCREnvironmentResponseJSON() {
	}
	
	public static List<OCREnvironmentResponse> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCREnvironmentResponseJSON::from)
			.toList();		
	}
	
	public static OCREnvironmentResponse from(JSONObject json) {
		if (json == null) return null; 
		return new OCREnvironmentResponse()
			.setHttpCode(OCRJSONUtils.getInteger(json, OCRNames.HTTP_CODE))
			.setEnvironment(toJSON(json))
			.setError(OCRErrorJSON.from(OCRJSONUtils.getObject(json, OCRNames.ERROR)))
		;
	}
	
	private static OCREnvironment toJSON(JSONObject json) {
		JSONObject result = OCRJSONUtils.getObject(json, OCRNames.RESULT);
		OCREnvironment environment = new OCREnvironment();
		environment.setId(OCRJSONUtils.getString(result, OCRNames.ID));
		environment.setName(OCRJSONUtils.getString(result, OCRNames.NAME));
		JSONArray apikeys = OCRJSONUtils.getArray(result, "apiKeys");
		for(Integer j = 0; j < apikeys.length(); j++) {
			environment.addApiKey(OCRApiKeyJSON.from(apikeys.getJSONObject(j)));
		}
			
		JSONArray webhooks = OCRJSONUtils.getArray(result, OCRNames.WEBHOOKS);
		for(Integer k=0; k < webhooks.length(); k++) {
			environment.addWebhook(OCRWebhookJSON.from(webhooks.getJSONObject(k)));
		}

		return environment;
	}
}
