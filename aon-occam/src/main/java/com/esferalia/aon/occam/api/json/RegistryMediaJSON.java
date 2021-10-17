package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.MediaType;

public class RegistryMediaJSON {
	
	private RegistryMediaJSON() {
	
	}

	public static RegistryMedia fromJSON(JSONObject json) {
		return new RegistryMedia()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(json.optInt(IJsonNames.DOMAIN))
			.setMedia(MediaType.safeValueOf(json.optString(IJsonNames.MEDIA)))
			.setValue(json.optString(IJsonNames.VALUE))
			.setComment(json.optString(IJsonNames.COMMENT))
			.setAdministrative(json.optBoolean(IJsonNames.ADMINISTRATIVE))
			.setCommercial(json.optBoolean(IJsonNames.COMMERCIAL))
			.setTechnical(json.optBoolean(IJsonNames.TECHNICAL))
			.setRegistry(JsonUtils.getInteger(json, IJsonNames.REGISTRY))
			.setDirty(JsonUtils.getboolean(json, IJsonNames.DIRTY))
			.setRemoved(JsonUtils.getboolean(json, IJsonNames.REMOVED));
			// TODO .setRaddress();
	}
	
	public static List<RegistryMedia> fromJSON(JSONArray json) {
		LinkedList<RegistryMedia> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static JSONArray toJSON(List<RegistryMedia> rmedias) {
		return toJSON(rmedias.stream());
	}
	
	public static JSONArray toJSON(Stream<RegistryMedia> rmedias) {
		JSONArray array = new JSONArray();
		rmedias.forEach(rmedia -> array.put(toJSON(rmedia)));
		return array;
	}
	
	public static JSONObject toJSON(RegistryMedia media) {
		if(media == null || media.isEmpty()) return new JSONObject();
		return new JSONObject()
			.put(IJsonNames.ID, media.getId())
			.put(IJsonNames.DOMAIN, media.getDomain())
			.put(IJsonNames.MEDIA, media.getMedia().name().toLowerCase())
			.put(IJsonNames.VALUE, media.getValue())
			.put(IJsonNames.COMMENT, media.getComment())
			.put(IJsonNames.ADMINISTRATIVE, media.isAdministrative())
			.put(IJsonNames.COMMERCIAL, media.isCommercial())
			.put(IJsonNames.TECHNICAL, media.isTechnical())
			.put(IJsonNames.REGISTRY, media.getRegistry())
			.put(IJsonNames.DIRTY, media.isDirty())
			.put(IJsonNames.REMOVED, media.isRemoved());
	}
	
}
