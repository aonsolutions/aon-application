package com.esferalia.aon.occam.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.MediaType;

public class RegistryMediaJSON {

	public static RegistryMedia fromJSON(JSONObject json) {
		return new RegistryMedia()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(json.optInt(IJsonNames.DOMAIN))
			.setMedia(MediaType.safeValueOf(json.optString(IJsonNames.TYPE)))
			.setValue(json.optString(IJsonNames.VALUE))
			.setComment(json.optString(IJsonNames.COMMENT))
			.setAdministrative(json.optBoolean(IJsonNames.ADMINISTRATIVE))
			.setAdministrative(json.optBoolean(IJsonNames.COMMERCIAL))
			.setAdministrative(json.optBoolean(IJsonNames.TECHNICAL))
			.setRegistry(JsonUtils.getInteger(json, IJsonNames.REGISTRY));
	
			// TODO .setRaddress();
	}
	
	public static JSONObject toJSON(RegistryMedia media) {
		if(media == null || media.isEmpty()) return new JSONObject();
		return new JSONObject()
			.put(IJsonNames.ID, media.getId())
			.put(IJsonNames.DOMAIN, media.getDomain())
			.put(IJsonNames.TYPE, media.getMedia().name().toLowerCase())
			.put(IJsonNames.VALUE, media.getValue())
			.put(IJsonNames.COMMENT, media.getComment())
			.put(IJsonNames.ADMINISTRATIVE, media.isAdministrative())
			.put(IJsonNames.COMMERCIAL, media.isCommercial())
			.put(IJsonNames.TECHNICAL, media.isTechnical())
			.put(IJsonNames.REGISTRY, media.getRegistry());
	}
	
}
