package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.IJsonNames;

public class AppParamJSON {
	
	public static LinkedList<ApplicationParameter> fromJSON(JSONArray json) {
		LinkedList<ApplicationParameter> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static ApplicationParameter fromJSON(JSONObject json) {
		return new ApplicationParameter()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setName(JsonUtils.getString(json, IJsonNames.NAME))
			.setValue(JsonUtils.getString(json, IJsonNames.VALUE))
			;
	}
	
	public static JSONArray toJSON(List<ApplicationParameter> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<ApplicationParameter> notes) {
		JSONArray array = new JSONArray();
		notes.forEach(note -> array.put(toJSON(note)));
		return array;
	}
	
	public static JSONObject toJSON(ApplicationParameter appParam) {
		return new JSONObject()
			.put(IJsonNames.ID, appParam.getId())
			.put(IJsonNames.DOMAIN, appParam.getDomain())
			.put(IJsonNames.NAME, appParam.getName())
			.put(IJsonNames.VALUE, appParam.getValue())
			;
	}

}
