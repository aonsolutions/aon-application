package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.accounting.AccountingIncome;
import com.esferalia.aon.watson.server.AonDateUtils;

public class AccountingIncomeJSON {
	
	private AccountingIncomeJSON() {
		
	}
	
	public static List<AccountingIncome> fromJSON(JSONArray json) {
		LinkedList<AccountingIncome> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static AccountingIncome fromJSON(JSONObject json) {
		if(json == null) return new AccountingIncome();

		return new AccountingIncome()
				.setActivity(EnterpriseActivityJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.ACTIVITY)))
				.setAmount(JsonUtils.getDouble(json, IJsonNames.AMOUNT))
				.setComments(JsonUtils.optString(json, IJsonNames.COMMENTS))
				.setCustomer(CustomerJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.CUSTOMER)))
				.setDate(JsonUtils.getDate(json, IJsonNames.DATE))
				.setDescription(JsonUtils.optString(json,IJsonNames.DESCRIPTION))
				.setPaymethod(PayMethodJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.PAY_METHOD)))
				.setReference(JsonUtils.getString(json, IJsonNames.REFERENCE));
	}
	
	public static JSONArray toJSON(List<AccountingIncome> projects) {
		return toJSON(projects.stream());
	}
	
	public static JSONArray toJSON(Stream<AccountingIncome> datas) {
		JSONArray array = new JSONArray();
		datas.forEach(d -> array.put(toJSON(d)));
		return array;
	}
	
	public static JSONObject toJSON(AccountingIncome a) {
		return new JSONObject()
				.put(IJsonNames.ACTIVITY, EnterpriseActivityJSON.toJSON(a.getActivity()))
				.put(IJsonNames.AMOUNT, a.getAmount())
				.put(IJsonNames.COMMENTS, a.getComments())
				.put(IJsonNames.CUSTOMER, CustomerJSON.toJSON(a.getCustomer()))
				.put(IJsonNames.DATE, AonDateUtils.format(a.getDate(), AonDateUtils.SIMPLE_DATE_FORMAT))
				.put(IJsonNames.DESCRIPTION, a.getDescription())
				.put(IJsonNames.PAY_METHOD, PayMethodJSON.toJSON(a.getPaymethod()))
				.put(IJsonNames.REFERENCE, a.getReference());
	}
}
