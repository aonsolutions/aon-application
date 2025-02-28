package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.accounting.AccountingExpense;
import com.esferalia.aon.watson.server.AonDateUtils;

public class AccountingExpenseJSON {

	private AccountingExpenseJSON() {
			
	}
	
	public static List<AccountingExpense> fromJSON(JSONArray json) {
		LinkedList<AccountingExpense> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static AccountingExpense fromJSON(JSONObject json) {
		if(json == null) return new AccountingExpense();

		return new AccountingExpense()
				.setActivity(EnterpriseActivityJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.ACTIVITY)))
				.setAmount(JsonUtils.getDouble(json, IJsonNames.AMOUNT))
				.setComments(JsonUtils.optString(json, IJsonNames.COMMENTS))
				.setCreditor(CreditorJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.CREDITOR)))
				.setDate(JsonUtils.getDate(json, IJsonNames.DATE))
				.setDescription(JsonUtils.optString(json,IJsonNames.DESCRIPTION))
				.setPaymethod(PayMethodJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.PAY_METHOD)))
				.setReference(JsonUtils.getString(json, IJsonNames.REFERENCE));
	}
	
	public static JSONArray toJSON(List<AccountingExpense> projects) {
		return toJSON(projects.stream());
	}
	
	public static JSONArray toJSON(Stream<AccountingExpense> datas) {
		JSONArray array = new JSONArray();
		datas.forEach(d -> array.put(toJSON(d)));
		return array;
	}
	
	public static JSONObject toJSON(AccountingExpense a) {
		return new JSONObject()
				.put(IJsonNames.ACTIVITY, EnterpriseActivityJSON.toJSON(a.getActivity()))
				.put(IJsonNames.AMOUNT, a.getAmount())
				.put(IJsonNames.COMMENTS, a.getComments())
				.put(IJsonNames.CREDITOR, CreditorJSON.toJSON(a.getCreditor()))
				.put(IJsonNames.DATE, AonDateUtils.format(a.getDate(), AonDateUtils.SIMPLE_DATE_FORMAT))
				.put(IJsonNames.DESCRIPTION, a.getDescription())
				.put(IJsonNames.PAY_METHOD, PayMethodJSON.toJSON(a.getPaymethod()))
				.put(IJsonNames.REFERENCE, a.getReference());
	}
	
	
	
}
