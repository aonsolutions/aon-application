package net.aonsolutions.occam.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import net.aonsolutions.occam.api.AonNames;
import net.aonsolutions.occam.api.constants.Country;
import net.aonsolutions.occam.api.constants.DocumentType;
import net.aonsolutions.occam.api.constants.InvoiceType;
import net.aonsolutions.occam.api.constants.TransactionType;
import net.aonsolutions.occam.api.invoicing.Invoice;
import net.aonsolutions.watson.client.util.AonCollectionUtils;
import net.aonsolutions.watson.server.AonObjectUtils;

public class InvoiceJSON {
	
	private InvoiceJSON() {
	}
	
	public static List<Invoice> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return AonJSONUtils.stream(array)
			.map(InvoiceJSON::from)
			.toList();		
	}
	
	public static Invoice from(JSONObject json) {
		if (json == null) return null; 
		return new Invoice()
			.setId(AonJSONUtils.getInteger(json, AonNames.ID))
			.setDomain(AonJSONUtils.getInteger(json, AonNames.DOMAIN))
			.setActivity( ActivityJSON.from(AonJSONUtils.getObject(json, AonNames.ACTIVITY)))
			.setSeries(AonJSONUtils.getString(json, AonNames.SERIES))
			.setNumber(AonJSONUtils.getInteger(json, AonNames.NUMBER))
			.setReferenceCode(AonJSONUtils.getString(json, AonNames.REFERENCE_CODE))
			.setIssueDate(AonJSONUtils.getDate(json, AonNames.ISSUE_DATE))
			.setTaxDate(AonJSONUtils.getDate(json, AonNames.TAX_DATE))
			.setConfidential(AonJSONUtils.getBoolean(json, AonNames.CONFIDENTIAL))
			.setRegistry(AonJSONUtils.getInteger(json, AonNames.REGISTRY))
			.setDocument(AonJSONUtils.getString(json, AonNames.DOCUMENT))
			.setDocumentType( DocumentType.safeValueOf(AonJSONUtils.getString(json, AonNames.DOCUMENT_TYPE)).orElse(null) )
			.setDocumentCountry( Country.safeValueOf(AonJSONUtils.getString(json, AonNames.DOCUMENT_COUNTRY)).orElse(null) )
			.setName(AonJSONUtils.getString(json, AonNames.NAME))
			.setScope(ScopeJSON.from(AonJSONUtils.getObject(json, AonNames.SCOPE)))
			.setType( InvoiceType.safeValueOf(AonJSONUtils.getString(json, AonNames.TYPE)).orElse(null) )
			.setTransaction( TransactionType.safeValueOf(AonJSONUtils.getString(json, AonNames.TRANSACTION)).orElse(null) )
			.setSurcharge(AonJSONUtils.getBoolean(json, AonNames.SURCHARGE))
			.setWithholding(AonJSONUtils.getBoolean(json, AonNames.WITHHOLDING))
			.setWithholdingFarmer(AonJSONUtils.getBoolean(json, AonNames.WITHHOLDING_FARMER))
			.setVatAccrualPayment(AonJSONUtils.getBoolean(json, AonNames.VAT_ACCRUAL_PAYMENT))
			.setInvestment(AonJSONUtils.getBoolean(json, AonNames.INVESTMENT))
			.setService(AonJSONUtils.getBoolean(json, AonNames.SERVICE))
			.setAnnulled(AonJSONUtils.getBoolean(json, AonNames.ANNULLED))
			.setTotal(AonJSONUtils.getDouble(json, AonNames.TOTAL))
			.setAudit( AuditJSON.from(AonJSONUtils.getObject(json, AonNames.AUDIT)))
			.setDetails(InvoiceDetailJSON.from(AonJSONUtils.getArray(json, AonNames.DETAILS)))
			.setBreakdown(InvoiceBreakdownJSON.from(AonJSONUtils.getArray(json, AonNames.BREAKDOWN)))
		;
	}
	
	public static JSONArray to(List<Invoice> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<Invoice> stream) {
		return stream
			.map(InvoiceJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(Invoice invoice) {
		if (invoice == null) return null;
		return new JSONObject()
			.put(AonNames.ID, invoice.getId())
			.putOpt(AonNames.DOMAIN, invoice.getDomain())
			.putOpt(AonNames.ACTIVITY, invoice.getActivity().map(ActivityJSON::to).orElse(null))
			.putOpt(AonNames.SERIES, invoice.getSeries())
			.putOpt(AonNames.NUMBER, invoice.getNumber())
			.putOpt(AonNames.REFERENCE_CODE, invoice.getReferenceCode())
			.putOpt(AonNames.ISSUE_DATE, AonJSONUtils.formatDate(invoice.getIssueDate()))
			.putOpt(AonNames.TAX_DATE, AonJSONUtils.formatDate(invoice.getTaxDate()))
			.putOpt(AonNames.CONFIDENTIAL, invoice.isConfidential())
			.putOpt(AonNames.REGISTRY, invoice.getRegistry())
			.putOpt(AonNames.DOCUMENT, invoice.getDocument())
			.putOpt(AonNames.DOCUMENT_TYPE, AonObjectUtils.ifNotNullGet(invoice.getDocumentType(), Object::toString ))
			.putOpt(AonNames.DOCUMENT_COUNTRY, AonObjectUtils.ifNotNullGet(invoice.getDocumentCountry(), Object::toString ))
			.putOpt(AonNames.NAME, invoice.getName())
			.putOpt(AonNames.SCOPE, invoice.getScope().map( ScopeJSON::to ).orElse(null) )
			.putOpt(AonNames.TYPE, AonObjectUtils.ifNotNullGet(invoice.getType(), Object::toString ))
			.putOpt(AonNames.TRANSACTION, AonObjectUtils.ifNotNullGet(invoice.getTransaction(), Object::toString ))			
			.putOpt(AonNames.SURCHARGE, invoice.isSurcharge())			
			.putOpt(AonNames.WITHHOLDING, invoice.isWithholding())
			.putOpt(AonNames.WITHHOLDING_FARMER, invoice.isWithholdingFarmer())
			.putOpt(AonNames.VAT_ACCRUAL_PAYMENT, invoice.isVatAccrualPayment())
			.putOpt(AonNames.INVESTMENT, invoice.isInvestment())
			.putOpt(AonNames.SERVICE, invoice.isService())
			.putOpt(AonNames.ANNULLED, invoice.isAnnulled())
			.putOpt(AonNames.TOTAL, invoice.getTotal())
			.putOpt(AonNames.AUDIT, AuditJSON.to(invoice.getAudit().orElse(null)))
			.putOpt(AonNames.DETAILS, InvoiceDetailJSON.to(invoice.getDetails().orElse(null)))
			.putOpt(AonNames.BREAKDOWN, InvoiceBreakdownJSON.to(invoice.getBreakdown().orElse(null)))
			;
	}
}
