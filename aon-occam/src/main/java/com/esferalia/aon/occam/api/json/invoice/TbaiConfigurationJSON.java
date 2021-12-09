package com.esferalia.aon.occam.api.json.invoice;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;

public class TbaiConfigurationJSON {

	private TbaiConfigurationJSON() {

	}
	
	public static TbaiConfiguration fromJSON(JSONObject json) {
		return new TbaiConfiguration()
				.setActive(JsonUtils.getboolean(json, IJsonNames.ACTIVE))
//				.setAdministration(Administration.safeValueOf(JsonUtils.getString(json, IJsonNames.ADMINISTRATION)))
				.setTest(JsonUtils.getboolean(json, IJsonNames.TEST));
	}
	
	public static JSONObject toJSON(TbaiConfiguration config) {
		return new JSONObject()
				.put(IJsonNames.ACTIVE, config.isActive())
				.put(IJsonNames.ADMINISTRATION, config.getAdministration().name())
				.put(IJsonNames.TEST, config.isTest());
	}
	
}
