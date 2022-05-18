package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.commission.CommissionType;

public class CommissionTypeJSON {
	
	private CommissionTypeJSON() {
	
	}
	
	public static List<CommissionType> fromJSON(JSONArray json) {
		LinkedList<CommissionType> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	
	public static CommissionType fromJSON(JSONObject json) {
		return new CommissionType()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setName(JsonUtils.getString(json, IJsonNames.NAME))
			.setRate(JsonUtils.getdouble(json, IJsonNames.RATE));
	}
	

	public static JSONArray toJSON(List<CommissionType> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<CommissionType> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(toJSON(object)));
		return array;
	}
	
	public static JSONObject toJSON(CommissionType object) {
		return new JSONObject()
			.put(IJsonNames.ID, object.getId())
			.put(IJsonNames.DOMAIN, object.getDomain())
			.put(IJsonNames.NAME, object.getName())
			.put(IJsonNames.RATE, object.getRate());
	}
}
