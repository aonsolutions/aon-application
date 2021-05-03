package com.esferalia.aon.occam.api.json.invoice;

import java.util.Date;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.RegistryAddressJSON;
import com.esferalia.aon.occam.api.json.RegistryJSON;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.watson.server.AonDateUtils;

import es.translogia.tedi.ewok.IConstants;
import es.translogia.tedi.json.TediJSONUtils;

public class InvoiceJSON {

	public static Invoice fromJSON(JSONObject json) {
		Date date = TediJSONUtils.parseDate(json.optString("date"));
		Registry registry = getType(json.optString(IJsonNames.TYPE)).equals(InvoiceType.SALES) 
				? RegistryJSON.fromJSON(json.optJSONObject(IJsonNames.RECEIVER))
				: RegistryJSON.fromJSON(json.optJSONObject(IJsonNames.SENDER));
//		RegistryAddress raddress = getType(json.optString(IJsonNames.TYPE)).equals(InvoiceType.SALES) 
//				? RegistryAddressJSON.fromJSON(json.optJSONObject(IJsonNames.RECEIVER).optJSONObject(IJsonNames.ADDRESS))
//				: RegistryAddressJSON.fromJSON(json.optJSONObject(IJsonNames.SENDER).optJSONObject(IJsonNames.ADDRESS));
		return new Invoice()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
				.setType(getType(json.optString(IJsonNames.TYPE)))
				.setSeries(json.optString(IJsonNames.SERIES))
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
//				.setRegistryAddress(raddress.getId())
//				.setAddress(raddress.getAddress())
//				.setAddressGeozone(raddress.getGeozone())
//				.setAddressNumber(raddress.getNumber())
//				.setAddressProvince(raddress.getProvince())
//				.setAddressStreetType(raddress.getStreetType())
//				.setAddressTown(raddress.getCity())
//				.setAddressZIP(raddress.getZip())
				.setBreakdown(InvoiceBreakdownJSON.fromJSON(json.optJSONArray(IJsonNames.TAXES)))
				.setDetails(InvoiceDetailJSON.fromJSON(json.optJSONArray(IJsonNames.DETAILS)));
				//.setFinances(FinanceJSON.fromJSON(json.optJSONArray(IJsonNames.FINANCES)));
	}
	
	public static JSONObject toJSON(Invoice invoice) {
		
		String date = TediJSONUtils.formatDate(invoice.getIssueDate());
		return new JSONObject()
			.put(IJsonNames.ID, invoice.getId())
			.put(IJsonNames.DOMAIN, invoice.getDomain())
			.put(IJsonNames.SERIES, invoice.getSeries())
			.put(IJsonNames.SERIE, invoice.getSeries())
			.put(IJsonNames.NUMBER, invoice.getNumber())
			.put(IJsonNames.DATE, date) //invoice.getIssueDate())
			.put(IJsonNames.REFERENCE, invoice.getReferenceCode())
			.put(IJsonNames.TYPE, invoice.getType().name())
			.put(IJsonNames.TRANSACTION, invoice.getTransaction().name())
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

				
	}
	
	
	private static InvoiceType getType(String t) {
		if("emitida".equalsIgnoreCase(t))
			return InvoiceType.SALES;
		else if("recibida".equalsIgnoreCase(t))
			return InvoiceType.PURCHASE;
		else if("ticket".equalsIgnoreCase(t)){
			return InvoiceType.UNDEDUCTIBLE;
		}
		return InvoiceType.PURCHASE;
	}
}
