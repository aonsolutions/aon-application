package com.esferalia.aon.occam.api.json.nordigen;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenException;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class NordigenAccessTokenJSON {
	
	private NordigenAccessTokenJSON() {
	}
	
	public static NordigenAccessToken from(String text) throws NordigenException {
		try {
			return from(new JSONObject(text));
		} catch (JSONException e) {
			throw new NordigenException(e.getMessage()); 
		} 
	}
	
	public static List<NordigenAccessToken> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return NordigenJSONUtils.stream(array)
			.map(NordigenAccessTokenJSON::from)
			.toList();		
	}
	
	public static NordigenAccessToken from(JSONObject json) {
		if (json == null) return null; 
		return new NordigenAccessToken()
			.setAccess(AonStringUtils.trimToNull(json.optString("access")))
			.setAccessExpires(json.isNull("access_expires") ? null : json.optLong("access_expires"))
			.setRefresh(AonStringUtils.trimToNull(json.optString("refresh")))
			.setRefreshExpires(json.isNull("refresh_expires") ? null : json.optLong("refresh_expires"));
	}
	
	public static JSONArray to(List<NordigenAccessToken> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}

	public static JSONArray to(Stream<NordigenAccessToken> stream) {
		return stream
			.map(NordigenAccessTokenJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}

	public static JSONObject to(NordigenAccessToken token) {
		if (token == null) return null;
		return new JSONObject()
			.put("access", token.getAccess())
			.put("access_expires", token.getAccessExpires())
			.put("refresh", token.getRefresh())
			.put("refresh_expires", token.getRefreshExpires())
			;
	}
}
