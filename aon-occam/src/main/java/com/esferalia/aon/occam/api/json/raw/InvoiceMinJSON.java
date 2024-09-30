package com.esferalia.aon.occam.api.json.raw;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.invoice.InvoiceMin;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.watson.server.AonDateUtils;

public class InvoiceMinJSON {
	
	private InvoiceMinJSON() {
	}
	
	public static Stream<InvoiceMin> stream(JSONArray jsonArray) {
		return JsonUtils.stream(jsonArray)
			.map(json -> fromJSON(json) );
		
	}
	
	public static List<InvoiceMin> fromJSON(JSONArray jsonArray) {
		return stream(jsonArray)
			.collect(Collectors.toCollection(LinkedList::new));
	}

	public static InvoiceMin fromString(String text) {
		JSONObject json = new JSONObject(text);
		return fromJSON(json); 
	}


	public static InvoiceMin fromJSON(String json) {
		return fromJSON(new JSONObject(json));
	}
	
	public static InvoiceMin fromJSON(JSONObject json) {
		if (json == null) return null;
		return new InvoiceMin()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setActivity(JsonUtils.getInteger(json, IJsonNames.ACTIVITY))
			.setActivityEpigraph(JsonUtils.getString(json, IJsonNames.EPIGRAPH))
			.setActivityName(JsonUtils.getString(json, IJsonNames.ACTIVITY_DESCRIPTION))
			.setType(InvoiceType.safeValueOf(JsonUtils.getString(json, IJsonNames.TYPE)))
			.setSeries(JsonUtils.getString(json, IJsonNames.SERIES))
			.setNumber(JsonUtils.getint(json, IJsonNames.NUMBER))
			.setReferenceCode(JsonUtils.getString(json, IJsonNames.REFERENCE_CODE))
			.setIssueDate(JsonUtils.getDate(json, IJsonNames.ISSUE_DATE))
			.setTaxDate(JsonUtils.getDate(json, IJsonNames.TAX_DATE))
			.setTransaction(InvoiceTransactionType.safeValueOf(json.getString(IJsonNames.TRANSACTION)))
			.setRegistry(JsonUtils.getInteger(json, IJsonNames.REGISTRY))
			.setRegistryDocument(JsonUtils.getString(json, IJsonNames.REGISTRY_DOCUMENT))
			.setRegistryDocumentCountry(Country.safeValueOf(JsonUtils.getString(json, IJsonNames.REGISTRY_DOCUMENT_COUNTRY)))
			.setRegistryDocumentType(DocumentType.safeValueOf(JsonUtils.getString(json, IJsonNames.REGISTRY_DOCUMENT_TYPE)))
			.setRegistryName(JsonUtils.getString(json, IJsonNames.REGISTRY_NAME))
			.setScope(JsonUtils.getInteger(json, IJsonNames.SCOPE))
			.setConfidential(JsonUtils.getboolean(json, IJsonNames.CONFIDENTIAL))
			.setRecorded(JsonUtils.getboolean(json, IJsonNames.RECORDED))
			.setRectificationType(RectificationType.safeValueOf(JsonUtils.getString(json, IJsonNames.RECTIFICATION_TYPE)))
			.setRectificationInvoiceId(JsonUtils.getInteger(json, IJsonNames.RECTIFICATION_INVOICE))
			.setTotal(JsonUtils.getdouble(json, IJsonNames.TOTAL))
			;
	}
	
	public static JSONArray toJSON(List<InvoiceMin> invoices) {
		JSONArray array = new JSONArray();
		invoices.stream().forEach(invoice -> array.put(toJSON(invoice)));
		return array;
	}
	
	public static JSONObject toJSON(InvoiceMin invoice) {
		if (invoice == null) return null;
		return new JSONObject()
			.put(IJsonNames.ID, invoice.getId())
			.put(IJsonNames.DOMAIN, invoice.getDomain())
			.put(IJsonNames.ACTIVITY, invoice.getActivity())
			.put(IJsonNames.EPIGRAPH, invoice.getActivityEpigraph())
			.put(IJsonNames.ACTIVITY_DESCRIPTION, invoice.getActivityName())
			.put(IJsonNames.TYPE, invoice.getType())
			.put(IJsonNames.SERIES, invoice.getSeries())
			.put(IJsonNames.NUMBER, invoice.getNumber())
			.put(IJsonNames.REFERENCE_CODE, invoice.getReferenceCode())
			.put(IJsonNames.TRANSACTION, invoice.getTransaction().getTediName())
			.put(IJsonNames.ISSUE_DATE, AonDateUtils.format(invoice.getIssueDate(), AonDateUtils.DATE_TIME_FORMAT_AUX))
			.put(IJsonNames.TAX_DATE, AonDateUtils.format(invoice.getTaxDate(), AonDateUtils.DATE_TIME_FORMAT_AUX))
			.put(IJsonNames.REGISTRY, invoice.getRegistry())
			.put(IJsonNames.REGISTRY_DOCUMENT, invoice.getRegistryDocument())
			.put(IJsonNames.REGISTRY_DOCUMENT_COUNTRY, invoice.getRegistryDocumentCountry())
			.put(IJsonNames.REGISTRY_DOCUMENT_TYPE, invoice.getRegistryDocumentType())
			.put(IJsonNames.REGISTRY_NAME, invoice.getRegistryName())
			.put(IJsonNames.CONFIDENTIAL, invoice.isConfidential())
			.put(IJsonNames.RECORDED, invoice.isRecorded())
			.put(IJsonNames.TOTAL, invoice.getTotal())
			.put(IJsonNames.RECTIFICATION_TYPE, Optional.ofNullable(invoice.getRectificationType()).map(e -> e.getDescription()).orElse(null))
			.put(IJsonNames.RECTIFICATION_INVOICE, invoice.getRectificationInvoiceId())
			.put(IJsonNames.SCOPE, invoice.getScope())
		;
	}
	
	
}
