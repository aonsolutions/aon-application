package net.aonsolutions.occam.api.json;

import org.json.JSONObject;

import net.aonsolutions.occam.api.model.Geozone;

public class GeozoneJSON {
	
	private GeozoneJSON() {
	
	}
	
	public static Geozone fromJSON(JSONObject json) {
		if (JsonUtils.isEmpty(json)) return null;
		return new Geozone()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setName(JsonUtils.getString(json, IJsonNames.NAME))
			.setCode(JsonUtils.getString(json, IJsonNames.CODE))
			.setSystem(JsonUtils.getboolean(json, IJsonNames.SYSTEM))
		;
	}
	
	public static JSONObject toJSON(Geozone geozone) {
		if(geozone == null) return null;
		return new JSONObject()
			.put(IJsonNames.ID, geozone.getId())
			.put(IJsonNames.DOMAIN, geozone.getDomain())
			.put(IJsonNames.NAME, geozone.getName())
			.put(IJsonNames.CODE, geozone.getCode())
			.put(IJsonNames.SYSTEM, geozone.isSystem())
			;
	}
	
}
