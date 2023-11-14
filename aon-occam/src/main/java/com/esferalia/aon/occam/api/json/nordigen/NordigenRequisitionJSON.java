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
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisitionStatus;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class NordigenRequisitionJSON {
	private NordigenRequisitionJSON() {
	}
	
	public static LinkedList<NordigenRequisition> fromResults(String text) throws NordigenException {
		try {
			JSONObject requisitionsJson = new JSONObject(text);
			return from(requisitionsJson.optJSONArray("results"));
		} catch (JSONException e) {
			throw new NordigenException(e.getMessage()); 
		} 
	}

	public static NordigenRequisition from(String text) throws NordigenException {
		try {
			return from(new JSONObject(text));
		} catch (JSONException e) {
			throw new NordigenException(e.getMessage()); 
		} 
	}
	
	public static LinkedList<NordigenRequisition> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return NordigenJSONUtils.stream(array)
			.map(NordigenRequisitionJSON::from)
			.collect(Collectors.toCollection(LinkedList::new));		
	}
	
	public static NordigenRequisition from(JSONObject json) {
		if (json == null) return null; 
		return new NordigenRequisition()
			.setId(json.optString("id", null))
			.setCreated(NordigenJSONUtils.getDateTimeAux(json,"created"))
			.setRedirect(json.optString("redirect", null))
			.setStatus(NordigenRequisitionStatus.safeValueOf(json.optString("status", null)))
			.setInstitutionId(json.optString("institution_id", null))
			.setAgreement(json.optString("agreement", null))
			.setReference(json.optString("reference", null))
			.setAccounts(NordigenJSONUtils.jsonStringArrayToList(json.optJSONArray("accounts")))
			.setUserLanguage(json.optString("user_language", null))
			.setLink(json.optString("link", null))
			.setSsn(json.optString("ssn", null))
			.setAccountSelection(!json.isNull("account_selection") ? json.optBoolean("account_selection") : null)
			.setRedirectImmediate(!json.isNull("redirect_immediate") ? json.optBoolean("redirect_immediate") : null)
			;
	}
	
	public static JSONArray to(List<NordigenRequisition> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}

	public static JSONArray to(Stream<NordigenRequisition> stream) {
		return stream
			.map(NordigenRequisitionJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}

	public static JSONObject to(NordigenRequisition requisition) {
		if (requisition == null) return null;
		return new JSONObject()
			.put("id", requisition.getId())
			.put("created", NordigenJSONUtils.formatDateTimeAux(requisition.getCreated()))
			.put("redirect", requisition.getRedirect())
			.put("status", requisition.getStatus() != null ? requisition.getStatus().getShortName() : null)
			.put("institution_id", requisition.getInstitutionId())
			.put("agreement", requisition.getAgreement())
			.put("reference", requisition.getReference())
			.put("accounts", NordigenJSONUtils.jsonStringArrayFromList(requisition.getAccounts()))
			.put("user_language", requisition.getUserLanguage())
			.put("link", requisition.getLink())
			.put("ssn", requisition.getSsn())
			.put("account_selection", requisition.getAccountSelection())
			.put("redirect_immediate", requisition.getRedirectImmediate());
	}
}
