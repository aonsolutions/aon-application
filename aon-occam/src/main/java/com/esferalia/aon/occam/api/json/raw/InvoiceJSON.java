package com.esferalia.aon.occam.api.json.raw;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.AccountJSON;
import com.esferalia.aon.occam.api.json.EnterpriseActivityJSON;
import com.esferalia.aon.occam.api.json.ScopeJSON;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.invoice.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceWithholding;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceJSON {
	
	private InvoiceJSON() {
	}
	
	public static Invoice fromJSON(String json) {
		if (AonStringUtils.isBlank(json)) return null;
		return fromJSON(new JSONObject(json));
	}
	
	public static Invoice fromJSON(JSONObject json) {
		if (JsonUtils.isEmpty(json)) return null;
		Invoice invoice =  new Invoice()
			.setSelected( JsonUtils.getBoolean(json, IJsonNames.SELECTED))
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setActivity(EnterpriseActivityJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.ACTIVITY)))
			.setInvestAsset(InvestAssetJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.INVEST_ASSET)))
			.setProject(JsonUtils.getInteger(json, IJsonNames.PROJECT))
			.setType(InvoiceType.safeValueOf(JsonUtils.getString(json, IJsonNames.TYPE)))
			.setSeries(JsonUtils.getString(json, IJsonNames.SERIES))
			.setNumber(JsonUtils.getint(json, IJsonNames.NUMBER))
			.setReferenceCode(JsonUtils.getString(json, IJsonNames.REFERENCE_CODE))
			.setIssueDate(JsonUtils.getDate(json, IJsonNames.ISSUE_DATE))
			.setTaxDate(JsonUtils.getDate(json, IJsonNames.TAX_DATE))
			.setRectificationType(RectificationType.safeValueOf(JsonUtils.getString(json, IJsonNames.RECTIFICATION_TYPE)))
			.setRectificationInvoice(InvoiceMinJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.RECTIFICATION_INVOICE)))
			.setConfidential(JsonUtils.getboolean(json, IJsonNames.CONFIDENTIAL))
			.setRegistry(JsonUtils.getInteger(json, IJsonNames.REGISTRY))
			.setRegistryDocument(JsonUtils.getString(json, IJsonNames.REGISTRY_DOCUMENT))
			.setRegistryDocumentCountry(Country.safeValueOf(JsonUtils.getString(json, IJsonNames.REGISTRY_DOCUMENT_COUNTRY)))
			.setRegistryDocumentType(DocumentType.safeValueOf(JsonUtils.getString(json, IJsonNames.REGISTRY_DOCUMENT_TYPE)))
			.setRegistryName(JsonUtils.getString(json, IJsonNames.REGISTRY_NAME))
			.setRegistryAddress(RegistryAddressJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.REGISTRY_ADDRESS)))
			.setRegistryAccount(AccountJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.REGISTRY_ACCOUNT)))
			.setScope(ScopeJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.SCOPE)))
			.setTransaction(InvoiceTransactionType.safeValueOf(json.optString(IJsonNames.TRANSACTION)))
			.setRecorded(JsonUtils.getboolean(json, IJsonNames.RECORDED))
			.setSurcharge(JsonUtils.getboolean(json, IJsonNames.SURCHARGE))
			.setWithholding(JsonUtils.getboolean(json, IJsonNames.WITHHOLDING))
			.setWithholdingFarmer(JsonUtils.getboolean(json, IJsonNames.WITHHOLDING_FARMER))
			.setVatAccrualPayment(JsonUtils.getboolean(json, IJsonNames.VAT_ACCRUAL_PAYMENT))
			.setInvestment(JsonUtils.getboolean(json, IJsonNames.INVESTMENT))
			.setService(JsonUtils.getboolean(json, IJsonNames.SERVICE))
			.setSigned(JsonUtils.getboolean(json, IJsonNames.SIGNED))
			.setAnnulled(JsonUtils.getboolean(json, IJsonNames.ANNULLED))
			.setTaxableBase(JsonUtils.getdouble(json, IJsonNames.TAXABLE_BASE))
			.setVatQuota(JsonUtils.getdouble(json, IJsonNames.VAT_QUOTA))
			.setRetentionQuota(JsonUtils.getdouble(json, IJsonNames.RETENTION_QUOTA))
			.setTotal(JsonUtils.getdouble(json, IJsonNames.TOTAL))
			.setSeller(SellerJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.SELLER)))
			.setComments(JsonUtils.getString(json, IJsonNames.COMMENTS))
			.setRemarks(JsonUtils.getString(json, IJsonNames.REMARKS))
			.setCreationUser(JsonUtils.getString(json, IJsonNames.CREATION_USER))
			.setCreationDate(JsonUtils.getDate(json, IJsonNames.CREATION_DATE))
			.setModificationUser(JsonUtils.getString(json, IJsonNames.MODIFICATION_USER))
			.setModificationDate(JsonUtils.getDate(json, IJsonNames.MODIFICATION_DATE))
			;
		if (invoice.isWithholding()) {
			JsonUtils.stream(json, IJsonNames.DETAILS)
				.map( detailJson -> InvoiceDetailJSON.fromJSON(invoice, detailJson))
				.filter(id -> !id.isPrepayment())
				.findFirst()
				.flatMap( id -> id.getWithholding())
				.ifPresent(it -> 
					invoice.setWithholding( 
						new InvoiceWithholding()
							.setWithholdingType(it.getWithholdingType()) 
							.setPercentage(it.getPercentage())
							.setAccount(it.getWithholdingAccount().orElse(null))
					)
				);
		}
		JsonUtils.stream(json, IJsonNames.DETAILS)
			.map( detailJson -> InvoiceDetailJSON.fromJSON(invoice, detailJson))
			.forEach( invoice::addDetail );
		return invoice;
	}
	
	public static JSONObject toJSON(Invoice invoice) {
		return toJSON(invoice, false);
	}
	
	public static JSONObject toJSON(Invoice invoice, boolean withComments) {
		if (invoice == null) return null;
		JSONObject json = new JSONObject()
			.put(IJsonNames.SELECTED, invoice.isSelected())
			.put(IJsonNames.ID, invoice.getId())
			.put(IJsonNames.DOMAIN, invoice.getDomain())
			.put(IJsonNames.ACTIVITY, EnterpriseActivityJSON.toJSON(invoice.getActivity().orElse(null)))
			.put(IJsonNames.INVEST_ASSET, InvestAssetJSON.toJSON( invoice.getInvestAsset().orElse(null)))
			.put(IJsonNames.PROJECT, invoice.getProject())
			.put(IJsonNames.TYPE, invoice.getType())
			.put(IJsonNames.SERIES, invoice.getSeries())
			.put(IJsonNames.NUMBER, invoice.getNumber())
			.put(IJsonNames.REFERENCE_CODE, invoice.getReferenceCode())
			.put(IJsonNames.ISSUE_DATE, AonDateUtils.simpleFormat(invoice.getIssueDate()))
			.put(IJsonNames.TAX_DATE, AonDateUtils.simpleFormat(invoice.getTaxDate()))
			.put(IJsonNames.RECTIFICATION_TYPE, RectificationType.safeValueOf(invoice.getRectificationType()))
			.put(IJsonNames.RECTIFICATION_INVOICE, InvoiceMinJSON.toJSON( invoice.getRectificationInvoice().orElse(null))) 
			.put(IJsonNames.CONFIDENTIAL, invoice.isConfidential())
			.put(IJsonNames.REGISTRY, invoice.getRegistry())
			.put(IJsonNames.REGISTRY_DOCUMENT, invoice.getRegistryDocument())
			.put(IJsonNames.REGISTRY_DOCUMENT_COUNTRY, invoice.getRegistryDocumentCountry())
			.put(IJsonNames.REGISTRY_DOCUMENT_TYPE, invoice.getRegistryDocumentType())
			.put(IJsonNames.REGISTRY_NAME, invoice.getRegistryName())
			.put(IJsonNames.REGISTRY_ADDRESS, RegistryAddressJSON.toJSON( invoice.getRegistryAddress().orElse(null))) 
			.put(IJsonNames.REGISTRY_ACCOUNT, AccountJSON.toJSON( invoice.getRegistryAccount().orElse(null)))
			.put(IJsonNames.SCOPE, ScopeJSON.toJSON( invoice.getScope() ))
			.put(IJsonNames.TRANSACTION, InvoiceTransactionType.safeValueOf( invoice.getTransaction()))
			.put(IJsonNames.RECORDED, invoice.isRecorded())
			.put(IJsonNames.SURCHARGE, invoice.isSurcharge())
			.put(IJsonNames.WITHHOLDING, invoice.isWithholding()) 
			.put(IJsonNames.WITHHOLDING_FARMER, invoice.isWithholdingFarmer()) 
			.put(IJsonNames.VAT_ACCRUAL_PAYMENT, invoice.isVatAccrualPayment()) 
			.put(IJsonNames.INVESTMENT, invoice.isInvestment())
			.put(IJsonNames.SERVICE, invoice.isService()) 
			.put(IJsonNames.SIGNED, invoice.isSigned()) 
			.put(IJsonNames.ANNULLED, invoice.isAnnulled()) 
			.put(IJsonNames.TAXABLE_BASE, invoice.getTaxableBase())
			.put(IJsonNames.VAT_QUOTA, invoice.getVatQuota())
			.put(IJsonNames.RETENTION_QUOTA, invoice.getRetentionQuota())
			.put(IJsonNames.TOTAL, invoice.getTotal())
			.put(IJsonNames.SELLER, SellerJSON.toJSON( invoice.getSeller().orElse(null)))
			.put(IJsonNames.CREATION_USER, invoice.getCreationUser())
			.put(IJsonNames.CREATION_DATE, AonDateUtils.dateTimeFormat(invoice.getCreationDate()))
			.put(IJsonNames.MODIFICATION_USER, invoice.getModificationUser())
			.put(IJsonNames.MODIFICATION_DATE, AonDateUtils.dateTimeFormat(invoice.getModificationDate()))
			.put(IJsonNames.DETAILS, InvoiceDetailJSON.toJSON( invoice.getDetails() ))
		;
		if (withComments) {
			json.put(IJsonNames.COMMENTS, invoice.getComments())
				.put(IJsonNames.REMARKS, invoice.getRemarks());
		}
		return json; 		
	}
}
/*
private TaxBreakdown taxBreakdown;

private List<Finance> finances;
private InvoiceFiscal fiscal;
private InvoiceInfo invoiceInfo;
private Attach attach;
private List<InvoiceError> messages;

private Integer rawdocId;
*/