package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class VatContextJSON {
	
	private VatContextJSON() {
		
	}
	
	public static LinkedList<VatContext> fromJSON(JSONArray json) {
		LinkedList<VatContext> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static VatContext fromJSON(JSONObject json) {
		return new VatContext()
			.setInvoice(JsonUtils.getInteger(json, IJsonNames.INVOICE))
			.setActivity(JsonUtils.optInteger(json, IJsonNames.ACTIVITY))
			.setActivityDescription(JsonUtils.getString(json, IJsonNames.ACTIVITY_DESCRIPTION))
			.setVatRegime(VATRegime.safeValueOf(JsonUtils.optInteger(json,IJsonNames.VAT_REGIME)))
			.setVatSurchargeRegime( json.optBoolean(IJsonNames.VAT_SURCHARGE_REGIME) )
			.setEpigraph(JsonUtils.getString(json, IJsonNames.EPIGRAPH))
			.setDocumentNumber(JsonUtils.optString(json, IJsonNames.DOCUMENT_NUMBER))
			.setReferenceCode(JsonUtils.optString(json, IJsonNames.REFERENCE_CODE))
			.setRegistryDocument(JsonUtils.optString(json, IJsonNames.REGISTRY_DOCUMENT))
			.setRegistryDocumentType(DocumentType.safeValueOf(JsonUtils.optInteger(json, IJsonNames.REGISTRY_DOCUMENT_TYPE)))
			.setRegistryDocumentCountry(Country.safeValueOf(JsonUtils.optString(json, IJsonNames.REGISTRY_DOCUMENT_COUNTRY)))
			.setRegistry(JsonUtils.optInteger(json, IJsonNames.REGISTRY_ID))
			.setRegistryName(JsonUtils.optString(json, IJsonNames.REGISTRY_NAME))
			.setIssueDate(JsonUtils.getDate(json, IJsonNames.ISSUE_DATE))
			.setTaxDate(JsonUtils.getDate(json, IJsonNames.TAX_DATE))
			.setCreationDate(JsonUtils.getDate(json, IJsonNames.CREATION_DATE))
			.setRegContableDate(JsonUtils.getDate(json, IJsonNames.REG_CONTABLE_DATE))
			.setDetailDescription(JsonUtils.getString(json, IJsonNames.DETAIL_DESCRIPTION))
			.setInsidePeriod( json.optBoolean(IJsonNames.INSIDE_PERIOD))
			.setInvoiceType(InvoiceType.safeValueOf(JsonUtils.optInteger(json, IJsonNames.INVOICE_TYPE)))
			.setRectificationType(RectificationType.safeValueOf(JsonUtils.optInteger(json, IJsonNames.RECTIFICATION_TYPE)))
			.setRectificationInvoice(JsonUtils.optInteger(json, IJsonNames.RECTIFICATION_INVOICE))
			.setService( json.optBoolean(IJsonNames.SERVICE))
			.setTransaction( InvoiceTransactionType.safeValueOf(JsonUtils.optInteger(json, IJsonNames.TRANSACTION)))
			.setInvestment( json.optBoolean(IJsonNames.INVESTMENT))
			.setVatAccrualRegime( json.optBoolean(IJsonNames.VAT_ACCRUAL_REGIME))
			.setVatDeductionType( VatDeductionType.safeValueOf(JsonUtils.optInteger(json, IJsonNames.VAT_DEDUCTION_TYPE)))
			.setFarmerRegime( json.optBoolean(IJsonNames.FARMER_REGIME))
			.setPrepayment( json.optBoolean(IJsonNames.PREPAYMENT))
			.setVatImportation( json.optBoolean(IJsonNames.VAT_IMPORTATION))
			.setDuaLinked( json.optBoolean(IJsonNames.DUA_LINKED))
			.setBase( json.optDouble(IJsonNames.BASE))
			.setPercentage( json.optDouble(IJsonNames.PERCENTAGE))
			.setQuota( json.optDouble(IJsonNames.QUOTA))
			.setInvestAsset( JsonUtils.optInteger(json, IJsonNames.INVEST_ASSET))
			.setDeductiblePercent( json.optDouble(IJsonNames.DEDUCTIBLE_PERCENT))
			.setDeductibleQuota( json.optDouble(IJsonNames.DEDUCTIBLE_QUOTA))
			.setSurcharge( json.optBoolean(IJsonNames.SURCHARGE))
			.setSurchargePercent( json.optDouble(IJsonNames.SURCHARGE_PERCENT))
			.setSurchargeQuota( json.optDouble(IJsonNames.SURCHARGEQUOTA))
			.setProrrated( json.optBoolean(IJsonNames.PRORRATED))
			.setProrratePercent( json.optDouble(IJsonNames.PRORRATE_PERCENT))
			.setProrrateQuota( json.optDouble(IJsonNames.PRORRATE_QUOTA))
			.setSiiStatus(JsonUtils.getString(json, IJsonNames.SII_STATUS))
			.setAmortizationDescription(JsonUtils.getString(json, IJsonNames.AMORTIZATION_DESCRIPTION))
			.setAmortizationPercentage( JsonUtils.getDouble(json,IJsonNames.AMORTIZATION_PERCENT))
			.setAmortizationInitialDate(JsonUtils.getDate(json, IJsonNames.AMORTIZATION_INITIAL_DATE))
			.setFinancePending( json.optBoolean(IJsonNames.FINANCE_PENDING))
			.setAmount347( json.optDouble(IJsonNames.AMOUNT_347))
			.setHasRetention( json.optBoolean(IJsonNames.RETENTION))
			;
	}
	
	public static JSONArray toJSON(LinkedList<VatContext> tasks) {
		return toJSON(tasks.stream());
	}
	
	public static JSONArray toJSON(Stream<VatContext> vatContexts) {
		JSONArray array = new JSONArray();
		vatContexts.forEach(task -> array.put(toJSON(task)));
		return array;
	}
	
	public static JSONObject toJSON(VatContext vat) {
		return new JSONObject()
			.put(IJsonNames.INVOICE, vat.getInvoice())				
			.put(IJsonNames.ACTIVITY, vat.getActivity())
			.putOpt(IJsonNames.ACTIVITY_DESCRIPTION, vat.getActivityDescription())
			.put(IJsonNames.VAT_REGIME, vat.getVatRegime()==null?null:vat.getVatRegime().ordinal() )
			.put(IJsonNames.VAT_SURCHARGE_REGIME, vat.isVatSurchargeRegime() )
			.putOpt(IJsonNames.EPIGRAPH, vat.getEpigraph() )
			.put(IJsonNames.DOCUMENT_NUMBER, vat.getDocumentNumber() )
			.put(IJsonNames.REFERENCE_CODE, vat.getReferenceCode() )
			.put(IJsonNames.REGISTRY_DOCUMENT, vat.getRegistryDocument() )
			.put(IJsonNames.REGISTRY_DOCUMENT_TYPE, vat.getRegistryDocumentType()==null?null:vat.getRegistryDocumentType().ordinal() )
			.put(IJsonNames.REGISTRY_DOCUMENT_COUNTRY, vat.getRegistryDocumentCountry()==null?null:vat.getRegistryDocumentCountry().getIso2() )
			.put(IJsonNames.REGISTRY_ID, vat.getRegistry() )
			.put(IJsonNames.REGISTRY_NAME, vat.getRegistryName() )
			.put(IJsonNames.ISSUE_DATE, vat.getIssueDate() == null? null : AonNumberUtils.toString(vat.getIssueDate().getTime()) )
			.put(IJsonNames.TAX_DATE, vat.getTaxDate() == null? null : AonNumberUtils.toString(vat.getTaxDate().getTime()) )
			.put(IJsonNames.CREATION_DATE, vat.getCreationDate() == null? null : AonNumberUtils.toString(vat.getCreationDate().getTime()) )
			.put(IJsonNames.REG_CONTABLE_DATE, vat.getRegContableDate() == null? null : AonNumberUtils.toString(vat.getRegContableDate().getTime()) )
			.putOpt(IJsonNames.DETAIL_DESCRIPTION, vat.getDetailDescription() )
			.put(IJsonNames.INSIDE_PERIOD, vat.isInsidePeriod() )
			.put(IJsonNames.INVOICE_TYPE, vat.getInvoiceType()==null?null:vat.getInvoiceType().ordinal() )
			.put(IJsonNames.RECTIFICATION_TYPE, vat.getRectificationType()==null?null:vat.getRectificationType().ordinal() )
			.put(IJsonNames.RECTIFICATION_INVOICE, vat.getRectificationInvoice() )
			.put(IJsonNames.SERVICE, vat.isService() )
			.put(IJsonNames.TRANSACTION, vat.getTransaction()==null?null:vat.getTransaction().ordinal() )
			.put(IJsonNames.INVESTMENT, vat.isInvestment() )
			.put(IJsonNames.VAT_ACCRUAL_REGIME, vat.isVatAccrualRegime() )
			.put(IJsonNames.VAT_DEDUCTION_TYPE, vat.getVatDeductionType()==null?null:vat.getVatDeductionType().ordinal() )
			.put(IJsonNames.FARMER_REGIME, vat.isFarmerRegime() )
			.put(IJsonNames.PREPAYMENT, vat.isPrepayment() )
			.put(IJsonNames.VAT_IMPORTATION, vat.isVatImportation() )
			.put(IJsonNames.DUA_LINKED, vat.hasDuaLinked() )
			.put(IJsonNames.BASE, vat.getBase() )
			.put(IJsonNames.PERCENTAGE, vat.getPercentage() )
			.put(IJsonNames.QUOTA, vat.getQuota() )
			.put(IJsonNames.INVEST_ASSET, vat.getInvestAsset() )
			.put(IJsonNames.DEDUCTIBLE_PERCENT, vat.getDeductiblePercent() )
			.put(IJsonNames.DEDUCTIBLE_QUOTA, vat.getDeductibleQuota() )
			.put(IJsonNames.SURCHARGE, vat.isSurcharge() )
			.put(IJsonNames.SURCHARGE_PERCENT, vat.getSurchargePercent() )
			.put(IJsonNames.SURCHARGEQUOTA, vat.getSurchargeQuota() )
			.put(IJsonNames.PRORRATED, vat.isProrrated() )
			.put(IJsonNames.PRORRATE_PERCENT, vat.getProrratePercent() )
			.put(IJsonNames.PRORRATE_QUOTA, vat.getProrrateQuota() )
			.putOpt(IJsonNames.SII_STATUS, vat.getSiiStatus() )
			.putOpt(IJsonNames.AMORTIZATION_DESCRIPTION, vat.getAmortizationDescription() )
			.putOpt(IJsonNames.AMORTIZATION_PERCENT, vat.getAmortizationPercentage() )
			.put(IJsonNames.AMORTIZATION_INITIAL_DATE, vat.getAmortizationInitialDate() == null ? null : AonNumberUtils.toString(vat.getAmortizationInitialDate().getTime()))
			.put(IJsonNames.FINANCE_PENDING, vat.isFinancePending() )
			.put(IJsonNames.AMOUNT_347, vat.getAmount347() )
			.put(IJsonNames.RETENTION, vat.hasRetention() )
			;
	}

}
