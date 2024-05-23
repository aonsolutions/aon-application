package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.security.Scope;

public class ScopeJSON {
	
	private ScopeJSON() {
	
	}
	
	public static List<Scope> fromJSON(JSONArray json) {
		LinkedList<Scope> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Scope fromJSON(JSONObject json) {
		return new Scope()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
				.setDescription(JsonUtils.getString(json, IJsonNames.DESCRIPTION));
	}
	
	public static JSONArray toJSON(List<Scope> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<Scope> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(toJSON(object)));
		return array;
	}
	
	
	public static JSONObject toJSON(Scope object) {
		if(object == null) return null;
		return new JSONObject()
				.put(IJsonNames.ID, object.getId())
				.put(IJsonNames.DOMAIN, object.getDomain())
				.put(IJsonNames.DESCRIPTION, object.getDescription())
				.put(IJsonNames.NAME, object.getDescription()); // DEPRECATED use desription.
	}
}
