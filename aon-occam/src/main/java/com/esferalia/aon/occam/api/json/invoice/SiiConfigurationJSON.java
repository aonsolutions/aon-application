package com.esferalia.aon.occam.api.json.invoice;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.finance.SiiConfiguration;
import com.esferalia.aon.watson.server.AonDateUtils;

public class SiiConfigurationJSON {

	private SiiConfigurationJSON() {

	}
	
	public static SiiConfiguration fromJSON(JSONObject json) {
		return new SiiConfiguration()
				.setActive(JsonUtils.getboolean(json, IJsonNames.ACTIVE))
//				.setAdministration(Administration.safeValueOf(JsonUtils.getString(json, IJsonNames.ADMINISTRATION)))
				.setTest(JsonUtils.getboolean(json, IJsonNames.TEST))
				.setAutosend(JsonUtils.getboolean(json, "autosend"))
				.setIncludeDate(JsonUtils.getDate(json, "includeDate"))
				.setRegistryDate(JsonUtils.getString(json, "registryDate"));
	}
	
	public static JSONObject toJSON(SiiConfiguration config) {
		return new JSONObject()
				.put(IJsonNames.ACTIVE, config.isActive())
				.put(IJsonNames.ADMINISTRATION, config.getAdministration().name())
				.put(IJsonNames.TEST, config.isTest())
				.put("autosend", config.isAutosend())
				.put("includeDate", AonDateUtils.format(config.getIncludeDate(), "yyyy-MM-dd"))
				.put("registryDate", config.getRegistryDate());
	}
	
}
