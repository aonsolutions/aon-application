package com.esferalia.aon.occam.api.json.invoice;

import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.EnterpriseActivityJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.RegistryAddressJSON;
import com.esferalia.aon.occam.api.json.RegistryJSON;
import com.esferalia.aon.occam.api.json.ScopeJSON;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceStatus;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceJSON {
	
	private InvoiceJSON() {
	
	}
	
	public static Invoice fromJSON(String json) {
		return fromJSON(new JSONObject(json));
	}
	
	public static Invoice fromJSON(JSONObject json) {
		Date date = JsonUtils.getDate(json, IJsonNames.DATE);
		String category = json.optString(IJsonNames.CATEGORY);
		InvoiceType type = getType(json.optString(IJsonNames.TYPE), category);
	
		JSONObject registryJSON = InvoiceType.SALES.equals(type) 
				? JsonUtils.getJSONObject(json, IJsonNames.RECEIVER)
				: JsonUtils.getJSONObject(json, IJsonNames.SENDER);
		Registry registry = RegistryJSON.fromJSON(registryJSON);
		JSONObject addressJSON = JsonUtils.getJSONObject(registryJSON, IJsonNames.ADDRESS);
		RegistryAddress raddress = RegistryAddressJSON.fromJSON(addressJSON);
		if(raddress.getRegistry() == null) raddress.setRegistry(registry.getId());
		JSONObject rectificationInvoiceJSON = JsonUtils.getJSONObject(json, IJsonNames.RECTIFICATION_INVOICE);
		RectificationType rtype = getRectificationType(json);
		Invoice rectificationInvoice = new Invoice();
		if(RectificationType.NORMAL_RECTIFIER.equals(rtype) 
			 || RectificationType.SPECIAL_RECTIFIER.equals(rtype))
			rectificationInvoice = getRectificationInvoice(rectificationInvoiceJSON);
		return new Invoice()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
				.setType(type)
				.setSeries(json.optString(IJsonNames.SERIE))
				.setNumber(JsonUtils.getInt(json, IJsonNames.NUMBER))
				.setScope(ScopeJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.SCOPE)))
				.setTransaction(InvoiceTransactionType.safeValueOf(json.optString(IJsonNames.TRANSACTION)))
				.setReferenceCode(InvoiceType.SALES.equals(type) ? null : json.optString(IJsonNames.REFERENCE))
				.setIssueDate(date) //JsonUtils.getDate(json, IJsonNames.DATE))
				.setTaxDate(date)// JsonUtils.getDate(json, IJsonNames.DATE))
				.setInvestment(json.optBoolean(IJsonNames.INVESTMENT))
				.setService(json.optBoolean(IJsonNames.SERVICE))
				.setWithholding(json.optBoolean(IJsonNames.WITHHOLDING))
				.setWithholdingFarmer(json.optBoolean(IJsonNames.WITHHOLDING_FARMER))
				.setVatAccrualPayment(json.optBoolean(IJsonNames.VAT_ACCRUAL_PAYMENT))
				.setSurcharge(json.optBoolean(IJsonNames.SURCHARGE))
				.setRectificationType(rtype)
				.setComments(json.optString(IJsonNames.COMMENTS))
				.setRemarks(json.optString(IJsonNames.REMARKS))
				.setRectificationInvoice(rectificationInvoice.getId())
				.setRectificationInvoiceSeries(rectificationInvoice.getSeries())
				.setRectificationInvoiceNumber(rectificationInvoice.getNumber())
				.setRectificationInvoiceDate(rectificationInvoice.getIssueDate())
				.setTotal(JsonUtils.getdouble(json, IJsonNames.TOTAL))
				.setRegistryData(registry)
				.setRegistry(registry.getId())
				.setRegistryDocument(registry.getDocument())
				.setRegistryDocumentCountry(registry.getDocumentCountry())
				.setRegistryDocumentType(registry.getDocumentType())
				.setRegistryName(registry.getName())
				.setRegistryAddress(raddress.getId())
				.setAddress(raddress)
				.setBreakdown(InvoiceBreakdownJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.TAXES)))
				.setDetails(InvoiceDetailJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.DETAILS)))
				.setFinances(FinanceJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.FINANCES)))
				.setTediCategory(JsonUtils.getString(json, IJsonNames.CATEGORY))
				.setActivity(EnterpriseActivityJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.ACTIVITY)));
	}

	private static RectificationType getRectificationType(JSONObject json) {
		if(json.optBoolean(IJsonNames.RECTIFIER)) {
			return RectificationType.NORMAL_RECTIFIER;
		} else if(json.optBoolean(IJsonNames.RECTIFIED)) {
			return RectificationType.RECTIFIED;
		} else return RectificationType.NONE;
	}
	
	private static Invoice getRectificationInvoice(JSONObject json) {
		return new Invoice()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setSeries(JsonUtils.getString(json, IJsonNames.SERIES))
				.setNumber(JsonUtils.getInteger(json, IJsonNames.NUMBER))
				.setIssueDate(JsonUtils.getDate(json, IJsonNames.DATE));
	}
	
	public static JSONArray toJSON(List<Invoice> invoices) {
		JSONArray array = new JSONArray();
		invoices.stream().forEach(invoice -> array.put(toJSON(invoice)));
		return array;
	}
	
	public static JSONObject toJSON(Invoice invoice) {
		String date = AonDateUtils.format(invoice.getIssueDate(), AonDateUtils.DATE_TIME_FORMAT_AUX);
		JSONObject json = new JSONObject()
			.put(IJsonNames.STATUS, invoice.isRecorded() 
				? InvoiceStatus.SCORED.name().toLowerCase() 
				: InvoiceStatus.PENDING.name().toLowerCase())
			.put(IJsonNames.ID, invoice.getId())
			.put(IJsonNames.DOMAIN, invoice.getDomain())
			.put(IJsonNames.SERIES, invoice.getSeries())
			.put(IJsonNames.SERIE, invoice.getSeries())
			.put(IJsonNames.NUMBER, invoice.getNumber())
			.put(IJsonNames.DATE, date) //invoice.getIssueDate())
			.put(IJsonNames.REFERENCE, invoice.getReferenceCode())
			.put(IJsonNames.TYPE, invoice.getType().getTediName())
			.put(IJsonNames.TRANSACTION, invoice.getTransaction().getTediName())
			.put(IJsonNames.INVESTMENT, invoice.isInvestment())
			.put(IJsonNames.SERVICE, invoice.isService())
			.put(IJsonNames.WITHHOLDING, invoice.isWithholding())
			.put(IJsonNames.WITHHOLDING_FARMER, invoice.isWithholdingFarmer())
			.put(IJsonNames.VAT_ACCRUAL_PAYMENT, invoice.isVatAccrualPayment())
			.put(IJsonNames.SURCHARGE, invoice.isSurcharge())
			.put(IJsonNames.RECTIFIED, invoice.isRectified())
			.put(IJsonNames.RECTIFIER, invoice.isRectifier())
			//.put(IJsonNames.COMMENTS, invoice.getComments())
			.put(IJsonNames.TOTAL, invoice.getTotal())
			.put(IJsonNames.SENDER,RegistryJSON.toJSON(invoice.getRegistryData()))
			.put(IJsonNames.RECEIVER, RegistryJSON.toJSON(invoice.getRegistryData()))
			.put(IJsonNames.TAXES, InvoiceBreakdownJSON.toJSON(invoice.getBreakdown()))
			.put(IJsonNames.DETAILS, InvoiceDetailJSON.toJSON(invoice.getDetails()))
			.put(IJsonNames.FINANCES, FinanceJSON.toJSON(invoice.getFinances()))
			.put(IJsonNames.ACTIVITY, EnterpriseActivityJSON.toJSON(invoice.getActivity()))
			.put(IJsonNames.SCOPE, ScopeJSON.toJSON(invoice.getScope()))
//			.put(IJsonNames.REMARKS, new JSONArray(invoice.getRemarks()))
			.put(IJsonNames.COMMENTS, invoice.getComments());
		
		if(invoice.getDetails() != null && !invoice.getDetails().isEmpty()) {
			json.put(IJsonNames.CATEGORY, invoice.getDetails().get(0).getAccountCode());
		}
		
		if(invoice.getRemarks() != null && !invoice.getRemarks().isEmpty()) {
			json.put(IJsonNames.REMARKS, new JSONArray().put(invoice.getRemarks().replace("\n", " ")));
		}else {
			json.put(IJsonNames.REMARKS, new JSONArray());
		}
			
		if(invoice.getAddress() != null) {
			JSONObject address = RegistryAddressJSON.toJSON(invoice.getAddress());
			JSONObject registry = InvoiceType.SALES.equals(invoice.getType()) 
					? json.optJSONObject(IJsonNames.RECEIVER)
					: json.optJSONObject(IJsonNames.SENDER);
			registry.put(IJsonNames.ADDRESS, address);
		}
		return json;
	}
	
	private static InvoiceType getType(String t, String account) {
		if("emitida".equalsIgnoreCase(t)) {
			return InvoiceType.SALES;
		} else if("ticket".equalsIgnoreCase(t)){
			return InvoiceType.UNDEDUCTIBLE;
		} else if("recibida".equalsIgnoreCase(t) 
				&& !AonStringUtils.isBlank(account) 
				&& "60".equals(account.substring(0, 2))) {
			return InvoiceType.PURCHASE;
		} else return InvoiceType.EXPENSES;
	}
	
	
	public static JSONObject toMinimalJSON(Invoice inv) {
		return new JSONObject()
			.put(IJsonNames.ID, inv.getId())				
			.put(IJsonNames.ACTIVITY, inv.getActivity()==null?null:inv.getActivity().getId())
			.putOpt(IJsonNames.ACTIVITY_DESCRIPTION, inv.getActivity()==null?null:inv.getActivity().getDescription())
			.putOpt(IJsonNames.EPIGRAPH, inv.getActivity()==null?null:inv.getActivity().getEpigraph())
			.put(IJsonNames.INVOICE_TYPE, inv.getType()==null?null:inv.getType().ordinal() )
			.put(IJsonNames.DOCUMENT_NUMBER, inv.getDocumentNumber() )
			.put(IJsonNames.REFERENCE_CODE, inv.getReferenceCode() )
			.put(IJsonNames.REGISTRY_DOCUMENT, inv.getRegistryDocument() )
			.put(IJsonNames.REGISTRY_DOCUMENT_TYPE, inv.getRegistryDocumentType()==null?null:inv.getRegistryDocumentType().ordinal() )
			.put(IJsonNames.REGISTRY_DOCUMENT_COUNTRY, inv.getRegistryDocumentCountry()==null?null:inv.getRegistryDocumentCountry().getIso2() )
			.put(IJsonNames.REGISTRY_ID, inv.getRegistry() )
			.put(IJsonNames.REGISTRY_NAME, inv.getRegistryName() )
			.put(IJsonNames.ISSUE_DATE, inv.getIssueDate() == null? null : AonNumberUtils.toString(inv.getIssueDate().getTime()) )
			.put(IJsonNames.TAX_DATE, inv.getTaxDate() == null? null : AonNumberUtils.toString(inv.getTaxDate().getTime()) )
			.put(IJsonNames.TOTAL, inv.getTotal() )
		;
	}
	
	
}
