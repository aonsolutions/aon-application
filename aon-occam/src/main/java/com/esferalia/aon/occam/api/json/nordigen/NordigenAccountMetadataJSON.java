package com.esferalia.aon.occam.api.json.nordigen;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountMetadata;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountStatus;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class NordigenAccountMetadataJSON {
	private NordigenAccountMetadataJSON() {
	}
	
	public static NordigenAccountMetadata from(String text) throws NordigenException {
		try {
			return from(new JSONObject(text));
		} catch (JSONException e) {
			throw new NordigenException(e.getMessage()); 
		} 
	}
	
	public static List<NordigenAccountMetadata> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return NordigenJSONUtils.stream(array)
			.map(NordigenAccountMetadataJSON::from)
			.toList();		
	}
	
	public static NordigenAccountMetadata from(JSONObject json) {
		if (json == null) return null; 
		return new NordigenAccountMetadata()
			.setId(json.optString("id", null))
			.setCreated(AonDateUtils.parse(json.optString("created", null), AonDateUtils.DATE_TIME_FORMAT_AUX))
			.setLastAccessed(AonDateUtils.parse(json.optString("last_accessed", null), AonDateUtils.DATE_TIME_FORMAT_AUX))
			.setIban(json.optString("iban", null))
			.setInstitutionId(json.optString("institution_id", null))
			.setStatus(NordigenAccountStatus.safeValueOf(json.optString("status", null)))
			.setOwnerName(json.optString("owner_name", null));
	}
	
	public static JSONArray to(List<NordigenAccountMetadata> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}

	public static JSONArray to(Stream<NordigenAccountMetadata> stream) {
		return stream
			.map(NordigenAccountMetadataJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}

	public static JSONObject to(NordigenAccountMetadata accountMetadata) {
		if (accountMetadata == null) return null;
		return new JSONObject()
			.put("id", accountMetadata.getId())
			.put("created", AonDateUtils.format(accountMetadata.getCreated(), AonDateUtils.DATE_TIME_FORMAT_AUX))
			.put("last_accessed", AonDateUtils.format(accountMetadata.getLastAccessed(), AonDateUtils.DATE_TIME_FORMAT_AUX))
			.put("iban", accountMetadata.getIban())
			.put("institution_id", accountMetadata.getInstitutionId())
			.put("status", accountMetadata.getStatus() != null ? accountMetadata.getStatus().getDescription() : null)
			.put("owner_name", accountMetadata.getOwnerName());
	}
			
}
