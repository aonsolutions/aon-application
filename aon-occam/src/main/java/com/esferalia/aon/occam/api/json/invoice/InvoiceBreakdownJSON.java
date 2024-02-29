package com.esferalia.aon.occam.api.json.invoice;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;

public class InvoiceBreakdownJSON {

	private InvoiceBreakdownJSON() {
	
	}
	
	public static List<InvoiceBreakdown> fromJSON(JSONArray json) {
		LinkedList<InvoiceBreakdown> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static InvoiceBreakdown fromJSON(JSONObject json) {
		return new InvoiceBreakdown()
				.setTaxType(TaxType.safeValueOf(json.optString(IJsonNames.TAX)))
				.setBase(JsonUtils.getdouble(json, IJsonNames.BASE))
				.setPercentage(JsonUtils.getdouble(json, IJsonNames.PERCENTAGE))
				.setQuota(JsonUtils.getdouble(json, IJsonNames.QUOTA))
				.setSurcharge(JsonUtils.getdouble(json, IJsonNames.SURCHARGE))
				.setSurchargeQuota(JsonUtils.getdouble(json, IJsonNames.SURCHARGE_QUOTA))
				.setWithholdingType(WithholdingType.safeValueOf(json.optString(IJsonNames.WITHHOLDING_TYPE)));
	}
	
	public static JSONArray toJSON(List<InvoiceBreakdown> breakdown) {
		JSONArray array = new JSONArray();
		breakdown.stream().forEach(tax -> array.put(toJSON(tax)));
		return array;
	}
	
	
	public static JSONObject toJSON(InvoiceBreakdown breakdown) {
		return new JSONObject()
				.put(IJsonNames.TAX, breakdown.getTaxType().getName2())
				.put(IJsonNames.TYPE, breakdown.getTaxType().getName2())
				.put(IJsonNames.BASE, breakdown.getBase())
				.put(IJsonNames.PERCENTAGE, breakdown.getPercentage())
				.put(IJsonNames.QUOTA, breakdown.getQuota())
				.put(IJsonNames.SURCHARGE, breakdown.getSurcharge())
				.put(IJsonNames.SURCHARGE_QUOTA, breakdown.getSurchargeQuota())
				.put(IJsonNames.WITHHOLDING_TYPE, breakdown.getWithholdingType());
	}
}
