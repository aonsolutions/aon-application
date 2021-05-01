package com.esferalia.aon.occam.api.json.invoice;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.RegistryJSON;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;

public class InvoiceJSON {

	public static Invoice fromJSON(JSONObject json) {
		
		return new Invoice()
				.setId(JsonUtils.getInt(json, IJsonNames.ID))
				.setDomain(JsonUtils.getInt(json, IJsonNames.DOMAIN))
				.setIssueDate(JsonUtils.getDate(json, IJsonNames.DATE))
				.setType(InvoiceType.safeValueOf(json.optString(IJsonNames.TYPE)))
				.setSeries(json.optString(IJsonNames.SERIES))
				.setNumber(JsonUtils.getInt(json, IJsonNames.NUMBER))
				.setTransaction(InvoiceTransactionType.safeValueOf(json.optString(IJsonNames.TRANSACTION)))
				.setReferenceCode(json.optString(IJsonNames.REFERENCE_CODE))
				.setIssueDate(JsonUtils.getDate(json, IJsonNames.DATE))
				.setTaxDate(JsonUtils.getDate(json, IJsonNames.DATE))
				.setInvestment(json.optBoolean(IJsonNames.INVESTMENT))
				.setService(json.optBoolean(IJsonNames.SERVICE))
				.setWithholding(json.optBoolean(IJsonNames.WITHHOLDING))
				.setWithholdingFarmer(json.optBoolean(IJsonNames.WITHHOLDING_FARMER))
				.setVatAccrualPayment(json.optBoolean(IJsonNames.VAT_ACCRUAL_PAYMENT))
				.setSurcharge(json.optBoolean(IJsonNames.SURCHARGE))
				.setRectificationType(json.optBoolean(IJsonNames.RECTIFIED) ? RectificationType.RECTIFIED : RectificationType.NONE)
				.setComments(json.optString(IJsonNames.COMMENTS))
				.setTotal(JsonUtils.getdouble(json, IJsonNames.TOTAL))
				.setRegistryData(InvoiceType.safeValueOf(json.optString(IJsonNames.TYPE)).equals(InvoiceType.SALES) 
						? RegistryJSON.fromJSON(json.optJSONObject(IJsonNames.RECEIVER))
						: RegistryJSON.fromJSON(json.optJSONObject(IJsonNames.SENDER)))

				.setBreakdown(InvoiceBreakdownJSON.fromJSON(json.optJSONArray(IJsonNames.TAXES)))
				.setDetails(InvoiceDetailJSON.fromJSON(json.optJSONArray(IJsonNames.DETAILS)));
				//.setFinances(FinanceJSON.fromJSON(json.optJSONArray(IJsonNames.FINANCES)));
	}
	
	public static JSONObject toJSON(Invoice invoice) {
		return new JSONObject();
	}
	
}
