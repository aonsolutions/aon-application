package com.esferalia.aon.occam.api.json.nordigen;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenException;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisitions;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class NordigenRequisitionsJSON {
	private NordigenRequisitionsJSON() {
	}
	
	public static NordigenRequisitions from(String text) throws NordigenException {
		try {
			return from(new JSONObject(text));
		} catch (JSONException e) {
			throw new NordigenException(e.getMessage()); 
		} 
	}
	
	public static LinkedList<NordigenRequisitions> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return NordigenJSONUtils.stream(array)
			.map(NordigenRequisitionsJSON::from)
			.collect(Collectors.toCollection(LinkedList::new));		
	}
	
	public static NordigenRequisitions from(JSONObject json) {
		if (json == null) return null; 
		return new NordigenRequisitions()
			.setCount(AonNumberUtils.toInteger(json.optString("count", null)))
			.setNext(json.optString("next", null))
			.setPrevious(json.optString("previous", null))
			.setResult( NordigenRequisitionJSON.from(json.optJSONArray("results")) )
			;
	}
	
	public static JSONArray to(List<NordigenRequisitions> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}

	public static JSONArray to(Stream<NordigenRequisitions> stream) {
		return stream
			.map(NordigenRequisitionsJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}

	public static JSONObject to(NordigenRequisitions requisitions) {
		if (requisitions == null) return null;
		return new JSONObject()
			.put("count", requisitions.getCount())
			.put("next", requisitions.getNext())
			.put("previous", requisitions.getPrevious())
			.put("results", NordigenRequisitionJSON.to(requisitions.getResult()))
			;
	}
}
