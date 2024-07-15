package com.esferalia.aon.occam.api.json.invoice;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;

public class InvoiceBreakdownJSON {

	private InvoiceBreakdownJSON() {
	}
	
	public static InvoiceBreakdown fromString(String text) {
		JSONObject json = new JSONObject(text);
		return fromJSON(json); 
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
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setInvoice(JsonUtils.getInteger(json, IJsonNames.INVOICE))
			.setTaxType(TaxType.safeValueOf(json.optString(IJsonNames.TAX)))
			.setBase(JsonUtils.getdouble(json, IJsonNames.BASE))
			.setPercentage(JsonUtils.getdouble(json, IJsonNames.PERCENTAGE))
			.setQuota(JsonUtils.getdouble(json, IJsonNames.QUOTA))
			.setSurcharge(JsonUtils.getdouble(json, IJsonNames.SURCHARGE))
			.setSurchargeQuota(JsonUtils.getdouble(json, IJsonNames.SURCHARGE_QUOTA))
			.setDeductibleQuota(JsonUtils.getdouble(json, IJsonNames.DEDUCTIBLE_QUOTA))
			.setWithholdingType(WithholdingType.safeValueOf(json.optString(IJsonNames.WITHHOLDING_TYPE)))
			.setVatDeductionType(VatDeductionType.safeValueOf(json.optString(IJsonNames.VAT_DEDUCTION_TYPE)))
		;
	}
	
	public static JSONArray toJSON(List<InvoiceBreakdown> breakdown) {
		JSONArray array = new JSONArray();
		breakdown.stream().forEach(tax -> array.put(toJSON(tax)));
		return array;
	}
	
	
	public static JSONObject toJSON(InvoiceBreakdown ib) {
		String taxType = ib.getTaxType() != null 
			? ib.getTaxType().getName2() 
			: TaxType.UNKNOWN.getName2();
		return new JSONObject()
			.put(IJsonNames.ID, ib.getId())
			.put(IJsonNames.DOMAIN, ib.getDomain())
			.put(IJsonNames.INVOICE, ib.getInvoice())
			.put(IJsonNames.TAX, taxType )
			// <BORRAR>
			.put(IJsonNames.TYPE, taxType )
			// </BORRAR>
			.put(IJsonNames.BASE, ib.getBase())
			.put(IJsonNames.PERCENTAGE, ib.getPercentage())
			.put(IJsonNames.QUOTA, ib.getQuota())
			.put(IJsonNames.SURCHARGE, ib.getSurcharge())
			.put(IJsonNames.SURCHARGE_QUOTA, ib.getSurchargeQuota())
			.put(IJsonNames.DEDUCTIBLE_QUOTA, ib.getDeductibleQuota())
			.put(IJsonNames.WITHHOLDING_TYPE, ib.getWithholdingType() != null ? ib.getWithholdingType().name() : null)
			.put(IJsonNames.VAT_DEDUCTION_TYPE, ib.getVatDeductionType() != null ? ib.getVatDeductionType().name() : null)
		;
	}
}
