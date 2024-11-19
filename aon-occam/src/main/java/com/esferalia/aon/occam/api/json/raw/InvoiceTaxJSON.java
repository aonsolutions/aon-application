package com.esferalia.aon.occam.api.json.raw;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.AccountJSON;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCalculator;
import com.esferalia.aon.occam.api.model.invoice.InvoiceTax;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class InvoiceTaxJSON {
	
	private InvoiceTaxJSON() {
	
	}
	
	public static Stream<InvoiceTax> stream(JSONArray jsonArray) {
		return JsonUtils.stream(jsonArray)
			.map(json -> fromJSON(json) );
		
	}
	
	public static List<InvoiceTax> fromJSON(JSONArray jsonArray) {
		return stream(jsonArray)
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static InvoiceTax fromString(String text) {
		JSONObject json = new JSONObject(text);
		return fromJSON(json); 
	}
	
	public static InvoiceTax fromJSON(JSONObject json) {
		InvoiceTax tax = new InvoiceTax()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setInvoiceDetail(JsonUtils.getInteger(json, IJsonNames.INVOICE_DETAIL))
			.setTaxType(TaxType.safeValueOf(json.optString(IJsonNames.TAX_TYPE)))
			.setBase(JsonUtils.getdouble(json, IJsonNames.BASE))
			.setPercentage(JsonUtils.getdouble(json, IJsonNames.PERCENTAGE))
			.setQuota(JsonUtils.getdouble(json, IJsonNames.QUOTA))
			.setSurcharge(JsonUtils.getdouble(json, IJsonNames.SURCHARGE_PERCENT))
			.setSurchargeQuota(JsonUtils.getdouble(json, IJsonNames.SURCHARGE_QUOTA))
			.setDeductiblePercent(JsonUtils.getdouble(json, IJsonNames.DEDUCTIBLE_PERCENT))
			.setDeductibleQuota(JsonUtils.getdouble(json, IJsonNames.DEDUCTIBLE_QUOTA))
			.setDirectTaxPercent(JsonUtils.getdouble(json, IJsonNames.DIRECT_TAX_PERCENT))
			.setVatDeductionType(VatDeductionType.safeValue(json.optString(IJsonNames.VAT_DEDUCTION_TYPE)))
			.setWithholdingType(WithholdingType.safeValue(json.optString(IJsonNames.WITHHOLDING_TYPE)))
			.setWithholdingAccount(AccountJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.WITHHOLDING_ACCOUNT)))
			.setOutputAccount(AccountJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.OUTPUT_ACCOUNT)))
			.setInputAccount(AccountJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.INPUT_ACCOUNT)))
			.setAdjAccount(AccountJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.ADJ_ACCOUNT)))
			.setAdjDirectTaxAccount(AccountJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.ADJ_DIRECT_TAX_ACCOUNT)))
		;
		return tax
			.setQuotaEdited(InvoiceCalculator.isQuotaEdited(tax))
			.setSurchargeQuotaEdited(InvoiceCalculator.isSurchargeQuotaEdited(tax))
			.setDeductibleQuotaEdited(InvoiceCalculator.isDeductibleQuotaEdited(tax))
		;
	}
	
	public static JSONArray toJSON(Stream<InvoiceTax> invoiceTaxes) {
		return toJSON(invoiceTaxes.toList()); 
	}

	public static JSONArray toJSON(List<InvoiceTax> details) {
		if (AonCollectionUtils.isEmpty(details)) return null;
		JSONArray array = new JSONArray();
		details.stream().forEach(detail -> array.put(toJSON(detail)));
		return array;
	}
	
	public static JSONObject toJSON(InvoiceTax tax) {
		return new JSONObject()
			.put(IJsonNames.ID, tax.getId())
			.put(IJsonNames.DOMAIN, tax.getDomain())
			.put(IJsonNames.INVOICE, tax.getInvoiceDetail())
			.put(IJsonNames.TAX_TYPE, TaxType.safeValueOf(tax.getTaxType()))
			.put(IJsonNames.BASE, tax.getBase())
			.put(IJsonNames.PERCENTAGE, tax.getPercentage())
			.put(IJsonNames.QUOTA, tax.getQuota())
			.put(IJsonNames.SURCHARGE_PERCENT, tax.getSurcharge())
			.put(IJsonNames.SURCHARGE_QUOTA, tax.getSurchargeQuota())
			.put(IJsonNames.DEDUCTIBLE_PERCENT, tax.getSurchargeQuota())
			.put(IJsonNames.DEDUCTIBLE_QUOTA, tax.getDeductibleQuota())
			.put(IJsonNames.DIRECT_TAX_PERCENT, tax.getDirectTaxPercent())
			.put(IJsonNames.VAT_DEDUCTION_TYPE, VatDeductionType.safeValueOf(tax.getVatDeductionType()))
			.put(IJsonNames.WITHHOLDING_TYPE, WithholdingType.safeValueOf(tax.getWithholdingType()))
			.put(IJsonNames.WITHHOLDING_ACCOUNT, AccountJSON.toJSON( tax.getWithholdingAccount().orElse(null)))
			.put(IJsonNames.OUTPUT_ACCOUNT, AccountJSON.toJSON( tax.getOutputAccount().orElse(null)))
			.put(IJsonNames.INPUT_ACCOUNT, AccountJSON.toJSON( tax.getInputAccount().orElse(null)))
			.put(IJsonNames.ADJ_ACCOUNT, AccountJSON.toJSON( tax.getAdjAccount().orElse(null)))
			.put(IJsonNames.ADJ_DIRECT_TAX_ACCOUNT, AccountJSON.toJSON( tax.getAdjDirectTaxAccount().orElse(null)))
			.put(IJsonNames.QUOTA_EDITED, tax.isQuotaEdited())
			.put(IJsonNames.SURCHARGE_QUOTA_EDITED, tax.isSurchargeQuotaEdited())
			.put(IJsonNames.DEDUCTIBLE_QUOTA_EDITED, tax.isDeductibleQuotaEdited())
		;
	}

}
