package net.aonsolutions.occam.api.json;

import org.json.JSONObject;

import net.aonsolutions.occam.api.model.Cnae;

public class CnaeJSON {
	
	private CnaeJSON() {
	
	}
	
	public static Cnae fromJSON(JSONObject json) {
		if (JsonUtils.isEmpty(json)) return null;
		return new Cnae()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setCode(JsonUtils.getString(json, IJsonNames.CODE))
			.setTitle(JsonUtils.getString(json, IJsonNames.TITLE))
		;
	}
	
	public static JSONObject toJSON(Cnae cnae) {
		if (cnae == null) return null;
		return new JSONObject()
			.put(IJsonNames.ID, cnae.getId())
			.put(IJsonNames.CODE, cnae.getCode())
			.put(IJsonNames.TITLE, cnae.getTitle())
			;
	}
	
}
