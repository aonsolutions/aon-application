package com.esferalia.aon.occam.api.json.nordigen;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenException;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAgreements;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class NordigenAgreementsJSON {
	private NordigenAgreementsJSON() {
	}
	
	public static NordigenAgreements from(String text) throws NordigenException {
		try {
			return from(new JSONObject(text));
		} catch (JSONException e) {
			throw new NordigenException(e.getMessage()); 
		} 
	}
	
	public static List<NordigenAgreements> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return NordigenJSONUtils.stream(array)
			.map(NordigenAgreementsJSON::from)
			.toList();		
	}
	
	public static NordigenAgreements from(JSONObject json) {
		if (json == null) return null; 
		return new NordigenAgreements()
			.setCount(AonNumberUtils.toInteger(json.optString("count", null)))
			.setNext(json.optString("next", null))
			.setPrevious(json.optString("previous", null))
			.setResult( NordigenAgreementJSON.from(json.optJSONArray("results")) )
			;
	}
	
	public static JSONArray to(List<NordigenAgreements> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}

	public static JSONArray to(Stream<NordigenAgreements> stream) {
		return stream
			.map(NordigenAgreementsJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}

	public static JSONObject to(NordigenAgreements agreements) {
		if (agreements == null) return null;
		return new JSONObject()
			.put("count", agreements.getCount())
			.put("next", agreements.getNext())
			.put("previous", agreements.getPrevious())
			.put("results", NordigenAgreementJSON.to(agreements.getResult()))
			;
	}
}
