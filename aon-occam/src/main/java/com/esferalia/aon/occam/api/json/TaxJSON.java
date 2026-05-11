package com.esferalia.aon.occam.api.json;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class TaxJSON {
	
	private TaxJSON() {
	    throw new IllegalStateException("Utility class");
	}
	
	public static List<Tax> fromJSON(JSONArray json) {
		LinkedList<Tax> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Tax fromJSON(JSONObject json) {
		if(json == null || json.isEmpty()) return null;
		return new Tax()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setName(JsonUtils.getString(json, IJsonNames.NAME))
			.setType(TaxType.safeValueOf(JsonUtils.getString(json, IJsonNames.TYPE)))
			.setPercentage(JsonUtils.getdouble(json, IJsonNames.PERCENTAGE))
			.setSurcharge(JsonUtils.getdouble(json, IJsonNames.SURCHARGE))
			;

	}
	
	public static JSONArray toJSON(List<Tax> list) {
		return toJSON(AonCollectionUtils.stream(list));
	}
	
	public static JSONArray toJSON(Stream<Tax> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(tax -> array.put(toJSON(tax)));
		return array;
	}
	
	public static JSONObject toJSON(Tax tax) {
		if (tax == null) return null;
		return new JSONObject()
			.put(IJsonNames.ID, tax.getId())
			.put(IJsonNames.DOMAIN, tax.getDomain())
			.put(IJsonNames.NAME, tax.getName())
			.put(IJsonNames.START_DATE2, JsonUtils.getDateJSON(tax.getStartDate()))
			.put(IJsonNames.TYPE, TaxType.name(tax.getType()))
			.put(IJsonNames.PERCENTAGE, tax.getPercentage())
			.put(IJsonNames.SURCHARGE, tax.getSurcharge())
			.put(IJsonNames.VAT_DEDUCTION_TYPE, VatDeductionType.name(tax.getVatDeductionType()))
			.put(IJsonNames.WITHHOLDING_TYPE, WithholdingType.name(tax.getWithholdingType()))
			.put(IJsonNames.TYPE, TaxType.name(tax.getType()))
			.put(IJsonNames.SALES_ACCOUNT, AccountJSON.to(tax.getSalesAccount() ).orElse(null))
			.put(IJsonNames.PURCHASE_ACCOUNT, AccountJSON.to(tax.getPurchaseAccount() ).orElse(null))
		;
	}
}
