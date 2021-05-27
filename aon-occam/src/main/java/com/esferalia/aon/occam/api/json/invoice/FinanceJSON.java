package com.esferalia.aon.occam.api.json.invoice;

import java.util.Date;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.type.PayMethodType;

import es.translogia.tedi.json.TediJSONUtils;

public class FinanceJSON {

	public static LinkedList<Finance> fromJSON(JSONArray json) {
		LinkedList<Finance> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Finance fromJSON(JSONObject json) {
		Date date = TediJSONUtils.parseDate(json.optString(IJsonNames.DUE_DATE));
		return new Finance()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
				.setPayMethodType(PayMethodType.safeValueOf(json.optString(IJsonNames.PAYMETHOD)))
				.setBankAccount(new BankAccount(json.optString(IJsonNames.BANK_ACCOUNT)))
				.setAmount(JsonUtils.getdouble(json, IJsonNames.AMOUNT))
				.setDueDate(date);
	}
	
	public static JSONArray toJSON(LinkedList<Finance> finances) {
		JSONArray array = new JSONArray();
		if(finances != null) 
			finances.stream().forEach(finance -> array.put(toJSON(finance)));
		return array;
	}
	
	public static JSONObject toJSON(Finance finance) {
		String date = TediJSONUtils.formatDate(finance.getDueDate());

		return new JSONObject()
			.put(IJsonNames.ID, finance.getId())
			.put(IJsonNames.DOMAIN, finance.getDomain())
			.put(IJsonNames.DUE_DATE, date) //JsonUtils.getDateJSON(finance.getDueDate()))
			.put(IJsonNames.PAYMETHOD, finance.getPayMethodType() != null ? finance.getPayMethodType().getTediName() : PayMethodType.OTHER)
			.put(IJsonNames.BANK_ACCOUNT, finance.getBankAccount() != null
				? finance.getBankAccount().getIban() : null)
			.put(IJsonNames.AMOUNT, finance.getAmount());
	}
}
