package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.EnterpriseData;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.watson.server.AonDateUtils;

public class EnterpriseDataJSON {

	private EnterpriseDataJSON() {
	
	}
	
	public static List<EnterpriseData> fromJSON(JSONArray array) {
		if(array == null) return new LinkedList<>();
		LinkedList<EnterpriseData> list = new LinkedList<>();
		for(Integer i = 0; i < array.length(); i++) {
			list.add(fromJSON(array.getJSONObject(i)));
		}
 		return list;
	}
	
	public static EnterpriseData fromJSON(JSONObject json) {
		if(json == null || json.isEmpty()) return null;
		return new EnterpriseData()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
				.setEnterprise(JsonUtils.getInteger(json, IJsonNames.ENTERPRISE))
				.setName(JsonUtils.getString(json, IJsonNames.NAME))
				.setExpression(JsonUtils.getString(json, IJsonNames.EXPRESSION))
				.setStartDate(JsonUtils.getDate(json, IJsonNames.START_DATE))
				.setEndDate(JsonUtils.getDate(json, IJsonNames.END_DATE))
				.setDeleted(JsonUtils.getboolean(json, IJsonNames.REMOVED));
	}
	
	public static <T extends EnterpriseData> JSONArray toJSON(List<T> list) {
		if(list == null) return new JSONArray();
		return toJSON(list.stream());
	}
	
	public static <T extends EnterpriseData> JSONArray toJSON(Stream<T> stream) {
		if(stream == null) return new JSONArray();
		JSONArray array = new JSONArray();
		stream.forEach(data -> array.put(toJSON(data)));
		return array;
	}
	
	public static JSONObject toJSON(EnterpriseData data) {
		if(data == null) return null;
		return new JSONObject()
			.put(IJsonNames.ID, data.getId())
			.put(IJsonNames.DOMAIN, data.getDomain())
			.put(IJsonNames.ENTERPRISE, data.getEnterprise())
			.put(IJsonNames.NAME, data.getName())
			.put(IJsonNames.EXPRESSION, data.getExpression())
			.put(IJsonNames.START_DATE, AonDateUtils.format(data.getStartDate(), AonDateUtils.SIMPLE_DATE_FORMAT4))
			.put(IJsonNames.END_DATE, AonDateUtils.format(data.getEndDate(), AonDateUtils.SIMPLE_DATE_FORMAT4));	
	}	
}
