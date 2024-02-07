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
			.setTest(JsonUtils.getboolean(json, IJsonNames.TEST));

	}
	
	public static JSONObject toJSON(InvofoxConfiguration config) {
		return new JSONObject()
			.put(IJsonNames.TEST, config.isTest());
	}
	
}
