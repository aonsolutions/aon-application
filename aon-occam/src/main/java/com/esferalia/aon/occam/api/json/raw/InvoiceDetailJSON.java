package com.esferalia.aon.occam.api.json.raw;

import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.AccountJSON;
import com.esferalia.aon.occam.api.json.WorkplaceJSON;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.invoice.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceDetail;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.watson.server.AonDateUtils;

public class InvoiceDetailJSON {
	
	private InvoiceDetailJSON() {
	
	}
	
	public static InvoiceDetail fromJSON(Invoice invoice, JSONObject json) {
		if (JsonUtils.isEmpty(json)) return null;
		InvoiceDetail detail =  new InvoiceDetail()
			.setSelected(JsonUtils.getboolean(json, IJsonNames.SELECTED))
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setInvoice(JsonUtils.getInteger(json, IJsonNames.INVOICE))
			.setInvestAsset(InvestAssetJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.INVEST_ASSET)))
			.setProject(JsonUtils.getInteger(json, IJsonNames.PROJECT))
			.setLine(JsonUtils.getShort(json, IJsonNames.LINE))
			.setItem(ItemJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.ITEM)))
			.setDescription(JsonUtils.getString(json,IJsonNames.DESCRIPTION))
			.setQuantity(JsonUtils.getdouble(json, IJsonNames.QUANTITY))
			.setPrice(JsonUtils.getdouble(json, IJsonNames.PRICE))
			.setDiscount(JsonUtils.getdouble(json, IJsonNames.DISCOUNT))
			.setTaxableBase(JsonUtils.getdouble(json, IJsonNames.TAXABLE_BASE))
			.setTaxes(JsonUtils.getdouble(json, IJsonNames.TAXES))
			.setPrepayment(JsonUtils.getboolean(json,IJsonNames.PREPAYMENT))
			.setSeller(SellerJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.SELLER)))
			.setWorkplace(WorkplaceJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.WORKPLACE)))
			.setWarehouse(WarehouseJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.WAREHOUSE)))
			.setSource(InvoiceSource.safeValueOf(json.optString(IJsonNames.SOURCE)))
			.setSourceId(JsonUtils.getInteger(json, IJsonNames.SOURCE_ID))
			.setCreationUser(JsonUtils.getString(json, IJsonNames.CREATION_USER))
			.setCreationDate(JsonUtils.getDate(json, IJsonNames.CREATION_DATE))
			.setModificationUser(JsonUtils.getString(json, IJsonNames.MODIFICATION_USER))
			.setModificationDate(JsonUtils.getDate(json, IJsonNames.MODIFICATION_DATE))
			.setExpAccount(AccountJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.EXP_ACCOUNT)))
		;
		JsonUtils.stream(json, IJsonNames.INVOICE_TAXES)
			.map( taxJson -> InvoiceTaxJSON.fromJSON(taxJson))
			.forEach( tax -> detail.addInvoiceTax(invoice, tax) );
		return detail;
	}
	
	public static JSONArray toJSON(Stream<InvoiceDetail> details) {
		return toJSON(details.toList());
	}
	public static JSONArray toJSON(List<InvoiceDetail> details) {
		JSONArray array = new JSONArray();
		details.stream().forEach(detail -> array.put(toJSON(detail)));
		return array;
	}
	
	public static JSONObject toJSON(InvoiceDetail detail) {
		if (detail == null) return null;
		return new JSONObject()
			.put(IJsonNames.ID, detail.isSelected())
			.put(IJsonNames.ID, detail.getId())
			.put(IJsonNames.DOMAIN, detail.getDomain())
			.put(IJsonNames.INVOICE, detail.getInvoice())
			.put(IJsonNames.INVEST_ASSET, InvestAssetJSON.toJSON( detail.getInvestAsset().orElse(null)))
			.put(IJsonNames.PROJECT, detail.getProject())
			.put(IJsonNames.LINE, detail.getLine())
			.put(IJsonNames.ITEM, ItemJSON.toJSON(detail.getItem().orElse(null)))
			.put(IJsonNames.DESCRIPTION, detail.getDescription())
			.put(IJsonNames.QUANTITY, detail.getQuantity())
			.put(IJsonNames.PRICE, detail.getPrice())
			.put(IJsonNames.DISCOUNT, detail.getDiscount())
			.put(IJsonNames.TAXABLE_BASE, detail.getTaxableBase())
			.put(IJsonNames.TAXES, detail.getTaxes())
			.put(IJsonNames.PREPAYMENT, detail.isPrepayment())
			.put(IJsonNames.SELLER, SellerJSON.toJSON( detail.getSeller().orElse(null)))
			.put(IJsonNames.WORKPLACE, WorkplaceJSON.toJSON( detail.getWorkplace()))
			.put(IJsonNames.WAREHOUSE, WarehouseJSON.toJSON( detail.getWarehouse().orElse(null)))
			.put(IJsonNames.SOURCE, InvoiceSource.safeValueOf(detail.getSource()))
			.put(IJsonNames.SOURCE_ID, detail.getSourceId())
			.put(IJsonNames.CREATION_USER, detail.getCreationUser())
			.put(IJsonNames.CREATION_DATE, AonDateUtils.dateTimeFormat(detail.getCreationDate()))
			.put(IJsonNames.MODIFICATION_USER, detail.getModificationUser())
			.put(IJsonNames.MODIFICATION_DATE, AonDateUtils.dateTimeFormat(detail.getModificationDate()))
			.put(IJsonNames.EXP_ACCOUNT, AccountJSON.toJSON( detail.getExpAccount().orElse(null)))
			.put(IJsonNames.INVOICE_TAXES, InvoiceTaxJSON.toJSON( detail.getInvoiceTaxes() ))
		;
	}

}
