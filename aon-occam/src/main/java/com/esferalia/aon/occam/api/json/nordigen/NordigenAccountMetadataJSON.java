package com.esferalia.aon.occam.api.json.nordigen;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.nordigen.NordigenJSONFunctionalInterfaces.INordigenAccountMetadataFromJSON;
import com.esferalia.aon.occam.api.json.nordigen.NordigenJSONFunctionalInterfaces.INordigenAccountMetadataToJSON;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountStatus;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountMetadata;
import com.esferalia.aon.watson.server.AonDateUtils;

public enum NordigenAccountMetadataJSON {
	ID(
			(account, json) -> account.setId(json.optString("id", null)),
			(account, json) -> json.put("id", account.getId())
	),
	CREATED(
			(agreement, json) -> agreement.setCreated(AonDateUtils.parse(json.optString("created", null), AonDateUtils.DATE_TIME_FORMAT_AUX)),
			(agreement, json) -> json.put("created", AonDateUtils.format(agreement.getCreated(), AonDateUtils.DATE_TIME_FORMAT_AUX))
	),
	LAST_ACCESSED(
			(agreement, json) -> agreement.setLastAccessed(AonDateUtils.parse(json.optString("last_accessed", null), AonDateUtils.DATE_TIME_FORMAT_AUX)),
			(agreement, json) -> json.put("last_accessed", AonDateUtils.format(agreement.getLastAccessed(), AonDateUtils.DATE_TIME_FORMAT_AUX))
	),
	IBAN(
			(account, json) -> account.setIban(json.optString("iban", null)),
			(account, json) -> json.put("iban", account.getIban())
	),
	INSTITUTION_ID(
			(account, json) -> account.setInstitutionId(json.optString("institution_id", null)),
			(account, json) -> json.put("institution_id", account.getInstitutionId())
	),
	STATUS(
			(account, json) -> account.setStatus(NordigenAccountStatus.safeValueOf(json.optString("status", null))),
			(account, json) -> json.put("status", account.getStatus() != null ? account.getStatus().getDescription() : null)
	),
	OWNER_NAME(
			(account, json) -> account.setOwnerName(json.optString("owner_name", null)),
			(account, json) -> json.put("owner_name", account.getOwnerName())
	),
	;
	private INordigenAccountMetadataFromJSON fromJSON;
	private INordigenAccountMetadataToJSON toJSON;
	
	private NordigenAccountMetadataJSON(INordigenAccountMetadataFromJSON fromJSON, INordigenAccountMetadataToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}
	
	public static JSONObject toJSON(NordigenAccountMetadata account) {
		if (account != null) {
			JSONObject json = new JSONObject();
			for (NordigenAccountMetadataJSON n : NordigenAccountMetadataJSON.values()) {
				n.toJSON.to(account, json);
			}
			return json;
		}
		return null;
	}
	
	public static NordigenAccountMetadata fromString(String text) {
		JSONObject json = new JSONObject(text);
		return fromJSON(json);
	}

	public static NordigenAccountMetadata fromJSON(JSONObject json) {
		if (json != null) {
			NordigenAccountMetadata account = new NordigenAccountMetadata();
			for (NordigenAccountMetadataJSON n : NordigenAccountMetadataJSON.values()) {
				n.fromJSON.from(account, json);
			}
			return account;
		}
		return null;
	}
	
	public static List<NordigenAccountMetadata> fromJSONArray(JSONArray jsonArray) {
		List<NordigenAccountMetadata> accList = new LinkedList<>();
		if (jsonArray != null) {
			for (int i=0; i<jsonArray.length(); i++) {
				JSONObject jsonObj = jsonArray.optJSONObject(i);
				NordigenAccountMetadata account = NordigenAccountMetadataJSON.fromJSON(jsonObj);
				accList.add(account);
			}
		}
		return accList;
	}
	
	public static JSONArray toJSONArray(List<NordigenAccountMetadata> list) {
		JSONArray accArr = new JSONArray();
		if (list != null) {
			for (NordigenAccountMetadata acc: list) {
				JSONObject accJson = NordigenAccountMetadataJSON.toJSON(acc);
				accArr.put(accJson);
			}
		}
		return accArr;
	}
}
