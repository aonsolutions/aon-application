package com.esferalia.aon.occam.api.json.invoice;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.AccountJSON;
import com.esferalia.aon.occam.api.json.EnterpriseActivityJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.ScopeJSON;
import com.esferalia.aon.occam.api.json.doc.InvoiceDocJSON;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON.InvoiceJSONVersion;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFiscal;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.VATTaxRegime;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorContext;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorKey;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonObjectUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

class InvoiceJSONV2 {
	
	private InvoiceJSONV2() {
	
	}
	
	// ***********************************************************
	// ************************************************ [FROM] ***
	// ***********************************************************
	
	static Optional<Invoice> from(JSONObject json) {
		return from(json, Invoice::new);
	}
	static Optional<Invoice> from(JSONObject json, Supplier<Invoice> sup) {
		if (JsonUtils.isEmpty(json)) return Optional.empty();
		Invoice inv = sup.get()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setActivity(EnterpriseActivityJSON.from(JsonUtils.getJSONObject(json, IJsonNames.ACTIVITY)).orElse(null))
			.setInvestAsset(JsonUtils.getInteger(json, IJsonNames.INVEST_ASSET))
			.setProject(JsonUtils.getInteger(json, IJsonNames.PROJECT))
			.setSeries(JsonUtils.getString(json, IJsonNames.SERIES))
			.setNumber(JsonUtils.getInt(json, IJsonNames.NUMBER))
			.setReferenceCode(JsonUtils.getString(json, IJsonNames.REFERENCE_CODE))
			.setIssueDate(JsonUtils.getDate(json, IJsonNames.ISSUE_DATE)) 
			.setTaxDate(JsonUtils.getDate(json, IJsonNames.TAX_DATE))
			.setConfidential(JsonUtils.getboolean(json,IJsonNames.CONFIDENTIAL))
			.setRegistryAddress(JsonUtils.getInteger(json, IJsonNames.REGISTRY_ADDRESS))
			.setAddress(getRegistryAddress(json).orElse(null))
			.setRectificationType(RectificationType.safeValueOf(JsonUtils.getString(json,IJsonNames.RECTIFICATION_TYPE)))
			.setRectificationInvoice(JsonUtils.getInteger(json, IJsonNames.RECTIFICATION_INVOICE_ID))
			.setRectificationInvoiceSeries(JsonUtils.getString(json, IJsonNames.RECTIFICATION_INVOICE_SERIES))
			.setRectificationInvoiceNumber(JsonUtils.getInteger(json, IJsonNames.RECTIFICATION_INVOICE_NUMBER))
			.setRectificationInvoiceReference(JsonUtils.getString(json, IJsonNames.RECTIFICATION_INVOICE_REFERENCE))
			.setRectificationInvoiceDate(JsonUtils.getDate(json, IJsonNames.RECTIFICATION_INVOICE_DATE))
			.setRegistry(JsonUtils.getInteger(json, IJsonNames.REGISTRY))
			.setRegistryDocument(JsonUtils.getString(json, IJsonNames.REGISTRY_DOCUMENT))
			.setRegistryDocumentType (DocumentType.safeValueOf(JsonUtils.getString(json,IJsonNames.REGISTRY_DOCUMENT_TYPE)))
			.setRegistryDocumentCountry(Country.safeValueOf(JsonUtils.getString(json,IJsonNames.REGISTRY_DOCUMENT_COUNTRY)))
			.setRegistryName(JsonUtils.getString(json, IJsonNames.REGISTRY_NAME))
			.setRegistryAccount(AccountJSON.from(JsonUtils.getJSONObject(json, IJsonNames.REGISTRY_ACCOUNT)).orElse(null))
			.setScope(ScopeJSON.from(JsonUtils.getJSONObject(json, IJsonNames.SCOPE)).orElse(null))
			.setType(InvoiceType.safeValueOf(JsonUtils.getString(json,IJsonNames.INVOICE_TYPE)))
			.setTransaction(InvoiceTransactionType.safeValueOf(JsonUtils.getString(json,IJsonNames.TRANSACTION)))
			.setRecorded(JsonUtils.getboolean(json,IJsonNames.RECORDED))
			.setSurcharge(JsonUtils.getboolean(json,IJsonNames.SURCHARGE))
			.setWithholding(JsonUtils.getboolean(json,IJsonNames.WITHHOLDING))
			.setWithholdingFarmer(JsonUtils.getboolean(json,IJsonNames.WITHHOLDING_FARMER))
			.setVatAccrualPayment(JsonUtils.getboolean(json,IJsonNames.VAT_ACCRUAL_PAYMENT))
			.setInvestment(JsonUtils.getboolean(json,IJsonNames.INVESTMENT))
			.setService(JsonUtils.getboolean(json,IJsonNames.SERVICE))
			.setAdvance(JsonUtils.getboolean(json,IJsonNames.ADVANCE))
			.setSigned(JsonUtils.getboolean(json,IJsonNames.SIGNED))
			.setAnnulled(JsonUtils.getboolean(json,IJsonNames.ANNULLED))
			.setTaxableBase(JsonUtils.getdouble(json, IJsonNames.TAXABLE_BASE))
			.setVatQuota(JsonUtils.getdouble(json, IJsonNames.VAT_QUOTA))
			.setRetentionQuota(JsonUtils.getdouble(json, IJsonNames.RETENTION_QUOTA))
			.setTotal(JsonUtils.getdouble(json, IJsonNames.TOTAL))
			.setPosShift(JsonUtils.getInteger(json, IJsonNames.POS_SHIFT))
			.setSeller(JsonUtils.getInteger(json, IJsonNames.SELLER))
			.setSellerName(JsonUtils.getString(json, IJsonNames.SELLER_NAME))
			.setComments(JsonUtils.getString(json, IJsonNames.COMMENTS))
			.setRemarks(JsonUtils.getString(json, IJsonNames.REMARKS))
			.setRecordable(JsonUtils.getboolean(json,IJsonNames.RECORDABLE))
			.setSelected(JsonUtils.getboolean(json,IJsonNames.SELECTED))
			.setSkipAlcatrazValidationAllowed(JsonUtils.getboolean(json,IJsonNames.SKIP_ALCATRAZ_VALIDATION_ALLOWED))
			.setSiiStatus(JsonUtils.getString(json,IJsonNames.SII_STATUS))
			.setTediCategory(JsonUtils.getString(json, IJsonNames.CATEGORY))
			.setDoc(InvoiceDocJSON.from(JsonUtils.getJSONObject(json, IJsonNames.INVOICE_DOC)).orElse(null))
			.setFiscal( getInvoiceFiscal( JsonUtils.getJSONObject(json, IJsonNames.INVOICE_FISCAL) ).orElse(null))
			.addCommunicationInfo(getCommunicationInfo( JsonUtils.getJSONObject(json, IJsonNames.COMMUNICATION_INFO) ).orElse(null))
			.setCreationUser(JsonUtils.getString(json,IJsonNames.CREATION_USER))		
			.setCreationDate(JsonUtils.getDateTime(json,IJsonNames.CREATION_DATE))
			.setModificationUser(JsonUtils.getString(json,IJsonNames.MODIFICATION_USER))
			.setModificationDate(JsonUtils.getDateTime(json,IJsonNames.MODIFICATION_DATE))
		;
		addDetails(inv, json);
		addBreakdown(inv, json);
		addFinances(inv, json);
		addMessages(inv, json);
		return Optional.of(inv);
	}

	// ---------------------------- [FROM INVOICE ADDRESS] ----------------------------
	
	
	public static Optional<RegistryAddress> getRegistryAddress(JSONObject invoiceJson) {
		if (JsonUtils.isEmpty(invoiceJson)) return Optional.empty();
		JSONObject json = JsonUtils.getJSONObject(invoiceJson, IJsonNames.ADDRESS);
		if (JsonUtils.isEmpty(json)) return Optional.empty();
		return Optional.of( new RegistryAddress()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setRegistry(JsonUtils.getInteger(json, IJsonNames.REGISTRY))
			.setMain(JsonUtils.getboolean(json, IJsonNames.MAIN))
			.setStreetType(StreetType.safeName(JsonUtils.getString(json, IJsonNames.STREET_TYPE)).orElse(null))
			.setAddress(JsonUtils.getString(json, IJsonNames.ADDRESS))
			.setNumber(JsonUtils.getString(json, IJsonNames.NUMBER))
			.setAddress2(JsonUtils.getString(json, IJsonNames.ADDRESS2))
			.setAddress3(JsonUtils.getString(json, IJsonNames.ADDRESS3))
			.setCity(JsonUtils.getString(json, IJsonNames.CITY))
			.setProvince(JsonUtils.getString(json, IJsonNames.PROVINCE))
			.setCountry(Country.safeName(JsonUtils.getString(json, IJsonNames.COUNTRY)).orElse(null))
			.setZip(JsonUtils.getString(json, IJsonNames.ZIP))
			.setDirty(JsonUtils.getboolean(json, IJsonNames.DIRTY))
			.setRemoved(JsonUtils.getboolean(json, IJsonNames.REMOVED))
			.setGlobal(JsonUtils.getboolean(json, IJsonNames.GLOBAL))
		);
	}
	
	// ---------------------------- [FROM INVOICE DETAIL] ----------------------------
	
	private static Invoice addDetails(Invoice inv, JSONObject invoiceJson) {
		if (JsonUtils.isEmpty(invoiceJson)) return inv;
		JSONArray details = JsonUtils.getJSONArray(invoiceJson, IJsonNames.DETAILS);
		if (JsonUtils.isEmpty(details)) return inv;
		AonCollectionUtils.stream(details.length())
			.mapToObj(details::getJSONObject)
    		.filter(JsonUtils::isNotEmpty )
    		.map(j -> {
    			InvoiceDetail det = new InvoiceDetail()
	    			.setId(JsonUtils.getInteger(j, IJsonNames.ID))
	    			.setInvestAsset(JsonUtils.getInteger(j, IJsonNames.INVEST_ASSET))
	    			.setProject(JsonUtils.getInteger(j, IJsonNames.PROJECT))
	    			.setItem(getItem(j))
	    			.setLine(JsonUtils.getshort(j,IJsonNames.LINE))
	    			.setDescription(JsonUtils.getString(j,IJsonNames.DESCRIPTION))
	    			.setQuantity(JsonUtils.getdouble(j, IJsonNames.QUANTITY))
	    			.setPrice(JsonUtils.getdouble(j, IJsonNames.PRICE))
	    			.setDiscount(JsonUtils.getdouble(j, IJsonNames.DISCOUNT))
	    			.setTaxableBase(JsonUtils.getdouble(j, IJsonNames.TAXABLE_BASE))
	    			.setPrepayment(JsonUtils.getboolean(j, IJsonNames.PREPAYMENT))
	    			.setWarehouse(JsonUtils.getInteger(j,IJsonNames.WAREHOUSE))
	    			.setWorkplace( getWorkplace(j) )
	    			.setAccountId(JsonUtils.getInteger(j, IJsonNames.ACCOUNT_ID))
	    			.setAccountCode(JsonUtils.getString(j, IJsonNames.ACCOUNT_CODE))
	    			.setAccountDescription(JsonUtils.getString(j, IJsonNames.ACCOUNT_DESCRIPTION))
	    			.setSource(InvoiceSource.safeValueOf( JsonUtils.getString(j, IJsonNames.SOURCE) ))
	    			.setSourceId(JsonUtils.getInteger(j, IJsonNames.SOURCE_ID))
    			;
    			return addTaxes(det, j);
			})
    		.forEach( inv::addDetail )
   		;
		return inv;
	}
	
	private static Workplace getWorkplace(JSONObject json) {
		Integer wp = JsonUtils.getInteger(json,IJsonNames.WORKPLACE);
		if (wp == null) return null;
		return new Workplace().setId(wp);
	}

	private static Item getItem(JSONObject json) {
		Integer it = JsonUtils.getInteger(json,IJsonNames.ITEM);
		if (it == null) return null;
		return new Item().setId(it);
	}
	// ---------------------------- [FROM INVOICE TAX] ----------------------------
	
	private static InvoiceDetail addTaxes(InvoiceDetail detail, JSONObject json) {
		JSONArray taxes = JsonUtils.getJSONArray(json, IJsonNames.INVOICE_TAXES);
		AonCollectionUtils.stream(taxes.length())
			.mapToObj(taxes::getJSONObject)
			.filter(JsonUtils::isNotEmpty )
    		.map(o -> new InvoiceTax()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setTaxType(TaxType.safeValueOf( JsonUtils.getInteger(json, IJsonNames.TAX_TYPE)))
				.setBase(JsonUtils.getdouble(json, IJsonNames.TAXABLE_BASE))
				.setPercentage(JsonUtils.getdouble(json, IJsonNames.PERCENTAGE))
				.setQuota(JsonUtils.getdouble(json, IJsonNames.QUOTA))
				.setSurcharge(JsonUtils.getdouble(json, IJsonNames.SURCHARGE))
				.setSurchargeQuota(JsonUtils.getdouble(json, IJsonNames.SURCHARGE_QUOTA))
				.setDeductiblePercent(JsonUtils.getdouble(json, IJsonNames.DEDUCTIBLE_PERCENT))
				.setDeductibleQuota(JsonUtils.getdouble(json, IJsonNames.DEDUCTIBLE_QUOTA))
				.setVatDeductionType(VatDeductionType.safeValue( JsonUtils.getString(json, IJsonNames.VAT_DEDUCTION_TYPE)))
				.setWithholdingType(WithholdingType.safeValue( JsonUtils.getString(json, IJsonNames.WITHHOLDING_TYPE))))
    		.forEach( detail::addTax );
		return detail;
	}
	
	// ---------------------------- [FROM INVOICE FISCAL] ----------------------------
	
	private static Optional<InvoiceFiscal> getInvoiceFiscal(JSONObject json) {
		if (JsonUtils.isEmpty(json)) return Optional.empty();
		InvoiceFiscal ifi = new InvoiceFiscal()
			.setIssueDate(JsonUtils.getDate(json, IJsonNames.ISSUE_DATE)) 
			.setTaxDate(JsonUtils.getDate(json, IJsonNames.TAX_DATE)) 
			.setExpDate(JsonUtils.getDate(json, IJsonNames.EXP_DATE))
		;
		JSONArray vrs = JsonUtils.getJSONArray(json, IJsonNames.VAT_REGIMES);
		AonCollectionUtils.stream(vrs.length())
			.mapToObj(vrs::getString)
			.filter(AonStringUtils::isNotBlank )
			.map(VATTaxRegime::safeValueOf)
			.filter(Objects::nonNull )
			.forEach( vr -> ifi.setVatRegime(vr, true))
		;
		return Optional.of( ifi );
	}
	
	// ---------------------------- [FROM INVOICE INFO] ----------------------------
	private static Optional<Map<InvoiceCommunicationType, InvoiceInfo>> getCommunicationInfo(JSONObject json) {
		if (JsonUtils.isEmpty(json)) return Optional.empty();
		HashMap<InvoiceCommunicationType, InvoiceInfo> map = new HashMap<>();
		for(Entry<String, Object> e : json.toMap().entrySet()) {
			InvoiceCommunicationType type = InvoiceCommunicationType.safeValueOf(e.getKey());
			if (type != null) {
				JSONObject j = (JSONObject)e.getValue();
				getInvoiceInfo(j).ifPresent( info -> map.put(type, info) );
			}
		}
		return map.isEmpty() ? Optional.empty() : Optional.of(map);
	}
	
	private static Optional<InvoiceInfo> getInvoiceInfo(JSONObject json) {
		if (JsonUtils.isEmpty(json)) return Optional.empty();
		return Optional.of( new InvoiceInfo()
			.setType(InvoiceCommunicationType.safeValueOf(JsonUtils.getString(json,IJsonNames.COMMUNICATION_TYPE)))
			.setStatus(InvoiceCommunicationStatus.safeValueOf(JsonUtils.getString(json,IJsonNames.COMMUNICATION_STATUS)))
			.setCreationUser(JsonUtils.getString(json,IJsonNames.CREATION_USER))		
			.setCreationDate(JsonUtils.getDateTime(json,IJsonNames.CREATION_DATE))
			.setModificationUser(JsonUtils.getString(json,IJsonNames.MODIFICATION_USER))
			.setModificationDate(JsonUtils.getDateTime(json,IJsonNames.MODIFICATION_DATE))
			.setCheckUrl(JsonUtils.getString(json,IJsonNames.CHECK_URL))
		);
	}

	// ---------------------------- [FROM INVOICE FINANCES] ----------------------------
	private static Invoice addFinances(Invoice inv, JSONObject invoiceJson) {
		if (JsonUtils.isEmpty(invoiceJson)) return inv;
		JSONArray finances = JsonUtils.getJSONArray(invoiceJson, IJsonNames.FINANCES);
		if (JsonUtils.isEmpty(finances)) return inv;
		AonCollectionUtils.stream(finances.length())
			.mapToObj(finances::getJSONObject )
			.filter(JsonUtils::isNotEmpty )
			.map(j -> new Finance()
				.setId(JsonUtils.getInteger(j, IJsonNames.ID))
				.setDomain(JsonUtils.getInteger(j, IJsonNames.DOMAIN))
				.setPayMethod(JsonUtils.getInteger(j, IJsonNames.PAYMETHOD))
				.setPayMethodName(JsonUtils.getString(j, IJsonNames.PAYMETHOD_NAME))
				.setPayMethodType(PayMethodType.safeValueOf(JsonUtils.getString(j, IJsonNames.PAYMETHOD_TYPE)))
				.setBankAccount(new BankAccount(JsonUtils.getString(j, IJsonNames.BANK_ACCOUNT)))
				.setAmount(JsonUtils.getdouble(j, IJsonNames.AMOUNT))
				.setDueDate(JsonUtils.getDate(j, IJsonNames.DUE_DATE)))
			.forEach( inv::addFinance )
		;
		return inv;
	}
	
	// ---------------------------- [FROM INVOICE MESSAGES] ----------------------------
	private static Invoice addMessages(Invoice inv, JSONObject invoiceJson) {
		if (JsonUtils.isEmpty(invoiceJson)) return inv;
		JSONArray messages = JsonUtils.getJSONArray(invoiceJson, IJsonNames.MESSAGES);
		if (JsonUtils.isEmpty(messages)) return inv;
		AonCollectionUtils.stream(messages.length())
			.mapToObj(messages::getJSONObject )
			.filter(JsonUtils::isNotEmpty )
			.map(j -> new InvoiceError()
				.setCode(j.getString(IJsonNames.CODE))
				.setMessage(j.getString(IJsonNames.MESSAGE))
				.setLevel(InvoiceErrorLevel.safeValueOf(j.getString(IJsonNames.LEVEL)))
				.setContext(fromInvoiceErrorContextJSON(j.getJSONObject(IJsonNames.CONTEXT)).orElse(null))
			)
			.forEach( inv::addMessage )
		;
		return inv;
	}
	
	private static Optional<InvoiceErrorContext> fromInvoiceErrorContextJSON(JSONObject json) {
		if (JsonUtils.isEmpty(json) ) return Optional.empty();
		return Optional.of(new InvoiceErrorContext()
			.setLine(JsonUtils.getInt(json,IJsonNames.LINE))
			.setKey(InvoiceErrorKey.safeValueOf(JsonUtils.getString(json,IJsonNames.KEY))));
	}
	
	
	// ---------------------------- [FROM INVOICE BREAKDOWN] ----------------------------
	private static Invoice addBreakdown(Invoice inv, JSONObject invoiceJson) {
		if (JsonUtils.isEmpty(invoiceJson)) return inv;
		JSONArray breakdown = JsonUtils.getJSONArray(invoiceJson, IJsonNames.INVOICE_BREAKDOWN);
		if (JsonUtils.isEmpty(breakdown)) return inv;
		AonCollectionUtils.stream(breakdown.length())
			.mapToObj(breakdown::getJSONObject )
			.filter(JsonUtils::isNotEmpty )
			.map(j -> new InvoiceBreakdown()
				.setId(JsonUtils.getInteger(j, IJsonNames.ID))
				.setTaxType(TaxType.safeValueOf( JsonUtils.getInteger(j, IJsonNames.TAX_TYPE)))
				.setBase(JsonUtils.getdouble(j, IJsonNames.AMOUNT))
				.setPercentage(JsonUtils.getdouble(j, IJsonNames.PERCENTAGE))
				.setQuota(JsonUtils.getdouble(j, IJsonNames.QUOTA))
				.setSurcharge(JsonUtils.getdouble(j, IJsonNames.SURCHARGE))
				.setSurchargeQuota(JsonUtils.getdouble(j, IJsonNames.SURCHARGE_QUOTA))
				.setDeductibleQuota(JsonUtils.getdouble(j, IJsonNames.DEDUCTIBLE_QUOTA))
				.setVatDeductionType(VatDeductionType.safeValue( JsonUtils.getString(j, IJsonNames.VAT_DEDUCTION_TYPE)))
				.setWithholdingType(WithholdingType.safeValue( JsonUtils.getString(j, IJsonNames.WITHHOLDING_TYPE)))
				)
			.forEach( inv::addBreakdown )
		;
		return inv;
	}
	
	// *********************************************************
	// ************************************************ [TO] ***
	// *********************************************************

	static Optional<JSONObject> to(Invoice inv) {
		return to(inv, JSONObject::new);
	}
	static Optional<JSONObject> to(Invoice inv, Supplier<JSONObject> sup) {
		if (inv == null) return Optional.empty();
		JSONObject json = sup.get()
			.put(IJsonNames.VERSION, InvoiceJSONVersion.V2.name())
			.put(IJsonNames.ID, inv.getId())
			.put(IJsonNames.DOMAIN, inv.getDomain())
			.put(IJsonNames.ACTIVITY, EnterpriseActivityJSON.to(inv.optActivity()).orElse(null))
			.put(IJsonNames.INVEST_ASSET, inv.getInvestAsset())
			.put(IJsonNames.PROJECT, inv.getProject())
			.put(IJsonNames.SERIES, inv.getSeries())
			.put(IJsonNames.NUMBER, inv.getNumber())
			.put(IJsonNames.REFERENCE_CODE, inv.getNumber())
			.put(IJsonNames.ISSUE_DATE, JsonUtils.getDateJSON(inv.getIssueDate()) )
			.put(IJsonNames.TAX_DATE, JsonUtils.getDateJSON(inv.getTaxDate()) )
			.put(IJsonNames.CONFIDENTIAL, inv.isConfidential() )
			.put(IJsonNames.REGISTRY_ADDRESS, inv.getRegistryAddress() )
			.put(IJsonNames.ADDRESS, getRegistryAddressJSON(inv.getAddress() ).orElse(null))
			.put(IJsonNames.RECTIFICATION_TYPE, RectificationType.safeValueOf(inv.getRectificationType()))
			.put(IJsonNames.RECTIFICATION_INVOICE_ID, inv.getRectificationInvoice())
			.put(IJsonNames.RECTIFICATION_INVOICE_SERIES, inv.getRectificationInvoiceSeries())
			.put(IJsonNames.RECTIFICATION_INVOICE_NUMBER, inv.getRectificationInvoiceNumber())
			.put(IJsonNames.RECTIFICATION_INVOICE_REFERENCE, inv.getRectificationInvoiceReference())
			.put(IJsonNames.RECTIFICATION_INVOICE_DATE, JsonUtils.getDateJSON(inv.getRectificationInvoiceDate()))
			.put(IJsonNames.REGISTRY, inv.getRegistry())
			.put(IJsonNames.REGISTRY_DOCUMENT, inv.getRegistryDocument())
			.put(IJsonNames.REGISTRY_DOCUMENT_TYPE, DocumentType.name(inv.getRegistryDocumentType()))
			.put(IJsonNames.REGISTRY_DOCUMENT_COUNTRY, Country.name(inv.getRegistryDocumentCountry()))
			.put(IJsonNames.REGISTRY_NAME, inv.getRegistryName())
			.put(IJsonNames.REGISTRY_ACCOUNT, AccountJSON.to(inv.getRegistryAccount()).orElse(null))
			.put(IJsonNames.SCOPE, ScopeJSON.to(inv.getScope()).orElse(null))
			.put(IJsonNames.INVOICE_TYPE, InvoiceType.name(inv.getType()))
			.put(IJsonNames.TRANSACTION, InvoiceTransactionType.name(inv.getTransaction()))
			.put(IJsonNames.RECORDED, inv.isRecorded())
			.put(IJsonNames.SURCHARGE, inv.isSurcharge())
			.put(IJsonNames.WITHHOLDING, inv.isWithholding())
			.put(IJsonNames.WITHHOLDING_FARMER, inv.isWithholdingFarmer())
			.put(IJsonNames.VAT_ACCRUAL_PAYMENT, inv.isVatAccrualPayment())
			.put(IJsonNames.INVESTMENT, inv.isInvestment())
			.put(IJsonNames.SERVICE, inv.isService())
			.put(IJsonNames.ADVANCE, inv.isAdvance())
			.put(IJsonNames.SIGNED, inv.isSigned())
			.put(IJsonNames.ANNULLED, inv.isAnnulled())
			.put(IJsonNames.TAXABLE_BASE, inv.getTaxableBase())
			.put(IJsonNames.VAT_QUOTA, inv.getVatQuota())
			.put(IJsonNames.RETENTION_QUOTA, inv.getRetentionQuota())
			.put(IJsonNames.TOTAL, inv.getTotal())
			.put(IJsonNames.POS_SHIFT, inv.getPosShift())
			.put(IJsonNames.SELLER, inv.getSeller())
			.put(IJsonNames.SELLER_NAME, inv.getSellerName())
			.put(IJsonNames.COMMENTS, inv.getComments())
			.put(IJsonNames.REMARKS, inv.getRemarks())
			.put(IJsonNames.RECORDABLE, inv.isRecordable())
			.put(IJsonNames.SELECTED, inv.isSelected())
			.put(IJsonNames.SKIP_ALCATRAZ_VALIDATION_ALLOWED, inv.isSkipAlcatrazValidation())
			.put(IJsonNames.SII_STATUS, inv.getSiiStatus())
			.put(IJsonNames.CATEGORY, inv.getTediCategory())
			.put(IJsonNames.INVOICE_DOC, InvoiceDocJSON.to(inv.getDoc()).orElse(null) )
			.put(IJsonNames.INVOICE_FISCAL, getInvoiceFiscalJSON(inv.getFiscal()).orElse(null))
			.put(IJsonNames.COMMUNICATION_INFO, getCommunicationInfoJSON(inv.getCommunicationInfo()).orElse(null))
			.put(IJsonNames.CREATION_USER, inv.getCreationUser())
			.put(IJsonNames.CREATION_DATE, JsonUtils.getDateTimeJSON(inv.getCreationDate()))
			.put(IJsonNames.MODIFICATION_USER, inv.getModificationUser())
			.put(IJsonNames.MODIFICATION_DATE, JsonUtils.getDateTimeJSON(inv.getModificationDate()))
			
			// ---------------------------- [INVOICE DETAILS] ----------------------------
			.put( IJsonNames.DETAILS, JsonUtils.nullIfEmpty(
				inv.detailStream()
		    		.map(det -> new JSONObject()
						.put(IJsonNames.ID, det.getId())
						.put(IJsonNames.INVEST_ASSET, det.getInvestAsset())
						.put(IJsonNames.PROJECT, det.getProject())
						.put(IJsonNames.ITEM, AonObjectUtils.ifNotNullGet(det.getItem(), Item::getId))
						.put(IJsonNames.LINE, det.getLine()) 
						.put(IJsonNames.DESCRIPTION, det.getDescription())
						.put(IJsonNames.QUANTITY, det.getQuantity())
						.put(IJsonNames.PRICE, det.getPrice())
						.put(IJsonNames.DISCOUNT, det.getDiscount())
						.put(IJsonNames.TAXABLE_BASE, det.getTaxableBase())
						.put(IJsonNames.PREPAYMENT, det.isPrepayment())
						.put(IJsonNames.WAREHOUSE, det.getWarehouse())
						.put(IJsonNames.WORKPLACE, AonObjectUtils.ifNotNullGet(det.getWorkplace(), Workplace::getId))
						.put(IJsonNames.ACCOUNT_ID, det.getAccountId())
						.put(IJsonNames.ACCOUNT_CODE, det.getAccountCode())
						.put(IJsonNames.ACCOUNT_DESCRIPTION, det.getAccountDescription())
						.put(IJsonNames.SOURCE, InvoiceSource.name(det.getSource()))
						.put(IJsonNames.SOURCE_ID, det.getSourceId())
						
			// ---------------------------- [INVOICE TAXES] ----------------------------						
						.put( IJsonNames.INVOICE_TAXES, JsonUtils.nullIfEmpty(det.taxStream()
							.map( t -> new JSONObject()
								.put(IJsonNames.ID, t.getId())
								.put(IJsonNames.TAX_TYPE, TaxType.name(t.getTaxType()))
								.put(IJsonNames.TAXABLE_BASE, t.getBase())
								.put(IJsonNames.PERCENTAGE, t.getPercentage())
								.put(IJsonNames.QUOTA, t.getQuota())
								.put(IJsonNames.SURCHARGE, t.getSurcharge())
								.put(IJsonNames.SURCHARGE_QUOTA, t.getSurchargeQuota())
								.put(IJsonNames.DEDUCTIBLE_PERCENT, t.getDeductiblePercent())
								.put(IJsonNames.DEDUCTIBLE_QUOTA, t.getDeductibleQuota())
								.put(IJsonNames.VAT_DEDUCTION_TYPE, VatDeductionType.name(t.getVatDeductionType()))
								.put(IJsonNames.WITHHOLDING_TYPE, WithholdingType.name(t.getWithholdingType()))
							)
							.collect(Collector.of(JSONArray::new,JSONArray::put,(left, right) -> left, Collector.Characteristics.UNORDERED))
						)
					)
				)
	    		.collect(Collector.of(JSONArray::new,JSONArray::put,(left, right) -> left, Collector.Characteristics.UNORDERED))
			))
			
			// ---------------------------- [INVOICE FINANCES] ----------------------------
			.put( IJsonNames.FINANCES,JsonUtils.nullIfEmpty(
				inv.financeStream()
					.map( f -> new JSONObject()
						.put(IJsonNames.ID, f.getId())
						.put(IJsonNames.DOMAIN, f.getDomain())
						.put(IJsonNames.PAYMETHOD, f.getPayMethod())
						.put(IJsonNames.PAYMETHOD_NAME, f.getPayMethodName())
						.put(IJsonNames.PAYMETHOD_NAME, PayMethodType.name(f.getPayMethodType()))
						.put(IJsonNames.BANK_ACCOUNT, AonObjectUtils.ifNotNullGet(f.getBankAccount(), BankAccount::getIban))
						.put(IJsonNames.AMOUNT, f.getAmount())
						.put(IJsonNames.DUE_DATE, JsonUtils.getDateJSON(f.getDueDate()))
					)
					.collect(Collector.of(JSONArray::new,JSONArray::put,(left, right) -> left, Collector.Characteristics.UNORDERED))
					)
				)			

			// ---------------------------- [INVOICE MESSAGES] ----------------------------
			.put( IJsonNames.MESSAGES,JsonUtils.nullIfEmpty(
				inv.messageStream()
				.map(m -> new JSONObject() 
					.put(IJsonNames.CODE, m.getCode())
					.put(IJsonNames.MESSAGE, m.getMessage())
					.put(IJsonNames.LEVEL, InvoiceErrorLevel.name(m.getLevel()))
					.put(IJsonNames.CONTEXT, toInvoiceErrorContextJSON(m.getContext()))
					
				)
				.collect(Collector.of(JSONArray::new,JSONArray::put,(left, right) -> left, Collector.Characteristics.UNORDERED))
			))			
		;
		addJSONBreakdown(inv, json);
		return Optional.of(json);
	}
	
	// ---------------------------- [TO INVOICE ADDRESS] ----------------------------
	public static Optional<JSONObject> getRegistryAddressJSON( RegistryAddress address) {
		if (address == null) return Optional.empty();
		return Optional.of(new JSONObject()
			.put(IJsonNames.ID, address.getId())
			.put(IJsonNames.DOMAIN, address.getDomain())
			.put(IJsonNames.REGISTRY, address.getRegistry())
			.put(IJsonNames.MAIN, address.isMain())
			.put(IJsonNames.ALIAS, address.getAlias())
			.put(IJsonNames.STREET_TYPE, StreetType.name(address.getStreetType()))
			.put(IJsonNames.ADDRESS, address.getAddress())
			.put(IJsonNames.NUMBER, address.getNumber() != null)
			.put(IJsonNames.ADDRESS2, address.getAddress2())
			.put(IJsonNames.ADDRESS3, address.getAddress3())
			.put(IJsonNames.CITY, address.getCity())
			.put(IJsonNames.PROVINCE, AonObjectUtils.ifNotNullGet(address.getChild(), GeoZone::getName))
			.put(IJsonNames.COUNTRY, AonObjectUtils.ifNotNullGet(address.getParent(), GeoZone::getCode))
			.put(IJsonNames.ZIP, address.getZip())
			.put(IJsonNames.DIRTY, address.isDirty())
			.put(IJsonNames.REMOVED, address.isRemoved())
			.put(IJsonNames.FULL_ADDRESS, address.getFullAddress2()));
	}
	
	// ---------------------------- [TO INVOICE FISCAL] ----------------------------
	private static Optional<JSONObject> getInvoiceFiscalJSON(InvoiceFiscal fiscal) {
		if (fiscal == null) return Optional.empty();
		return Optional.of(new JSONObject()
			.put(IJsonNames.ISSUE_DATE, JsonUtils.getDateJSON(fiscal.getIssueDate())) 
			.put(IJsonNames.TAX_DATE, JsonUtils.getDateJSON(fiscal.getTaxDate()))
			.put(IJsonNames.EXP_DATE, JsonUtils.getDateJSON(fiscal.getExpDate()))
			.put(IJsonNames.VAT_REGIMES, JsonUtils.nullIfEmpty(
					AonCollectionUtils.stream(fiscal.getVatRegimes())
						.filter(Entry::getValue )
						.map(e -> e.getKey().name() )
						.collect(Collector.of(JSONArray::new,JSONArray::put,(left, right) -> left, Collector.Characteristics.UNORDERED))
			))
		);
	}

	// ---------------------------- [TO INVOICE INFO] ----------------------------
	private static Optional<JSONObject> getCommunicationInfoJSON(Map<InvoiceCommunicationType, InvoiceInfo> enumMap) {
		if (AonCollectionUtils.isEmpty(enumMap)) return Optional.empty();
		JSONObject json = new JSONObject();
		AonCollectionUtils.stream(enumMap)
			.filter( e -> e.getKey() != null )
			.filter( e -> e.getValue() != null )
			.forEach( e -> {
				String key = InvoiceCommunicationType.name(e.getKey());
				getInvoiceInfoJSON(e.getValue())
					.ifPresent( infoJson -> json.put(key, infoJson));
			});
		if (JsonUtils.isEmpty(json)) return Optional.empty();
		return Optional.of( json );
		
	}
	private static Optional<JSONObject> getInvoiceInfoJSON(InvoiceInfo info) {
		if (info == null) return Optional.empty();
		return Optional.of( new JSONObject()
			.put(IJsonNames.COMMUNICATION_TYPE, InvoiceCommunicationType.name(info.getType()))
			.put(IJsonNames.COMMUNICATION_STATUS, InvoiceCommunicationStatus.name(info.getStatus()))
			.put(IJsonNames.CREATION_USER, info.getCreationUser())
			.put(IJsonNames.CREATION_DATE, JsonUtils.getDateTimeJSON(info.getCreationDate()))
			.put(IJsonNames.MODIFICATION_USER, info.getModificationUser())
			.put(IJsonNames.MODIFICATION_DATE, JsonUtils.getDateTimeJSON(info.getModificationDate()))
			.put(IJsonNames.CHECK_URL, info.getCheckUrl())
		);
	}
	
	// ---------------------------- [TO INVOICE BREAKDOWN] ----------------------------
	private static void addJSONBreakdown(Invoice inv, JSONObject invoiceJson) {
		if (inv == null) return;
		inv.getTaxBreakdown()
			.ifPresent( br -> invoiceJson.put( IJsonNames.INVOICE_BREAKDOWN, 
				JsonUtils.nullIfEmpty(
					br.getBreakdown().stream()
						.map(b -> new JSONObject()
							.put(IJsonNames.ID, b.getId())
							.put(IJsonNames.TAX_TYPE, TaxType.name(b.getTaxType()))
							.put(IJsonNames.TAXABLE_BASE, b.getBase())
							.put(IJsonNames.PERCENTAGE, b.getPercentage())
							.put(IJsonNames.QUOTA, b.getQuota())
							.put(IJsonNames.SURCHARGE, b.getSurcharge())
							.put(IJsonNames.SURCHARGE_QUOTA, b.getSurchargeQuota())
							.put(IJsonNames.DEDUCTIBLE_QUOTA, b.getDeductibleQuota())
							.put(IJsonNames.VAT_DEDUCTION_TYPE, VatDeductionType.name(b.getVatDeductionType()))
							.put(IJsonNames.WITHHOLDING_TYPE, WithholdingType.name(b.getWithholdingType()))
						)
						.collect(Collector.of(JSONArray::new,JSONArray::put,(left, right) -> left, Collector.Characteristics.UNORDERED))
					)
				)
			);
	}
	
	private static JSONObject toInvoiceErrorContextJSON(InvoiceErrorContext iec) {
		if (iec == null) return null;
		return new JSONObject()
			.put(IJsonNames.LINE, iec.getLine())
			.put(IJsonNames.KEY, InvoiceErrorKey.name(iec.getKey()));
	}

}
