package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;

public class CreditorJSON {
	
	private CreditorJSON() {
	
	}
	
	public static List<Creditor> fromJSON(JSONArray json) {
		LinkedList<Creditor> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	
	public static Creditor fromJSON(JSONObject json) {
		return new Creditor()
			.copy(RegistryJSON.fromJSON(json))
			.setAccount(JsonUtils.getInteger(json, IJsonNames.ACCOUNT))
			.setScope(ScopeJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.SCOPE)))
			.setTransaction(InvoiceTransactionType.safeValueOf(JsonUtils.getString(json, IJsonNames.TRANSACTION)))
			.setWithholding(JsonUtils.getboolean(json, IJsonNames.WITHHOLDING))
			.setStatus(RegistryStatus.safeValueOf(JsonUtils.getString(json, IJsonNames.STATUS)))
			.setVatAccrualPayment(JsonUtils.getboolean(json, IJsonNames.VAT_ACCRUAL_PAYMENT));
	}
	

	public static JSONArray toJSON(List<Creditor> creditors) {
		return toJSON(creditors.stream());
	}
	
	public static JSONArray toJSON(Stream<Creditor> creditors) {
		JSONArray array = new JSONArray();
		creditors.forEach(creditor -> array.put(toJSON(creditor)));
		return array;
	}
	
	public static JSONObject toJSON(Creditor creditor) {
		return RegistryJSON.toJSON(creditor)
			.put(IJsonNames.WITHHOLDING, creditor.isWithholding())
			.put(IJsonNames.VAT_ACCRUAL_PAYMENT, creditor.isVatAccrualPayment())
			.put(IJsonNames.ACCOUNT, creditor.getAccount())
			.put(IJsonNames.SCOPE, ScopeJSON.toJSON(creditor.getScope()))
			.put(IJsonNames.TRANSACTION, creditor.getTransaction().getTediName())
			.put(IJsonNames.STATUS, creditor.getStatus().name());
	}
}
