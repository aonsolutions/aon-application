package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.type.CarrierStatus;

public class CarrierJSON {
	
	private CarrierJSON() {
	
	}
	
	public static List<Carrier> fromJSON(JSONArray json) {
		LinkedList<Carrier> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	
	public static Carrier fromJSON(JSONObject json) {
		return new Carrier()
			.copy(RegistryJSON.fromJSON(json))
			.setScope(ScopeJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.SCOPE)))
			.setStatus(CarrierStatus.safeValueOf(JsonUtils.getString(json, IJsonNames.STATUS)));
	}
	

	public static JSONArray toJSON(List<Carrier> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<Carrier> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(toJSON(object)));
		return array;
	}
	
	public static JSONObject toJSON(Carrier object) {
		return RegistryJSON.toJSON(object)
			.put(IJsonNames.SCOPE, object.getScope())
			.put(IJsonNames.STATUS, object.getStatus().name());
	}
}
