package com.esferalia.aon.occam.api.json.nordigen;

import static com.esferalia.aon.occam.api.json.nordigen.NordigenJSONUtils.countryJSONArrayToSet;
import static com.esferalia.aon.occam.api.json.nordigen.NordigenJSONUtils.countryJSONSetToArray;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenException;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenInstitution;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class NordigenInstitutionJSON {
	
	private NordigenInstitutionJSON() {
	}
	
	public static List<NordigenInstitution> fromArray(String text) throws NordigenException {
		try {
			return from(new JSONArray(text));
		} catch (JSONException e) {
			throw new NordigenException(e.getMessage()); 
		} 
	}
	
	public static NordigenInstitution from(String text) throws NordigenException {
		try {
			return from(new JSONObject(text));
		} catch (JSONException e) {
			throw new NordigenException(e.getMessage()); 
		} 
	}
	
	public static List<NordigenInstitution> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return NordigenJSONUtils.stream(array)
			.map(NordigenInstitutionJSON::from)
			.toList();		
	}
	
	public static NordigenInstitution from(JSONObject json) {
		if (json == null) return null; 
		return new NordigenInstitution()
			.setId(json.optString("id", null))
			.setName(json.optString("name", null))
			.setBic(json.optString("bic", null))
			.setTransactionTotalDays(AonNumberUtils.toInteger(json.optString("transaction_total_days", null)))
			.setCountries(countryJSONArrayToSet(json.optJSONArray("countries")))
			.setLogo(json.optString("logo", null));
	}
	
	public static JSONArray to(List<NordigenInstitution> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}

	public static JSONArray to(Stream<NordigenInstitution> stream) {
		return stream
			.map(NordigenInstitutionJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}

	public static JSONObject to(NordigenInstitution institution) {
		if (institution == null) return null;
		return new JSONObject()
			.put("id", institution.getId())
			.put("name", institution.getName())
			.put("bic", institution.getBic())
			.put("transaction_total_days", institution.getTransactionTotalDays())
			.put("countries", countryJSONSetToArray(institution.getCountries()))
			.put("logo", institution.getLogo())
			;
	}
}
