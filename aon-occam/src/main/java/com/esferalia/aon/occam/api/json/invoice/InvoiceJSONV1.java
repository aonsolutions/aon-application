package com.esferalia.aon.occam.api.json.invoice;

import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.AmortizationJSON;
import com.esferalia.aon.occam.api.json.EnterpriseActivityJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.RegistryAddressJSON;
import com.esferalia.aon.occam.api.json.RegistryJSON;
import com.esferalia.aon.occam.api.json.ScopeJSON;
import com.esferalia.aon.occam.api.json.doc.InvoiceDocJSON;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.finance.InvoiceStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorContext;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorKey;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;

class InvoiceJSONV1 {
	
	private InvoiceJSONV1() {
	
	}
	
	static Invoice fromJSON(String json) {
		return fromJSON(new JSONObject(json));
	}
	
	static Invoice fromJSON(JSONObject json) {
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
		List<InvoiceError> messageList = InvoiceErrorJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.MESSAGES));
		LinkedList<InvoiceError> messages = new LinkedList<>();
		messages.addAll(messageList);
		return new Invoice()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setType(type)
			.setSeries(json.optString(IJsonNames.SERIES))
			.setNumber(JsonUtils.getInt(json, IJsonNames.NUMBER))
			.setScope(ScopeJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.SCOPE)))
			.setTransaction(InvoiceTransactionType.safeValueOf(json.optString(IJsonNames.TRANSACTION)))
			.setReferenceCode(JsonUtils.getString(json, IJsonNames.REFERENCE))
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
			.setRectificationInvoiceReference(rectificationInvoice.getReferenceCode())			
			.setTotal(JsonUtils.getdouble(json, IJsonNames.TOTAL))
			.setRegistryData(registry)
			.setRegistry(registry.getId())
			.setRegistryDocument(registry.getDocument())
			.setRegistryDocumentCountry(registry.getDocumentCountry())
			.setRegistryDocumentType(registry.getDocumentType())
			.setRegistryName(registry.getName())
			.setRegistryAddress(raddress.getId())
			.setAddress(raddress)
			.setSigned(JsonUtils.getboolean(json, IJsonNames.SIGNED))
			.setBreakdown(InvoiceBreakdownJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.TAXES)))
			.setDetails(InvoiceDetailJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.DETAILS)))
			.setFinances(FinanceJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.FINANCES)))
			.setMessages(messages)
			.setTediCategory(JsonUtils.getString(json, IJsonNames.CATEGORY))
			.setActivity(EnterpriseActivityJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.ACTIVITY)))
			.setAmortization(AmortizationJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.AMORTIZATION)))
			.setDoc(InvoiceDocJSON.from(JsonUtils.getJSONObject(json, IJsonNames.INVOICE_DOC)).orElse(null))
			.addCommunicationInfo(getCommunicationInfo( JsonUtils.getJSONObject(json, IJsonNames.COMMUNICATION_INFO) ).orElse(null))
		;
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
			.setIssueDate(JsonUtils.getDate(json, IJsonNames.DATE))
			.setReferenceCode(JsonUtils.getString(json, IJsonNames.REFERENCE_CODE))
		;
	}
	
	static JSONArray toJSON(List<Invoice> invoices) {
		JSONArray array = new JSONArray();
		invoices.stream().forEach(invoice -> array.put(toJSON(invoice)));
		return array;
	}
	
	static JSONObject toJSON(Invoice invoice) {
		String date = AonDateUtils.format(invoice.getIssueDate(), AonDateUtils.DATE_TIME_FORMAT_AUX);
		String expDate = AonDateUtils.format(invoice.getExpDate(), AonDateUtils.DATE_TIME_FORMAT_AUX);
		
		JSONObject json = new JSONObject()
			.put(IJsonNames.STATUS, invoice.isRecorded() 
				? InvoiceStatus.SCORED.name().toLowerCase() 
				: InvoiceStatus.PENDING.name().toLowerCase())
			.put(IJsonNames.ID, invoice.getId())
			.put(IJsonNames.DOMAIN, invoice.getDomain())
			.put(IJsonNames.SERIES, invoice.getSeries())
			.put(IJsonNames.NUMBER, invoice.getNumber())
			.put(IJsonNames.DATE, date) //invoice.getIssueDate())
			.put(IJsonNames.EXP_DATE, expDate)
			.put(IJsonNames.REFERENCE, invoice.getReferenceCode())
			.put(IJsonNames.TYPE, invoice.getType().getTediName())
			.put(IJsonNames.TRANSACTION, invoice.getTransaction().getTediName())
			.put(IJsonNames.INVESTMENT, invoice.isInvestment())
			.put(IJsonNames.SERVICE, invoice.isService())
			.put(IJsonNames.SIGNED, invoice.isSigned())
			.put(IJsonNames.WITHHOLDING, invoice.isWithholding())
			.put(IJsonNames.WITHHOLDING_FARMER, invoice.isWithholdingFarmer())
			.put(IJsonNames.VAT_ACCRUAL_PAYMENT, invoice.isVatAccrualPayment())
			.put(IJsonNames.SURCHARGE, invoice.isSurcharge())
			.put(IJsonNames.RECTIFIED, invoice.isRectified())
			.put(IJsonNames.RECTIFIER, invoice.isRectifier())
			.put(IJsonNames.COMMENTS, invoice.getComments())
			.put(IJsonNames.REMARKS, invoice.getRemarks())
			.put(IJsonNames.TAXABLE_BASE, invoice.getTaxableBase())
			.put(IJsonNames.VAT_QUOTA, invoice.getVatQuota())
			.put(IJsonNames.RETENTION_QUOTA, invoice.getRetentionQuota())
			.put(IJsonNames.TOTAL, invoice.getTotal())
			.put(IJsonNames.SENDER,RegistryJSON.toJSON(invoice.getRegistryData()))
			.put(IJsonNames.RECEIVER, RegistryJSON.toJSON(invoice.getRegistryData()))
			.put(IJsonNames.TAXES, InvoiceBreakdownJSON.toJSON(invoice.getBreakdown()))
			.put(IJsonNames.DETAILS, InvoiceDetailJSON.toJSON(invoice.getDetails()))
			.put(IJsonNames.FINANCES, FinanceJSON.toJSON(invoice.getFinances()))
			.put(IJsonNames.ACTIVITY, EnterpriseActivityJSON.toJSON(invoice.getActivity()))
			.put(IJsonNames.AMORTIZATION, invoice.getAmortization() != null ? AmortizationJSON.toJSON(invoice.getAmortization()) : JSONObject.NULL)
			.put(IJsonNames.SCOPE, ScopeJSON.toJSON(invoice.getScope()))
			.put(IJsonNames.INVOICE_DOC, invoice.getDoc().map(InvoiceDocJSON::to).orElse(null))
			.put(IJsonNames.COMMUNICATION_INFO, getCommunicationInfoJSON(invoice.getCommunicationInfo()).orElse(null))
		;
		
		json.put( IJsonNames.MESSAGES,JsonUtils.nullIfEmpty(
				invoice.messageStream()
				.map(m -> new JSONObject() 
						.put(IJsonNames.CODE, m.getCode())
						.put(IJsonNames.MESSAGE, m.getMessage())
						.put(IJsonNames.LEVEL, InvoiceErrorLevel.name(m.getLevel()))
						.put(IJsonNames.CONTEXT, toInvoiceErrorContextJSON(m.getContext()))
				)
				.collect(Collector.of(JSONArray::new,JSONArray::put,(left, right) -> left, Collector.Characteristics.UNORDERED))
			))			
		;
				
		if(invoice.isRectifier() || invoice.isRectified()) {
			String rectificationInvoiceDate = AonDateUtils.format(invoice.getRectificationInvoiceDate() , AonDateUtils.DATE_TIME_FORMAT_AUX);

			JSONObject rectificationInvoice = new JSONObject()
				.put(IJsonNames.ID, invoice.getRectificationInvoice())
				.put(IJsonNames.SERIES, invoice.getRectificationInvoiceSeries())
				.put(IJsonNames.NUMBER, invoice.getRectificationInvoiceNumber())
				.put(IJsonNames.REFERENCE_CODE, invoice.getRectificationInvoiceReference())
				.put(IJsonNames.DATE, rectificationInvoiceDate);
			
			json.put(IJsonNames.RECTIFICATION_INVOICE, rectificationInvoice);
		}
		
		if(!invoice.getDetails().isEmpty()) {
			json.put(IJsonNames.CATEGORY, invoice.getDetails().get(0).getAccountCode());
		}
			
		if(invoice.getAddress() != null) {
			JSONObject address = RegistryAddressJSON.toJSON(invoice.getAddress());
			JSONObject registry = InvoiceType.SALES.equals(invoice.getType()) 
					? json.optJSONObject(IJsonNames.RECEIVER)
					: json.optJSONObject(IJsonNames.SENDER);
			registry.put(IJsonNames.ADDRESS, address);
		}
		extractUniqueWorkplaceFromDetails(json, invoice);
		return json;
	}
	
	private static void extractUniqueWorkplaceFromDetails(JSONObject json, Invoice invoice) {
		if (invoice.hasDetails() ) {
			Integer workplace = invoice.detailStream()
				.map(d -> d.getWorkplace())
				.filter(w -> w != null)
				.map(w -> w.getId())
				.filter(i -> i != null)
				.distinct()
				.limit(2)
			    .findFirst() 
			    .orElse(null);
			json.put(IJsonNames.WORKPLACE, workplace);
		}
	}
	
	private static JSONObject toInvoiceErrorContextJSON(InvoiceErrorContext iec) {
		if (iec == null) return null;
		return new JSONObject()
			.put(IJsonNames.LINE, iec.getLine())
			.put(IJsonNames.KEY, InvoiceErrorKey.name(iec.getKey()));
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
	
	// ---------------------------- [FROM INVOICE INFO] ----------------------------
	private static Optional<Map<InvoiceCommunicationType, InvoiceInfo>> getCommunicationInfo(JSONObject json) {
		if (JsonUtils.isEmpty(json)) return Optional.empty();
		Map<InvoiceCommunicationType, InvoiceInfo> map = new EnumMap<>(InvoiceCommunicationType.class);
		AonCollectionUtils.stream(json.keySet())
			.map( InvoiceCommunicationType::safeValueOf)
			.filter(t -> t != null)
			.map( t -> new Pair<InvoiceCommunicationType,JSONObject>(t, json.getJSONObject(t.name())))
			.forEach( p -> getInvoiceInfo(p.getRight()).ifPresent( info -> map.put(p.getLeft(), info) ));
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
	
	// ---------------------------- [TO INVOICE INFO] ----------------------------
	static Optional<JSONObject> getCommunicationInfoJSON(Map<InvoiceCommunicationType, InvoiceInfo> enumMap) {
		if (AonCollectionUtils.isEmpty(enumMap)) return Optional.empty();
		JSONObject map = 
			AonCollectionUtils.stream(enumMap)
				.filter( e -> e.getKey() != null )
				.filter( e -> e.getValue() != null )
				.collect(Collector.of(
					JSONObject::new
					,(obj, e) -> obj.put( 
						InvoiceCommunicationType.name(e.getKey())
						,getInvoiceInfoJSON(e.getValue()).orElse(null) )
					,(left, right) -> left
					,Collector.Characteristics.UNORDERED
				)
			)
		;
		if (JsonUtils.isEmpty(map)) return Optional.empty();
		return Optional.of(map);
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
}
