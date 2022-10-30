package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.registry.Segment;

public class SegmentJSON {
	
	private SegmentJSON() {
	
	}
	
	public static List<Segment> fromJSON(JSONArray json) {
		LinkedList<Segment> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Segment fromJSON(JSONObject json) {
		if(json.isNull(IJsonNames.DOMAIN)) 
			return new Segment();

		return new Segment()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
				.setName(JsonUtils.getString(json, IJsonNames.NAME));
	}
	
	public static JSONArray toJSON(List<Segment> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<Segment> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(toJSON(object)));
		return array;
	}
	
	
	public static JSONObject toJSON(Segment object) {
		return new JSONObject()
				.put(IJsonNames.ID, object.getId())
				.put(IJsonNames.DOMAIN, object.getDomain())
				.put(IJsonNames.NAME, object.getName());
	}
}
