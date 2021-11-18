package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.type.PayMethodType;

public class PayMethodJSON {

	private PayMethodJSON() {

	}
	
	public static List<PayMethod> fromJSON(JSONArray json) {
		LinkedList<PayMethod> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static PayMethod fromJSON(JSONObject json) {
		return new PayMethod()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
				.setType(PayMethodType.safeValueOf(JsonUtils.optString(json, IJsonNames.TYPE)))
				.setName(JsonUtils.getString(json, IJsonNames.NAME));
	}
	
	public static JSONArray toJSON(List<PayMethod> paymethods) {
		return toJSON(paymethods.stream());
	}
	
	public static JSONArray toJSON(Stream<PayMethod> paymethods) {
		JSONArray array = new JSONArray();
		paymethods.forEach(paymethod -> array.put(toJSON(paymethod)));
		return array;
	}
	
	public static JSONObject toJSON(PayMethod paymethod) {
		if(paymethod == null) return new JSONObject();
		return new JSONObject()
				.put(IJsonNames.ID, paymethod.getId())
				.put(IJsonNames.DOMAIN, paymethod.getDomain())
				.put(IJsonNames.TYPE, paymethod.getType().name())
				.put(IJsonNames.NAME, paymethod.getName());

	}
}
