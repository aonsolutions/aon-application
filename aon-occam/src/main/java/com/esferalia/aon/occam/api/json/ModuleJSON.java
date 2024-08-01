package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;

import com.esferalia.aon.occam.api.model.Module;

public class ModuleJSON {

	public static LinkedList<Module> fromJSON(JSONArray array) {
		LinkedList<Module> list = new LinkedList<>();
		for(Integer i = 0; i < array.length(); i++) {
			list.add(Module.safeValueOf(array.getString(i)));
		}
 		return list;
	}
	
	public static Module fromJSON(String json) {
		return Module.safeValueOf(json);
	}
	
	public static JSONArray toJSON(List<Module> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<Module> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(object.name()));
		return array;
	}
	
	public static String toJSON(Module object) {
		return object.name();
	}
}
