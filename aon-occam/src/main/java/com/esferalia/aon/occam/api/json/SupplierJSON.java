package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;

public class SupplierJSON {
	
	private SupplierJSON() {
	
	}
	
	public static List<Supplier> fromJSON(JSONArray json) {
		LinkedList<Supplier> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	
	public static Supplier fromJSON(JSONObject json) {
		return new Supplier()
			.copy(RegistryJSON.fromJSON(json))
			.setAccount(JsonUtils.getInteger(json, IJsonNames.ACCOUNT))
			.setPurchaseValuated(JsonUtils.getboolean(json, IJsonNames.PURCHASE_VALUATED))
			.setScope(ScopeJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.SCOPE)))
			.setTariff(JsonUtils.getInteger(json, IJsonNames.TARIFF))
			.setTransaction(InvoiceTransactionType.safeValueOf(JsonUtils.getString(json, IJsonNames.TRANSACTION)))
			.setWithholding(JsonUtils.getboolean(json, IJsonNames.WITHHOLDING))
			.setWithholdingFarmer(JsonUtils.getboolean(json, IJsonNames.WITHHOLDING_FARMER))
			.setVatAccrualPayment(JsonUtils.getboolean(json, IJsonNames.VAT_ACCRUAL_PAYMENT))
			.setStatus(RegistryStatus.safeValueOf(JsonUtils.getString(json, IJsonNames.STATUS)));
	}
	

	public static JSONArray toJSON(List<Supplier> suppliers) {
		return toJSON(suppliers.stream());
	}
	
	public static JSONArray toJSON(Stream<Supplier> suppliers) {
		JSONArray array = new JSONArray();
		suppliers.forEach(supplier -> array.put(toJSON(supplier)));
		return array;
	}
	
	public static JSONObject toJSON(Supplier supplier) {
		JSONObject json = RegistryJSON.toJSON(supplier)
				.put(IJsonNames.WITHHOLDING, supplier.isWithholding())
				.put(IJsonNames.WITHHOLDING_FARMER, supplier.isWithholdingFarmer())
				.put(IJsonNames.VAT_ACCRUAL_PAYMENT, supplier.isVatAccrualPayment())
				.put(IJsonNames.PURCHASE_VALUATED, supplier.isPurchaseValuated())
				.put(IJsonNames.ACCOUNT, supplier.getAccount())
				.put(IJsonNames.SCOPE, ScopeJSON.toJSON(supplier.getScope()))
				.put(IJsonNames.TARIFF, supplier.getTariff())
				.put(IJsonNames.TRANSACTION, supplier.getTransaction().getTediName());
		
		if(supplier.getStatus() != null && supplier.getStatus().name() != null) {
			json.put(IJsonNames.STATUS, supplier.getStatus().name());
		}
		
		return json;
	}
}
