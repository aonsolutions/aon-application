package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.registry.RegistrySegment;

public class RegistrySegmentJSON {
	
	private RegistrySegmentJSON() {
	
	}
	
	public static List<RegistrySegment> fromJSON(JSONArray json) {
		LinkedList<RegistrySegment> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static RegistrySegment fromJSON(JSONObject json) {
		return new RegistrySegment()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setDomain(DomainJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.DOMAIN)))
				.setSegment(SegmentJSON.fromJSON(JsonUtils.getJSONObject(json, "segment")))
				;
	}
	
	public static JSONArray toJSON(List<RegistrySegment> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<RegistrySegment> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(toJSON(object)));
		return array;
	}
	
	
	public static JSONObject toJSON(RegistrySegment object) {
		return new JSONObject()
			.put(IJsonNames.ID, object.getId())
			.put(IJsonNames.DOMAIN, DomainJSON.toJSON(object.getDomain()))
			.put(IJsonNames.REGISTRY, object.getRegistry())
			.put("segment", SegmentJSON.toJSON(object.getSegment()))
			;
	}
}
