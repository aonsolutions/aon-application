package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.registry.RProfile;

public class RegistryProfileJSON {
	
	private RegistryProfileJSON() {
	
	}
	
	public static List<RProfile> fromJSON(JSONArray json) {
		LinkedList<RProfile> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static RProfile fromJSON(JSONObject json) {
		return new RProfile()
				.setQuestionAlias(JsonUtils.getString(json, IJsonNames.QUESTION_ALIAS))
				.setValue(JsonUtils.getString(json, IJsonNames.VALUE))
				;
	}
	
	public static JSONArray toJSON(List<RProfile> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<RProfile> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(toJSON(object)));
		return array;
	}
	
	
	public static JSONObject toJSON(RProfile object) {
		return new JSONObject()
			.put(IJsonNames.QUESTION_ALIAS, object.getQuestionAlias())
			.put(IJsonNames.VALUE, object.getValue())
			;
	}
}
