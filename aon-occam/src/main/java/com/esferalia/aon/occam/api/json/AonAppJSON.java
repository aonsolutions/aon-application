package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;

import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;

public class AonAppJSON {
	
	public static LinkedList<AonApp> fromJSON(JSONArray array) {
		LinkedList<AonApp> list = new LinkedList<>();
		for(Integer i = 0; i < array.length(); i++) {
			list.add(AonApp.safeValueOf(array.getString(i)));
		}
 		return list;
	}
	
	public static AonApp fromJSON(String json) {
		return AonApp.safeValueOf(json);
	}
	
	public static JSONArray toJSON(List<AonApp> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<AonApp> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(object.name()));
		return array;
	}
	
	public static String toJSON(AonApp object) {
		return object.name();
	}

}
