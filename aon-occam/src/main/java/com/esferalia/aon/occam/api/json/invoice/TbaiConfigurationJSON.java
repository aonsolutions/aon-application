package com.esferalia.aon.occam.api.json.invoice;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.type.Administration;

public class TbaiConfigurationJSON {

	private TbaiConfigurationJSON() {

	}
	
	public static TbaiConfiguration fromJSON(JSONObject json) {
		return new TbaiConfiguration()
				.setActive(JsonUtils.getboolean(json, IJsonNames.ACTIVE))
//				.setAdministration(Administration.safeValueOf(IJsonNames.ADMINISTRATION))
//				.setDefaultCertificate(JsonUtils.getInteger(json, IJsonNames.DEFAULT_CERTIFICATE))
				.setTest(JsonUtils.getboolean(json, IJsonNames.TEST));
	}
	
	public static JSONObject toJSON(TbaiConfiguration config) {
		return new JSONObject()
				.put(IJsonNames.ACTIVE, config.isActive())
//				.put(IJsonNames.ADMINISTRATION, config.getAdministration().name())
//				.put(IJsonNames.DEFAULT_CERTIFICATE, config.getDefaultCertificate())
				.put(IJsonNames.TEST, config.isTest());
	}
	
}
