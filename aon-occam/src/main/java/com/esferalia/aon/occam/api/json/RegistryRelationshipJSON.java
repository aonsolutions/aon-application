package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.registry.RegistryRelationship;

public class RegistryRelationshipJSON {
	
	private RegistryRelationshipJSON() {
	
	}
	
	public static List<RegistryRelationship> fromJSON(JSONArray json) {
		LinkedList<RegistryRelationship> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static RegistryRelationship fromJSON(JSONObject json) {
		if(json==null) return new RegistryRelationship();
		return new RegistryRelationship()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setDomain(DomainJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.DOMAIN)))
				.setRegistry(JsonUtils.getInteger(json, IJsonNames.REGISTRY))
				.setRelatedRegistry(JsonUtils.getInteger(json, "related_registry"))
				.setRelationship(RelationshipJSON.fromJSON(JsonUtils.getJSONObject(json, "relationship")))
				.setComments(JsonUtils.getString(json, IJsonNames.COMMENTS))
				.setRemoved(JsonUtils.getboolean(json, IJsonNames.REMOVED));
	
	}
	
	public static JSONArray toJSON(List<RegistryRelationship> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<RegistryRelationship> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(toJSON(object)));
		return array;
	}
	
	public static JSONObject toJSON(RegistryRelationship object) {
		return new JSONObject()
			.put(IJsonNames.ID, object.getId())
			.put(IJsonNames.DOMAIN, DomainJSON.toJSON(object.getDomain()))
			.put(IJsonNames.REGISTRY, object.getRegistry())
			.put(IJsonNames.COMMENTS, object.getComments())
			.put("related_registry",object.getRelatedRegistry())
			.put("relationship", RelationshipJSON.toJSON(object.getRelationship()))
			;
	}
}
