package com.esferalia.aon.occam.api.json.nordigen;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountDetail;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenCashAccountType;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenException;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class NordigenAccountDetailJSON {
	
	private NordigenAccountDetailJSON() {
	}
	
	public static NordigenAccountDetail fromAccount(String jsonString) {
		if (AonStringUtils.isNotBlank(jsonString)) {
			JSONObject account = new JSONObject(jsonString);
			return NordigenAccountDetailJSON.from( account.optJSONObject("account") );
		}
		return null;
	}

	public static NordigenAccountDetail from(String text) throws NordigenException {
		try {
			return from(new JSONObject(text));
		} catch (JSONException e) {
			throw new NordigenException(e.getMessage()); 
		} 
	}
	
	public static LinkedList<NordigenAccountDetail> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return NordigenJSONUtils.stream(array)
			.map(NordigenAccountDetailJSON::from)
			.collect(Collectors.toCollection(LinkedList::new));		
	}
	
	public static NordigenAccountDetail from(JSONObject json) {
		if (json == null) return null; 
		return new NordigenAccountDetail()
			.setResourceId(json.optString("id", null))
			.setIban(json.optString("iban", null))
			.setCurrency(json.optString("currency", null))
			.setOwnerName(json.optString("ownerName", null))
			.setName(json.optString("name", null))
			.setCashAccountType(NordigenCashAccountType.safeValueOf(json.optString("cashAccountType")))
			.setProduct(json.optString("product", null))
			.setStatus(json.optString("status", null))
			.setBic(json.optString("bic", null));
	}
	
	public static JSONArray to(List<NordigenAccountDetail> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}

	public static JSONArray to(Stream<NordigenAccountDetail> stream) {
		return stream
			.map(NordigenAccountDetailJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}

	public static JSONObject to(NordigenAccountDetail detail) {
		if (detail == null) return null;
		return new JSONObject()
			.put("id", detail.getResourceId())
			.put("iban", detail.getIban())
			.put("currency", detail.getCurrency())
			.put("ownerName", detail.getOwnerName())
			.put("name", detail.getName())
			.put("cashAccountType", detail.getCashAccountType() != null ? detail.getCashAccountType().toString() : null)
			.put("product", detail.getProduct())
			.put("status", detail.getStatus())
			.put("bic", detail.getBic());
	}
	
	
	
	
}
