package com.esferalia.aon.occam.api.json.raw;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.RegistryJSON;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class FinanceJSON {

	private FinanceJSON() {
	
	}
	
	public static List<Finance> fromJSON(JSONArray json) {
		LinkedList<Finance> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Finance fromJSON(JSONObject json) {
		Date date = JsonUtils.getDate(json, IJsonNames.DUE_DATE);
		Integer paymethod = null;
		if(AonNumberUtils.isNumber(json.optString(IJsonNames.PAYMETHOD)))
			paymethod = AonNumberUtils.toInteger(json.optString(IJsonNames.PAYMETHOD));
		return new Finance()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
				.setPayMethod(paymethod)
				.setPayMethodName(JsonUtils.getString(json, IJsonNames.PAYMETHOD_NAME))
				.setPayMethodType(PayMethodType.safeValueOf(JsonUtils.getString(json, IJsonNames.PAYMETHOD_TYPE)))
				.setBankAccount(new BankAccount(json.optString(IJsonNames.BANK_ACCOUNT)))
				.setAmount(JsonUtils.getdouble(json, IJsonNames.AMOUNT))
				.setDueDate(date);
	}
	
	public static JSONArray toJSON(List<Finance> finances) {
		JSONArray array = new JSONArray();
		if(finances != null) 
			finances.stream().forEach(finance -> array.put(toJSON(finance)));
		return array;
	}
	
	public static JSONObject toJSON(Finance finance) {
		return new JSONObject()
			.put(IJsonNames.ID, finance.getId())
			.put(IJsonNames.DOMAIN, finance.getDomain())
			.put(IJsonNames.PAYMENT, finance.isPayment())
			.put(IJsonNames.REGISTRY, RegistryJSON.toJSON(finance.getRegistry()))
			.put(IJsonNames.REGISTRY_DOCUMENT, finance.getRegistryDocument())
			.put(IJsonNames.REGISTRY_DOCUMENT_TYPE, finance.getRegistryDocumentType())
			.put(IJsonNames.REGISTRY_DOCUMENT_COUNTRY, finance.getRegistryDocumentCountry())
			.put(IJsonNames.REGISTRY_NAME, finance.getRegistryName())
			.put(IJsonNames.AMOUNT, finance.getAmount())
			.put(IJsonNames.EXPENSES, finance.getExpenses())
			.put(IJsonNames.CONCEPT, finance.getConcept())
			.put(IJsonNames.DUE_DATE, AonDateUtils.format(finance.getDueDate(), AonDateUtils.DATE_TIME_FORMAT_AUX))
			.put(IJsonNames.PAYMETHOD, finance.getPayMethod())
			.put(IJsonNames.PAYMETHOD_NAME, finance.getPayMethodName() != null ? finance.getPayMethodName() : null)
			.put(IJsonNames.PAYMETHOD_TYPE, finance.getPayMethodType() != null ? finance.getPayMethodType().name() : null)
			.put(IJsonNames.BANK_ACCOUNT, finance.getBankAccount() != null ? finance.getBankAccount().getIban() : null)
			.put(IJsonNames.BANK_ALIAS, finance.getBankAlias())
			.put(IJsonNames.BIC, finance.getBic())
			
			.put(IJsonNames.CREATION_USER, finance.getCreationUser())
			.put(IJsonNames.CREATION_DATE, AonDateUtils.simpleFormat(finance.getCreationDate()))
			.put(IJsonNames.MODIFICATION_USER, finance.getModificationUser())
			.put(IJsonNames.MODIFICATION_DATE, AonDateUtils.simpleFormat(finance.getModificationDate()))
			;
			
	}
}
