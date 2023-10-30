package com.esferalia.aon.occam.api.json.nordigen;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.nordigen.NordigenJSONFunctionalInterfaces.INordigenAccountDetailsFromJSON;
import com.esferalia.aon.occam.api.json.nordigen.NordigenJSONFunctionalInterfaces.INordigenAccountDetailsToJSON;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenCashAccountType;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountDetails;

public enum NordigenAccountDetailsJSON {
	RESOURCE_ID(
			(detail, json) -> detail.setResourceId(json.optString("id", null)),
			(detail, json) -> json.put("id", detail.getResourceId())
	),
	IBAN(
			(detail, json) -> detail.setIban(json.optString("iban", null)),
			(detail, json) -> json.put("iban", detail.getIban())
	),
	CURRENCY(
			(detail, json) -> detail.setCurrency(json.optString("currency", null)),
			(detail, json) -> json.put("currency", detail.getCurrency())
	),
	OWNER_NAME(
			(detail, json) -> detail.setOwnerName(json.optString("ownerName", null)),
			(detail, json) -> json.put("ownerName", detail.getOwnerName())
	),
	NAME(
			(detail, json) -> detail.setName(json.optString("name", null)),
			(detail, json) -> json.put("name", detail.getName())
	),
	CASH_ACCOUNT_TYPE(
			(detail, json) -> detail.setCashAccountType(NordigenCashAccountType.safeValueOf(json.optString("cashAccountType"))),
			(detail, json) -> json.put("cashAccountType", detail.getCashAccountType() != null ? detail.getCashAccountType().toString() : null)
	),
	PRODUCT(
			(detail, json) -> detail.setProduct(json.optString("product", null)),
			(detail, json) -> json.put("product", detail.getProduct())
	),
	STATUS(
			(detail, json) -> detail.setStatus(json.optString("status", null)),
			(detail, json) -> json.put("status", detail.getStatus())
	),
	BIC(
			(detail, json) -> detail.setBic(json.optString("bic", null)),
			(detail, json) -> json.put("bic", detail.getBic())
	)
	;
	private INordigenAccountDetailsFromJSON fromJSON;
	private INordigenAccountDetailsToJSON toJSON;
	
	private NordigenAccountDetailsJSON(INordigenAccountDetailsFromJSON fromJSON, INordigenAccountDetailsToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}
	
	public static JSONObject toJSON(NordigenAccountDetails details) {
		if (details != null) {
			JSONObject json = new JSONObject();
			for (NordigenAccountDetailsJSON n : NordigenAccountDetailsJSON.values()) {
				n.toJSON.to(details, json);
			}
			return json;
		}
		return null;
	}
	
	public static NordigenAccountDetails fromString(String text) {
		JSONObject json = new JSONObject(text);
		return fromJSON(json);
	}

	public static NordigenAccountDetails fromJSON(JSONObject json) {
		if (json != null) {
			NordigenAccountDetails details = new NordigenAccountDetails();
			for (NordigenAccountDetailsJSON n : NordigenAccountDetailsJSON.values()) {
				n.fromJSON.from(details, json);
			}
			return details;
		}
		return null;
	}
	
	public static List<NordigenAccountDetails> fromJSONArray(JSONArray jsonArray) {
		List<NordigenAccountDetails> detailList = new LinkedList<>();
		if (jsonArray != null) {
			for (int i=0; i<jsonArray.length(); i++) {
				JSONObject jsonObj = jsonArray.optJSONObject(i);
				NordigenAccountDetails details = NordigenAccountDetailsJSON.fromJSON(jsonObj);
				detailList.add(details);
			}
		}
		return detailList;
	}
	
	public static JSONArray toJSONArray(List<NordigenAccountDetails> list) {
		JSONArray detailArr = new JSONArray();
		if (list != null) {
			for (NordigenAccountDetails details: list) {
				JSONObject detailJson = NordigenAccountDetailsJSON.toJSON(details);
				detailArr.put(detailJson);
			}
		}
		return detailArr;
	}
}
