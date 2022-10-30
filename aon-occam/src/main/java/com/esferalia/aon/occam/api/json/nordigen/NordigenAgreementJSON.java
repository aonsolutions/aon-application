package com.esferalia.aon.occam.api.json.nordigen;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.nordigen.NordigenJSONFunctionalInterfaces.INordigenAgreementFromJSON;
import com.esferalia.aon.occam.api.json.nordigen.NordigenJSONFunctionalInterfaces.INordigenAgreementToJSON;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAgreement;
import com.esferalia.aon.watson.server.AonDateUtils;

public enum NordigenAgreementJSON {
	ID(
			(agreement, json) -> agreement.setId(json.optString("id", null)),
			(agreement, json) -> json.put("id", agreement.getId())
	),
	CREATED(
			(agreement, json) -> agreement.setCreated(AonDateUtils.parse(json.optString("created", null), AonDateUtils.DATE_TIME_FORMAT_AUX)),
			(agreement, json) -> json.put("created", AonDateUtils.format(agreement.getCreated(), AonDateUtils.DATE_TIME_FORMAT_AUX))
	),
	MAX_HISTORICAL_DAYS(
			(agreement, json) -> agreement.setMaxHistoricalDays(json.optInt("max_historical_days")),
			(agreement, json) -> json.put("max_historical_days", agreement.getMaxHistoricalDays())
	),
	ACCESS_VALID_FOR_DAYS(
			(agreement, json) -> agreement.setAccessValidForDays(json.optInt("access_valid_for_days")),
			(agreement, json) -> json.put("access_valid_for_days", agreement.getAccessValidForDays())
	),
	ACCESS_SCOPE(
			(agreement, json) -> agreement.setAccessScope(NordigenJSONUtils.accessScopesFromJSON(json.optJSONArray("access_scope"))),
			(agreement, json) -> json.put("access_scope", NordigenJSONUtils.accessScopesToJSON(agreement.getAccessScope()))
	),
	ACCEPTED(
			(agreement, json) -> agreement.setAccepted(AonDateUtils.parse(json.optString("accepted", null), AonDateUtils.DATE_TIME_FORMAT_AUX)),
			(agreement, json) -> json.put("accepted", AonDateUtils.format(agreement.getAccepted(), AonDateUtils.DATE_TIME_FORMAT_AUX))
	),
	INSTITUTION_ID(
			(agreement, json) -> agreement.setInstitutionId(json.optString("institution_id")),
			(agreement, json) -> json.put("institution_id", agreement.getInstitutionId())
	)
	;
	private INordigenAgreementFromJSON fromJSON;
	private INordigenAgreementToJSON toJSON;
	
	private NordigenAgreementJSON(INordigenAgreementFromJSON fromJSON, INordigenAgreementToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}
	
	public static JSONObject toJSON(NordigenAgreement agreement) {
		if (agreement != null) {
			JSONObject json = new JSONObject();
			for (NordigenAgreementJSON n : NordigenAgreementJSON.values()) {
				n.toJSON.to(agreement, json);
			}
			return json;
		}
		return null;
	}
	
	public static NordigenAgreement fromString(String text) {
		JSONObject json = new JSONObject(text);
		return fromJSON(json);
	}

	public static NordigenAgreement fromJSON(JSONObject json) {
		if (json != null) {
			NordigenAgreement agreement = new NordigenAgreement();
			for (NordigenAgreementJSON n : NordigenAgreementJSON.values()) {
				n.fromJSON.from(agreement, json);
			}
			return agreement;
		}
		return null;
	}
	
	public static List<NordigenAgreement> fromJSONArray(JSONArray jsonArray) {
		List<NordigenAgreement> agrList = new LinkedList<>();
		if (jsonArray != null) {
			for (int i=0; i<jsonArray.length(); i++) {
				JSONObject jsonObj = jsonArray.optJSONObject(i);
				NordigenAgreement agreement = NordigenAgreementJSON.fromJSON(jsonObj);
				agrList.add(agreement);
			}
		}
		return agrList;
	}
	
	public static JSONArray toJSONArray(List<NordigenAgreement> list) {
		JSONArray agrArr = new JSONArray();
		if (list != null) {
			for (NordigenAgreement agr: list) {
				JSONObject agrJson = NordigenAgreementJSON.toJSON(agr);
				agrArr.put(agrJson);
			}
		}
		return agrArr;
	}
}
