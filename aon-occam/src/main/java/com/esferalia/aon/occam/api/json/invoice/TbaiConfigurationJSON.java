package com.esferalia.aon.occam.api.json.invoice;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.watson.server.AonDateUtils;

public class TbaiConfigurationJSON {

	private TbaiConfigurationJSON() {

	}
	
	public static TbaiConfiguration fromJSON(JSONObject json) {
		return new TbaiConfiguration()
				.setActive(JsonUtils.getboolean(json, IJsonNames.ACTIVE))
//				.setAdministration(Administration.safeValueOf(JsonUtils.getString(json, IJsonNames.ADMINISTRATION)))
				.setTest(JsonUtils.getboolean(json, IJsonNames.TEST))
				.setIncludeDate(JsonUtils.getDate(json, IJsonNames.INCLUDE_DATE))
				.setRegistryDate(JsonUtils.getString(json, IJsonNames.REGISTRY_DATE));

	}
	
	public static JSONObject toJSON(TbaiConfiguration config) {
		return new JSONObject()
				.put(IJsonNames.ACTIVE, config.isActive())
				.put(IJsonNames.ADMINISTRATION, config.getAdministration().name())
				.put(IJsonNames.TEST, config.isTest())
				.put(IJsonNames.INCLUDE_DATE, AonDateUtils.format(config.getIncludeDate(), "yyyy-MM-dd"))
				.put(IJsonNames.REGISTRY_DATE, config.getRegistryDate());
	}
	
}
