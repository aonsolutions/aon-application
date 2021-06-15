package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.registry.RegistryPayMethod;

public class RegistryPaymethodJSON {

	public static LinkedList<RegistryPayMethod> fromJSON(JSONArray json) {
		LinkedList<RegistryPayMethod> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static RegistryPayMethod fromJSON(JSONObject json) {
		return new RegistryPayMethod()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID));
	}
	
	public static JSONArray toJSON(LinkedList<RegistryPayMethod> rpaymethods) {
		return toJSON(rpaymethods.stream());
	}
	
	public static JSONArray toJSON(Stream<RegistryPayMethod> rpaymethods) {
		JSONArray array = new JSONArray();
		rpaymethods.forEach(rpaymethod -> array.put(toJSON(rpaymethod)));
		return array;
	}
	
	public static JSONObject toJSON(RegistryPayMethod rbank) {
		return new JSONObject()
				.put(IJsonNames.ID, rbank.getId())
				.put(IJsonNames.DOMAIN, rbank.getDomain());
	}
}
