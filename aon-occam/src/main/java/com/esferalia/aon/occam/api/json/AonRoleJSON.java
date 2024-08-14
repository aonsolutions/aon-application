package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;

import com.esferalia.aon.occam.api.model.aonsolutions.AonRole;

public class AonRoleJSON {
	
	public static LinkedList<AonRole> fromJSON(JSONArray array) {
		LinkedList<AonRole> list = new LinkedList<>();
		for(Integer i = 0; i < array.length(); i++) {
			list.add(AonRole.safeValueOf(array.getString(i)));
		}
 		return list;
	}
	
	public static AonRole fromJSON(String json) {
		return AonRole.safeValueOf(json);
	}
	
	public static JSONArray toJSON(List<AonRole> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<AonRole> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(object.name()));
		return array;
	}
	
	public static String toJSON(AonRole object) {
		return object.name();
	}

}
