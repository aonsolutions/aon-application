package net.aonsolutions.occam.api.json;

import org.json.JSONObject;

import net.aonsolutions.occam.api.model.Iae;

public class IaeJSON {
	
	private IaeJSON() {
	
	}
	
	public static Iae fromJSON(JSONObject json) {
		if (JsonUtils.isEmpty(json)) return null;
		return new Iae()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setSection(JsonUtils.getString(json, IJsonNames.SECTION))
			.setEpigraph(JsonUtils.getString(json, IJsonNames.EPIGRAPH))
			.setTitle(JsonUtils.getString(json, IJsonNames.TITLE))
		;
	}
	
	public static JSONObject toJSON(Iae iae) {
		if(iae == null) return null;
		return new JSONObject()
			.put(IJsonNames.ID, iae.getId())
			.put(IJsonNames.SECTION, iae.getSection())
			.put(IJsonNames.EPIGRAPH, iae.getEpigraph())
			.put(IJsonNames.TITLE, iae.getTitle())
			;
	}
	
}
