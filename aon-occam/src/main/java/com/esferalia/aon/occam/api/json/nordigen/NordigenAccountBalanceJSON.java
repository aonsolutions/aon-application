package com.esferalia.aon.occam.api.json.nordigen;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.nordigen.NordigenJSONFunctionalInterfaces.INordigenAccountBalanceFromJSON;
import com.esferalia.aon.occam.api.json.nordigen.NordigenJSONFunctionalInterfaces.INordigenAccountBalanceToJSON;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBalanceType;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountBalance;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum NordigenAccountBalanceJSON {
	BALANCE_AMOUNT(
			(balance, json) -> balance.setBalanceAmount(NordigenJSONUtils.accountAmountFromJSON(json.optJSONObject("balanceAmount"))),
			(balance, json) -> json.put("balanceAmount", NordigenJSONUtils.accountAmountToJSON(balance.getBalanceAmount()))
	),
	BALANCE_TYPE(			
			(balance, json) -> balance.setBalanceType(NordigenBalanceType.getByValue(json.optString("balanceType"))),
			(balance, json) -> json.put("balanceType", balance.getBalanceType() != null ? balance.getBalanceType().getValue() : null)
	),
	REFERENCE_DATE(
			(balance, json) -> balance.setReferenceDate(AonDateUtils.parse(json.optString("referenceDate", null), AonDateUtils.SIMPLE_DATE_FORMAT4)),
			(balance, json) -> json.put("referenceDate", AonDateUtils.format(balance.getReferenceDate(), AonDateUtils.SIMPLE_DATE_FORMAT4))
	),
	ORIGINAL_JSON(
			(balance, json) -> balance.setOriginalJson(AonStringUtils.isNotBlank(json.optString("originalJson")) ? json.optString("originalJson") : json.toString(4)),
			(balance, json) -> json.put("originalJson", balance.getOriginalJson())
	)
	;
	private INordigenAccountBalanceFromJSON fromJSON;
	private INordigenAccountBalanceToJSON toJSON;
	
	private NordigenAccountBalanceJSON(INordigenAccountBalanceFromJSON fromJSON, INordigenAccountBalanceToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}
	
	public static JSONObject toJSON(NordigenAccountBalance balance) {
		if (balance != null) {
			JSONObject json = new JSONObject();
			for (NordigenAccountBalanceJSON n : NordigenAccountBalanceJSON.values()) {
				n.toJSON.to(balance, json);
			}
			return json;
		}
		return null;
	}
	
	public static NordigenAccountBalance fromString(String text) {
		JSONObject json = new JSONObject(text);
		return fromJSON(json);
	}

	public static NordigenAccountBalance fromJSON(JSONObject json) {
		if (json != null) {
			NordigenAccountBalance institution = new NordigenAccountBalance();
			for (NordigenAccountBalanceJSON n : NordigenAccountBalanceJSON.values()) {
				n.fromJSON.from(institution, json);
			}
			return institution;
		}
		return null;
	}
	
	public static List<NordigenAccountBalance> fromJSONArray(JSONArray jsonArray) {
		List<NordigenAccountBalance> instList = new LinkedList<>();
		if (jsonArray != null) {
			for (int i=0; i<jsonArray.length(); i++) {
				JSONObject jsonObj = jsonArray.optJSONObject(i);
				NordigenAccountBalance institution = NordigenAccountBalanceJSON.fromJSON(jsonObj);
				instList.add(institution);
			}
		}
		return instList;
	}
	
	public static JSONArray toJSONArray(List<NordigenAccountBalance> list) {
		JSONArray instArr = new JSONArray();
		if (list != null) {
			for (NordigenAccountBalance inst: list) {
				JSONObject instJson = NordigenAccountBalanceJSON.toJSON(inst);
				instArr.put(instJson);
			}
		}
		return instArr;
	}
}
