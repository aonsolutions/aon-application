package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;

public class WarehouseJSON {
	
	private WarehouseJSON() {
	
	}
	
	public static List<Warehouse> fromJSON(JSONArray json) {
		LinkedList<Warehouse> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Warehouse fromJSON(JSONObject json) {
		return new Warehouse()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setDepartment(JsonUtils.getInteger(json, IJsonNames.DEPARTMENT))
			.setName(JsonUtils.getString(json, IJsonNames.NAME))
			.setWorkplace(JsonUtils.getInteger(json, IJsonNames.WORKPLACE))
			.setActive(JsonUtils.getboolean(json, IJsonNames.ACTIVE))
			;
	}
	
	public static JSONArray toJSON(List<Warehouse> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<Warehouse> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(toJSON(object)));
		return array;
	}
	
	public static JSONObject toJSON(Warehouse object) {
		if(object == null) return new JSONObject();
		return new JSONObject()
			.put(IJsonNames.ID, object.getId())
			.put(IJsonNames.DOMAIN, object.getDomain())
			.put(IJsonNames.DEPARTMENT, object.getDepartment())
			.put(IJsonNames.NAME, object.getName())
			.put(IJsonNames.WORKPLACE, object.getWorkplace())
			.put(IJsonNames.ACTIVE, object.isActive())
			;
	}
}
