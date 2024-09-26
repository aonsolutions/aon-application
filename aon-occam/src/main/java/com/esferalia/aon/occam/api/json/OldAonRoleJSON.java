package com.esferalia.aon.occam.api.json;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;

import com.esferalia.aon.occam.api.model.type.OldAonRole;

public class OldAonRoleJSON {
	
	public static LinkedList<OldAonRole> fromJSON(JSONArray array) {
		LinkedList<OldAonRole> list = new LinkedList<>();
		for(Integer i = 0; i < array.length(); i++) {
			list.add(OldAonRole.safeValueOf(array.getString(i)));
		}
 		return list;
	}
	
	public static OldAonRole fromJSON(String json) {
		return OldAonRole.safeValueOf(json);
	}

	public static JSONArray toJSON(OldAonRole[] array) {
		List<OldAonRole> list = array != null ? Arrays.asList(array) : new LinkedList<>();
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(List<OldAonRole> list) {
		if(list == null) list = new LinkedList<>();
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<OldAonRole> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(object.name()));
		return array;
	}
	
	public static String toJSON(OldAonRole object) {
		return object.name();
	}

}
