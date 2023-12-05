package com.esferalia.aon.occam.api.json.nordigen;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenException;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenResponse;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class NordigenResponseJSON {
	
	private NordigenResponseJSON() {
	}
	
	public static NordigenResponse from(String text) throws NordigenException {
		try {
			return from(new JSONObject(text));
		} catch (JSONException e) {
			throw new NordigenException(e.getMessage()); 
		} 
	}
	
	public static List<NordigenResponse> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return NordigenJSONUtils.stream(array)
			.map(NordigenResponseJSON::from)
			.toList();		
	}
	
	public static NordigenResponse from(JSONObject json) {
		if (json == null) return null; 
		return new NordigenResponse()
			.setSummary( json.optString("summary"))
			.setDetail(json.optString("detail"))
			.setType(json.optString("type"))
			.setCountry(json.optString("country"))
			.setStatusCode(json.optInt("status_code"));
	}
	
	public static JSONArray to(List<NordigenResponse> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}

	public static JSONArray to(Stream<NordigenResponse> stream) {
		return stream
			.map(NordigenResponseJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}

	public static JSONObject to(NordigenResponse response) {
		if (response == null) return null;
		return new JSONObject()
			.put("summary", response.getSummary())
			.put("detail", response.getDetail())
			.put("type", response.getType())
			.put("country", response.getCountry())
			.put("status_code", response.getStatusCode())
			;
	}
}


