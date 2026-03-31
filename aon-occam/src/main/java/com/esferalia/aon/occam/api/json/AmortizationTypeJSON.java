package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.accounting.AmortizationType;

public class AmortizationTypeJSON {

	private AmortizationTypeJSON() {

	}

	public static List<AmortizationType> fromJSON(JSONArray json) {
		LinkedList<AmortizationType> list = new LinkedList<>();
		for (Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
		return list;
	}

	public static AmortizationType fromJSON(JSONObject json) {
		if (json == null) return new AmortizationType();
		return new AmortizationType()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setDomain(new Domain().setId(JsonUtils.getInteger(json, IJsonNames.DOMAIN)))
				.setDescription(JsonUtils.optString(json, IJsonNames.DESCRIPTION))
				.setFixedAssetAccount(JsonUtils.optString(json, "fixedAssetAccount"))
				.setAccumulatedAccount(JsonUtils.optString(json, "accumulatedAccount"))
				.setAllocationAccount(JsonUtils.optString(json, "allocationAccount"))
				.setPercentage(json.isNull(IJsonNames.PERCENTAGE) ? null : json.getDouble(IJsonNames.PERCENTAGE))
				.setModify(json.optBoolean("modify"));
	}

	public static JSONArray toJSON(List<AmortizationType> list) {
		return toJSON(list.stream());
	}

	public static JSONArray toJSON(Stream<AmortizationType> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(d -> array.put(toJSON(d)));
		return array;
	}

	public static JSONObject toJSON(AmortizationType d) {
		return new JSONObject()
				.put(IJsonNames.ID, d.getId())
				.put(IJsonNames.DOMAIN, d.getDomain() != null ? d.getDomain().getId() : JSONObject.NULL)
				.put(IJsonNames.DESCRIPTION, d.getDescription())
				.put("fixedAssetAccount", d.getFixedAssetAccount())
				.put("accumulatedAccount", d.getAccumulatedAccount())
				.put("allocationAccount", d.getAllocationAccount())
				.put(IJsonNames.PERCENTAGE, d.getPercentage())
				.put("modify", d.isModify());
	}
}
