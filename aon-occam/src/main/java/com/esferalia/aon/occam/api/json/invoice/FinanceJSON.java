package com.esferalia.aon.occam.api.json.invoice;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.type.PayMethodType;

public class FinanceJSON {

	public static LinkedList<Finance> fromJSON(JSONArray json) {
		LinkedList<Finance> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Finance fromJSON(JSONObject json) {
		return new Finance()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
				.setPayMethodType(PayMethodType.safeValueOf(json.optString(IJsonNames.PAYMETHOD)))
				.setBankAccount(new BankAccount(json.optString(IJsonNames.BANK_ACCOUNT)))
				.setAmount(JsonUtils.getdouble(json, IJsonNames.AMOUNT));
	}
	
	public static JSONArray toJSON(LinkedList<Finance> finances) {
		JSONArray array = new JSONArray();
		if(finances != null) 
			finances.stream().forEach(finance -> array.put(toJSON(finance)));
		return array;
	}
	
	public static JSONObject toJSON(Finance finance) {
		return new JSONObject()
			.put(IJsonNames.ID, finance.getId())
			.put(IJsonNames.DOMAIN, finance.getDomain())
			.put(IJsonNames.DUE_DATE, JsonUtils.getDateJSON(finance.getDueDate()))
			.put(IJsonNames.PAYMETHOD, finance.getPayMethodType().name())
			.put(IJsonNames.BANK_ACCOUNT, finance.getBankAccount().getIban())
			.put(IJsonNames.AMOUNT, finance.getAmount());
	}
}
