package com.esferalia.aon.occam.api.json.nordigen;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.nordigen.NordigenJSONFunctionalInterfaces.INordigenRequisitionFromJSON;
import com.esferalia.aon.occam.api.json.nordigen.NordigenJSONFunctionalInterfaces.INordigenRequisitionToJSON;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisitionStatus;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;
import com.esferalia.aon.watson.server.AonDateUtils;

public enum NordigenRequisitionJSON {
	ID(
			(institution, json) -> institution.setId(json.optString("id", null)),
			(institution, json) -> json.put("id", institution.getId())
	),
	CREATED(
			(requisition, json) -> requisition.setCreated(AonDateUtils.parse(json.optString("created", null), AonDateUtils.DATE_TIME_FORMAT_AUX)),
			(requisition, json) -> json.put("created", AonDateUtils.format(requisition.getCreated(), AonDateUtils.DATE_TIME_FORMAT_AUX))
	),
	REDIRECT(
			(requisition, json) -> requisition.setRedirect(json.optString("redirect", null)),
			(requisition, json) -> json.put("redirect", requisition.getRedirect())
	),
	STATUS(
			(requisition, json) -> requisition.setStatus(NordigenRequisitionStatus.safeValueOf(json.optString("status", null))),
			(requisition, json) -> json.put("status", requisition.getStatus() != null ? requisition.getStatus().getShortName() : null)
	),
	INSTITUTION_ID(
			(requisition, json) -> requisition.setInstitutionId(json.optString("institution_id", null)),
			(requisition, json) -> json.put("institution_id", requisition.getInstitutionId())
	),
	AGREEMENT(
			(requisition, json) -> requisition.setAgreement(json.optString("agreement", null)),
			(requisition, json) -> json.put("agreement", requisition.getAgreement())
	),
	REFERENCE(
			(requisition, json) -> requisition.setReference(json.optString("reference", null)),
			(requisition, json) -> json.put("reference", requisition.getReference())
	),
	ACCOUNTS(
			(requisition, json) -> requisition.setAccounts(NordigenJSONUtils.jsonStringArrayToList(json.optJSONArray("accounts"))),
			(requisition, json) -> json.put("accounts", NordigenJSONUtils.jsonStringArrayFromList(requisition.getAccounts()))
	),
	USER_LANGUAGE(
			(requisition, json) -> requisition.setUserLanguage(json.optString("user_language", null)),
			(requisition, json) -> json.put("user_language", requisition.getUserLanguage())
	),
	LINK(
			(requisition, json) -> requisition.setLink(json.optString("link", null)),
			(requisition, json) -> json.put("link", requisition.getLink())
	),
	SSN(
			(requisition, json) -> requisition.setSsn(json.optString("ssn", null)),
			(requisition, json) -> json.put("ssn", requisition.getSsn())
	),
	ACCOUNT_SELECTION(
			(requisition, json) -> requisition.setAccountSelection(!json.isNull("account_selection") ? json.optBoolean("account_selection") : null),
			(requisition, json) -> json.put("account_selection", requisition.getAccountSelection())
	),
	REDIRECT_IMMEDIATE(
			(requisition, json) -> requisition.setRedirectImmediate(!json.isNull("redirect_immediate") ? json.optBoolean("redirect_immediate") : null),
			(requisition, json) -> json.put("redirect_immediate", requisition.getRedirectImmediate())
	)
	;
	private INordigenRequisitionFromJSON fromJSON;
	private INordigenRequisitionToJSON toJSON;
	
	private NordigenRequisitionJSON(INordigenRequisitionFromJSON fromJSON, INordigenRequisitionToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}
	
	public static JSONObject toJSON(NordigenRequisition requisition) {
		if (requisition != null) {
			JSONObject json = new JSONObject();
			for (NordigenRequisitionJSON n : NordigenRequisitionJSON.values()) {
				n.toJSON.to(requisition, json);
			}
			return json;
		}
		return null;
	}
	
	public static NordigenRequisition fromString(String text) {
		JSONObject json = new JSONObject(text);
		return fromJSON(json);
	}

	public static NordigenRequisition fromJSON(JSONObject json) {
		if (json != null) {
			NordigenRequisition requisition = new NordigenRequisition();
			for (NordigenRequisitionJSON n : NordigenRequisitionJSON.values()) {
				n.fromJSON.from(requisition, json);
			}
			return requisition;
		}
		return null;
	}
	
	public static List<NordigenRequisition> fromJSONArray(JSONArray jsonArray) {
		List<NordigenRequisition> reqList = new LinkedList<>();
		if (jsonArray != null) {
			for (int i=0; i<jsonArray.length(); i++) {
				JSONObject jsonObj = jsonArray.optJSONObject(i);
				NordigenRequisition requisition = NordigenRequisitionJSON.fromJSON(jsonObj);
				reqList.add(requisition);
			}
		}
		return reqList;
	}
	
	public static JSONArray toJSONArray(List<NordigenRequisition> list) {
		JSONArray reqArr = new JSONArray();
		if (list != null) {
			for (NordigenRequisition req: list) {
				JSONObject reqJson = NordigenRequisitionJSON.toJSON(req);
				reqArr.put(reqJson);
			}
		}
		return reqArr;
	}
}
