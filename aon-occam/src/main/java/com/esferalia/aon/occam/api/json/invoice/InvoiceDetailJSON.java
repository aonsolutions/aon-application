package com.esferalia.aon.occam.api.json.invoice;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;

public class InvoiceDetailJSON {
	
	public static LinkedList<InvoiceDetail> fromJSON(JSONArray json) {
		LinkedList<InvoiceDetail> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static InvoiceDetail fromJSON(JSONObject json) {
		InvoiceDetail detail =  new InvoiceDetail()
			.setId(JsonUtils.getInt(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInt(json, IJsonNames.DOMAIN))
			.setDescription(json.optString(IJsonNames.DESCRIPTION))
			.setItem(new OldItem().setId(JsonUtils.getInteger(json, IJsonNames.ITEM)))
			.setAccountCode(json.optString(IJsonNames.CATEGORY))
			.setQuantity(JsonUtils.getdouble(json, IJsonNames.QUANTITY))
			.setPrice(JsonUtils.getdouble(json, IJsonNames.PRICE))
			.setDiscountExpression(json.optString(IJsonNames.DISCOUNT))
			.setTaxableBase(JsonUtils.getdouble(json, IJsonNames.AMOUNT))
			.setSurcharge(JsonUtils.getdouble(json, IJsonNames.SURCHARGE))
			.setPrepayment(json.optBoolean(IJsonNames.PREPAYMENT))
			.setInvoiceTaxes(new LinkedList<InvoiceTax>());
		
		if(json.opt(IJsonNames.PERCENTAGE) != null) {
			InvoiceTax tax = new InvoiceTax()
					.setDomain(detail.getDomain())
					.setTaxType(TaxType.VAT)
					.setBase(JsonUtils.getdouble(json, IJsonNames.AMOUNT))
					.setPercentage(JsonUtils.getdouble(json, IJsonNames.PERCENTAGE))
					.setQuota(JsonUtils.getdouble(json, IJsonNames.QUOTA))
					.setSurcharge(JsonUtils.getdouble(json, IJsonNames.SURCHARGE))
					.setSurchargeQuota(JsonUtils.getdouble(json, IJsonNames.SURCHARGE_QUOTA))
					.setVatDeductionType(VatDeductionType.WITH_RIGHT)
					.setDeductiblePercent(100.0)
					.setDeductibleQuota(JsonUtils.getdouble(json, IJsonNames.QUOTA));
			detail.getInvoiceTaxes().add(tax);
		}
		if(json.optBoolean(IJsonNames.WITHHOLDING)) {
			InvoiceTax tax = new InvoiceTax()
					.setDomain(detail.getDomain())
					.setTaxType(TaxType.RETENTION)
					.setBase(JsonUtils.getdouble(json, IJsonNames.AMOUNT))
					.setPercentage(JsonUtils.getdouble(json, IJsonNames.WITHHOLDING_PERCENTAGE))
					.setQuota(JsonUtils.getdouble(json, IJsonNames.WITHHOLDING_QUOTA))
					.setWithholding(true)
					.setWithholdingType(WithholdingType.safeValueOf(json.optString(IJsonNames.WITHHOLDING_TYPE)));
			detail.getInvoiceTaxes().add(tax);
		}
		
		return detail;
	}
	
	public static JSONObject toJSON(InvoiceDetail detail) {
		JSONObject json = new JSONObject()
				.put(IJsonNames.ID, detail.getId())
				.put(IJsonNames.DOMAIN, detail.getDomain())
				.put(IJsonNames.DESCRIPTION, detail.getDescription())
				.put(IJsonNames.ITEM, detail.getItem().getId())
				.put(IJsonNames.QUANTITY, detail.getQuantity())
				.put(IJsonNames.PRICE, detail.getPrice())
				.put(IJsonNames.DISCOUNT, detail.getDiscountExpression());

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
