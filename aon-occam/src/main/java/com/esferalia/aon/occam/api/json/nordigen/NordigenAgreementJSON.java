package com.esferalia.aon.occam.api.json.nordigen;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAgreement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class NordigenAgreementJSON {
	private NordigenAgreementJSON() {
	}
	
	public static NordigenAgreement from(String text) throws NordigenException {
		try {
			return from(new JSONObject(text));
		} catch (JSONException e) {
			throw new NordigenException(e.getMessage()); 
		} 
	}
	
	public static LinkedList<NordigenAgreement> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return NordigenJSONUtils.stream(array)
			.map(NordigenAgreementJSON::from)
			.collect(Collectors.toCollection(LinkedList::new));		
	}
	
	public static NordigenAgreement from(JSONObject json) {
		if (json == null) return null; 
		return new NordigenAgreement()
			.setId(json.optString("id", null))
			.setCreated(AonDateUtils.parse(json.optString("created", null), AonDateUtils.DATE_TIME_FORMAT_AUX))
			.setMaxHistoricalDays(json.optInt("max_historical_days"))
			.setAccessValidForDays(json.optInt("access_valid_for_days"))
			.setAccessScope(NordigenJSONUtils.accessScopesFromJSON(json.optJSONArray("access_scope")))
			.setAccepted(AonDateUtils.parse(json.optString("accepted", null), AonDateUtils.DATE_TIME_FORMAT_AUX))
			.setInstitutionId(json.optString("institution_id"));
	}
	
	public static JSONArray to(List<NordigenAgreement> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}

	public static JSONArray to(Stream<NordigenAgreement> stream) {
		return stream
			.map(NordigenAgreementJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}

	public static JSONObject to(NordigenAgreement agreement) {
		if (agreement == null) return null;
		return new JSONObject()
			.put("id", agreement.getId())
			.put("created", AonDateUtils.format(agreement.getCreated(), AonDateUtils.DATE_TIME_FORMAT_AUX))
			.put("max_historical_days", agreement.getMaxHistoricalDays())
			.put("access_valid_for_days", agreement.getAccessValidForDays())
			.put("access_scope", NordigenJSONUtils.accessScopesToJSON(agreement.getAccessScope()))
			.put("accepted", AonDateUtils.format(agreement.getAccepted(), AonDateUtils.DATE_TIME_FORMAT_AUX))
			.put("institution_id", agreement.getInstitutionId());
	}
	
}
