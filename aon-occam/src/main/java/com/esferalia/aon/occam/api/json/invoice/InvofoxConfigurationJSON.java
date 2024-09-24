package com.esferalia.aon.occam.api.json.invoice;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.finance.InvofoxConfiguration;

public class InvofoxConfigurationJSON {

	private InvofoxConfigurationJSON() {

	}
	
	public static InvofoxConfiguration fromJSON(JSONObject json) {
		return new InvofoxConfiguration()
			.setPersonalized(JsonUtils.getboolean(json, IJsonNames.PERSONALIZED))
			.setApiKey(JsonUtils.getString(json, IJsonNames.API_KEY))
			.setApiUrl(JsonUtils.getString(json, IJsonNames.API_URL))
			.setEnvironment(JsonUtils.getString(json, IJsonNames.ENVIRONMENT))
			.setAutoAccept(JsonUtils.getboolean(json, IJsonNames.AUTO_ACCEPT))
			.setAutoRecord(JsonUtils.getboolean(json, IJsonNames.AUTO_RECORD));

	}
	
	public static JSONObject toJSON(InvofoxConfiguration config) {
		return new JSONObject()
			.put(IJsonNames.PERSONALIZED, config.isPersonalized())
			.put(IJsonNames.API_KEY, config.getApiKey())
			.put(IJsonNames.API_URL, config.getApiUrl())
			.put(IJsonNames.ENVIRONMENT, config.getEnvironment())
			.put(IJsonNames.AUTO_ACCEPT, config.isAutoAccept())
			.put(IJsonNames.AUTO_RECORD, config.isAutoRecord())
			.put(IJsonNames.LOGIN_REQUIRED, config.isLoginRequired());
	
	}
	
}
