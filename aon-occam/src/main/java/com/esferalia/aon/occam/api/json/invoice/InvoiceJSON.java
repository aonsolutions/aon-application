package com.esferalia.aon.occam.api.json.invoice;

import java.util.Date;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.RegistryAddressJSON;
import com.esferalia.aon.occam.api.json.RegistryJSON;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceStatus;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.watson.util.AonStringUtils;

import es.translogia.tedi.json.TediJSONUtils;

public class InvoiceJSON {

	public static Invoice fromJSON(JSONObject json) {
		Date date = TediJSONUtils.parseDate(json.optString("date"));
		String category = json.optString(IJsonNames.CATEGORY);
		InvoiceType type = getType(json.optString(IJsonNames.TYPE), category);

		JSONObject registryJSON = InvoiceType.SALES.equals(type) 
				? JsonUtils.getJSONObject(json, IJsonNames.RECEIVER)
				: JsonUtils.getJSONObject(json, IJsonNames.SENDER);
		Registry registry = RegistryJSON.fromJSON(registryJSON);
		JSONObject addressJSON = JsonUtils.getJSONObject(registryJSON, IJsonNames.ADDRESS);
		RegistryAddress raddress = RegistryAddressJSON.fromJSON(addressJSON);
		return new Invoice()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
				.setType(type)
				.setSeries(json.optString(IJsonNames.SERIE))
				.setNumber(JsonUtils.getInt(json, IJsonNames.NUMBER))
				.setTransaction(InvoiceTransactionType.safeValueOf(json.optString(IJsonNames.TRANSACTION)))
				.setReferenceCode(json.optString(IJsonNames.REFERENCE_CODE))
				.setIssueDate(date) //JsonUtils.getDate(json, IJsonNames.DATE))
				.setTaxDate(date)// JsonUtils.getDate(json, IJsonNames.DATE))
				.setInvestment(json.optBoolean(IJsonNames.INVESTMENT))
				.setService(json.optBoolean(IJsonNames.SERVICE))
				.setWithholding(json.optBoolean(IJsonNames.WITHHOLDING))
				.setWithholdingFarmer(json.optBoolean(IJsonNames.WITHHOLDING_FARMER))
				.setVatAccrualPayment(json.optBoolean(IJsonNames.VAT_ACCRUAL_PAYMENT))
				.setSurcharge(json.optBoolean(IJsonNames.SURCHARGE))
				.setRectificationType(json.optBoolean(IJsonNames.RECTIFIED) ? RectificationType.RECTIFIED : RectificationType.NONE)
				//.setComments(json.optString(IJsonNames.COMMENTS))
				.setTotal(JsonUtils.getdouble(json, IJsonNames.TOTAL))
				.setRegistryData(registry)
				.setRegistry(registry.getId())
				.setRegistryDocument(registry.getDocument())
				.setRegistryDocumentCountry(registry.getDocumentCountry())
				.setRegistryDocumentType(registry.getDocumentType())
				.setRegistryName(registry.getName())
				.setRegistryAddress(raddress.getId())
				.setRegistryAddressData(raddress)
//				.setAddress(raddress.getAddress())
//				.setAddressGeozone(raddress.getGeozone())
//				.setAddressNumber(raddress.getNumber())
//				.setAddressProvince(raddress.getProvince())
//				.setAddressStreetType(raddress.getStreetType())
//				.setAddressTown(raddress.getCity())
//				.setAddressZIP(raddress.getZip())
				.setBreakdown(InvoiceBreakdownJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.TAXES)))
				.setDetails(InvoiceDetailJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.DETAILS)));
				//.setFinances(FinanceJSON.fromJSON(json.optJSONArray(IJsonNames.FINANCES)));
	}
	
	public static JSONObject toJSON(Invoice invoice) {
		String date = TediJSONUtils.formatDate(invoice.getIssueDate());
		InvoiceStatus status = invoice.getStatus() != null 
				? InvoiceStatus.safeValueOf(invoice.getStatus())
				: InvoiceStatus.PENDING; 
		
		JSONObject json = new JSONObject()
			.put(IJsonNames.STATUS, status.name().toLowerCase())
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
			//.put(IJsonNames.COMMENTS, invoice.getComments())
			.put(IJsonNames.TOTAL, invoice.getTotal())
			.put(IJsonNames.SENDER,RegistryJSON.toJSON(invoice.getRegistryData()))
			.put(IJsonNames.RECEIVER, RegistryJSON.toJSON(invoice.getRegistryData()))
			.put(IJsonNames.TAXES, InvoiceBreakdownJSON.toJSON(invoice.getBreakdown()))
			.put(IJsonNames.DETAILS, InvoiceDetailJSON.toJSON(invoice.getDetails()))
			.put(IJsonNames.FINANCES, FinanceJSON.toJSON(invoice.getFinances()));
		
		if(invoice.getDetails() != null && invoice.getDetails().size() > 0) {
			json.put(IJsonNames.CATEGORY, invoice.getDetails().get(0).getAccountCode());
		}
			
		if(invoice.getRegistryAddressData() != null) {
			JSONObject address = RegistryAddressJSON.toJSON(invoice.getRegistryAddressData());
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
}
