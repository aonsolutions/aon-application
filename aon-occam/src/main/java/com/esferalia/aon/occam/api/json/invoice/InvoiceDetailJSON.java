package com.esferalia.aon.occam.api.json.invoice;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.ItemJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceDetailJSON {
	
	private InvoiceDetailJSON() {
	
	}
	
	public static List<InvoiceDetail> fromJSON(JSONArray json) {
		LinkedList<InvoiceDetail> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)).setLine(i.shortValue()));
		}
 		return list;
	}
	
	public static InvoiceDetail fromJSON(JSONObject json) {
		String discount = JsonUtils.getString(json, IJsonNames.DISCOUNT);
		InvoiceDetail detail =  new InvoiceDetail()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setDescription(json.optString(IJsonNames.DESCRIPTION))
			.setItem(ItemJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.ITEM)))
			.setAccountCode(json.optString(IJsonNames.CATEGORY))
			.setQuantity(JsonUtils.getdouble(json, IJsonNames.QUANTITY))
			.setPrice(JsonUtils.getdouble(json, IJsonNames.PRICE))
			.setDiscountExpression(AonStringUtils.isBlank(discount) ? "0.0" : discount)
			.setTaxableBase(JsonUtils.getdouble(json, IJsonNames.AMOUNT))
			.setPrepayment(json.optBoolean(IJsonNames.PREPAYMENT))
			.setSource(InvoiceSource.TEDI)
			.setInvoiceTaxes(new LinkedList<>())
			.setInvestAsset(JsonUtils.getInteger(json, IJsonNames.INVEST_ASSET));
		
		if(json.opt(IJsonNames.PERCENTAGE) != null) {
			double quota = JsonUtils.getdouble(json, IJsonNames.QUOTA);	
			double surchargeQuota = JsonUtils.getdouble(json, IJsonNames.SURCHARGE_QUOTA);
			double deductibleQuota = JsonUtils.has(json, IJsonNames.VAT_DEDUCTIBLE_QUOTA)
				? JsonUtils.getdouble(json, IJsonNames.VAT_DEDUCTIBLE_QUOTA)
				: AonMathUtils.round(quota);
			InvoiceTax tax = new InvoiceTax()
					.setDomain(detail.getDomain())
					.setTaxType(TaxType.VAT)
					.setBase(JsonUtils.getdouble(json, IJsonNames.AMOUNT))
					.setPercentage(JsonUtils.getdouble(json, IJsonNames.PERCENTAGE))
					.setQuota(quota)
					.setSurcharge(JsonUtils.getdouble(json, IJsonNames.SURCHARGE))
					.setSurchargeQuota(surchargeQuota)
					.setVatDeductionType(VatDeductionType.WITH_RIGHT)
					.setDeductiblePercent(JsonUtils.has(json, IJsonNames.VAT_DEDUCTIBLE_PERCENT) 
							? JsonUtils.getdouble(json, IJsonNames.VAT_DEDUCTIBLE_PERCENT) 
							: 100.0)
					.setDeductibleQuota(deductibleQuota);
			detail.getInvoiceTaxes().add(tax);
		}
		if(json.optBoolean(IJsonNames.WITHHOLDING)) {
			InvoiceTax tax = new InvoiceTax()
					.setDomain(detail.getDomain())
					.setTaxType(TaxType.RETENTION)
					.setBase(JsonUtils.has(json, IJsonNames.WITHHOLDING_BASE)
						? JsonUtils.getdouble(json, IJsonNames.WITHHOLDING_BASE)
						: JsonUtils.getdouble(json, IJsonNames.AMOUNT))
					.setPercentage(JsonUtils.getdouble(json, IJsonNames.WITHHOLDING_PERCENTAGE))
					.setQuota(JsonUtils.getdouble(json, IJsonNames.WITHHOLDING_QUOTA))
					.setWithholding(true)
					.setWithholdingType(WithholdingType.safeValueOf(json.optString(IJsonNames.WITHHOLDING_TYPE)));
			detail.getInvoiceTaxes().add(tax);
		}
		
		return detail;
	}
	
	public static JSONArray toJSON(List<InvoiceDetail> details) {
		JSONArray array = new JSONArray();
		details.stream().forEach(detail -> array.put(toJSON(detail)));
		return array;
	}
	
	public static JSONObject toJSON(InvoiceDetail detail) {
		JSONObject json = new JSONObject()
				.put(IJsonNames.ID, detail.getId())
				.put(IJsonNames.DOMAIN, detail.getDomain())
				.put(IJsonNames.DESCRIPTION, detail.getDescription())
				.put(IJsonNames.ITEM, ItemJSON.toJSON(detail.getItem()))
				.put(IJsonNames.INVEST_ASSET, detail.getInvestAsset())
				.put(IJsonNames.QUANTITY, detail.getQuantity())
				.put(IJsonNames.PRICE, detail.getPrice())
				.put(IJsonNames.AMOUNT, detail.getTaxableBase())
				.put(IJsonNames.DISCOUNT, detail.getDiscount())
				.put(IJsonNames.CATEGORY, detail.getAccountCode())
				.put(IJsonNames.PREPAYMENT, detail.isPrepayment())
				.put(IJsonNames.SOURCE, detail.getSource().name());
		
		
		detail.getInvoiceTaxes().stream().forEach(tax -> {
			if(TaxType.VAT.equals(tax.getTaxType())) {
				json.put(IJsonNames.PERCENTAGE, tax.getPercentage())
					.put(IJsonNames.QUOTA, tax.getQuota())
					.put(IJsonNames.SURCHARGE, tax.getSurcharge())
					.put(IJsonNames.SURCHARGE_QUOTA, tax.getSurchargeQuota());				
			} else if(TaxType.RETENTION.equals(tax.getTaxType())) {
				json.put(IJsonNames.WITHHOLDING, true)
					.put(IJsonNames.WITHHOLDING_TYPE, getWT(tax.getWithholdingType()))
					.put(IJsonNames.WITHHOLDING_PERCENTAGE, tax.getPercentage())
					.put(IJsonNames.WITHHOLDING_QUOTA, tax.getQuota());
			}
		});
		
		return json;
	}

	private static String getWT(WithholdingType type) {
		if(WithholdingType.FARMER.equals(type)) return "IRPF_AGRI"; 
		else if(WithholdingType.MOVABLE_CAPITAL.equals(type)) return "IRPF_ALQ";
		else if(WithholdingType.RENTING.equals(type)) return "IRPF_ALQ";
		else return "IRPF_PROF";
	}
}
