package com.esferalia.aon.occam.api.json.invoice;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.finance.VerifactuConfiguration;
import com.esferalia.aon.watson.server.AonDateUtils;

public class VerifactuConfigurationJSON {

	private VerifactuConfigurationJSON() {

	}
	
	public static VerifactuConfiguration fromJSON(JSONObject json) {
		return new VerifactuConfiguration()
				.setActive(JsonUtils.getboolean(json, IJsonNames.ACTIVE))
				.setTest(JsonUtils.getboolean(json, IJsonNames.TEST))
				.setIncludeDate(JsonUtils.getDate(json, IJsonNames.INCLUDE_DATE))
				.setRegistryDate(JsonUtils.getString(json, IJsonNames.REGISTRY_DATE));

	}
	
	public static JSONObject toJSON(VerifactuConfiguration config) {
		return new JSONObject()
				.put(IJsonNames.ACTIVE, config.isActive())
				.put(IJsonNames.TEST, config.isTest())
				.put(IJsonNames.INCLUDE_DATE, AonDateUtils.format(config.getIncludeDate(), "yyyy-MM-dd"))
				.put(IJsonNames.REGISTRY_DATE, config.getRegistryDate());
	}
	
}
