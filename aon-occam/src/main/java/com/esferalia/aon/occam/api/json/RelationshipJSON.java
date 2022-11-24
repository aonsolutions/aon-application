package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Relationship;

public class RelationshipJSON {
	
	private RelationshipJSON() {
	
	}
	
	public static List<Relationship> fromJSON(JSONArray json) {
		LinkedList<Relationship> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Relationship fromJSON(JSONObject json) {
		if(json==null) return new Relationship();
		return new Relationship()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setDomain(DomainJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.DOMAIN)))
				.setDescription(JsonUtils.getString(json, IJsonNames.DESCRIPTION))
				;
	
	}
	
	public static JSONArray toJSON(List<Relationship> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<Relationship> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(toJSON(object)));
		return array;
	}
	
	
	public static JSONObject toJSON(Relationship object) {
		return new JSONObject()
			.put(IJsonNames.ID, object.getId())
			.put(IJsonNames.DOMAIN, DomainJSON.toJSON(object.getDomain()))
			.put(IJsonNames.DESCRIPTION, object.getDescription())
			;
	}
}
