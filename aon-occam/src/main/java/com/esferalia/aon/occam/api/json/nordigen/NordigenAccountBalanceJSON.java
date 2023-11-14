package com.esferalia.aon.occam.api.json.nordigen;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountBalance;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBalanceType;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenException;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class NordigenAccountBalanceJSON {
	
	
	private NordigenAccountBalanceJSON() {
	}
	
	public static LinkedList<NordigenAccountBalance> fromBalances(String balancesString) {
		if (AonStringUtils.isNotBlank(balancesString)) {
			JSONObject balances = new JSONObject(balancesString);
			JSONArray balancesJson = balances.optJSONArray("balances");
			return NordigenAccountBalanceJSON.from(balancesJson);
		}
		return new LinkedList<>();
	}
	
	public static NordigenAccountBalance from(String text) throws NordigenException {
		try {
			return from(new JSONObject(text));
		} catch (JSONException e) {
			throw new NordigenException(e.getMessage()); 
		} 
	}
	
	public static LinkedList<NordigenAccountBalance> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return NordigenJSONUtils.stream(array)
			.map(NordigenAccountBalanceJSON::from)
			.collect(Collectors.toCollection(LinkedList::new));		
	}
	
	public static NordigenAccountBalance from(JSONObject json) {
		if (json == null) return null; 
		return new NordigenAccountBalance()
			.setBalanceAmount(NordigenJSONUtils.accountAmountFromJSON(json.optJSONObject("balanceAmount")))
			.setBalanceType(NordigenBalanceType.getByValue(json.optString("balanceType")))
			.setReferenceDate(NordigenJSONUtils.getDate(json,"referenceDate"))
			.setOriginalJson(AonStringUtils.isNotBlank(json.optString("originalJson")) ? json.optString("originalJson") : json.toString(4));
	}
	
	public static JSONArray to(List<NordigenAccountBalance> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}

	public static JSONArray to(Stream<NordigenAccountBalance> stream) {
		return stream
			.map(NordigenAccountBalanceJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}

	public static JSONObject to(NordigenAccountBalance balance) {
		if (balance == null) return null;
		return new JSONObject()
			.put("balanceAmount", NordigenJSONUtils.accountAmountToJSON(balance.getBalanceAmount()))
			.put("balanceType", balance.getBalanceType() != null ? balance.getBalanceType().getValue() : null)
			.put("referenceDate", NordigenJSONUtils.formatDate(balance.getReferenceDate()))
			.put("originalJson", balance.getOriginalJson());
	}
}
