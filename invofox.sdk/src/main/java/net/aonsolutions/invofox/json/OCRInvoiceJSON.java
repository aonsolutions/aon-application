package net.aonsolutions.invofox.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.invofox.model.OCRInvoice;

public class OCRInvoiceJSON {
	
	private OCRInvoiceJSON() {
	}
	
	public static List<OCRInvoice> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCRInvoiceJSON::from)
			.toList();		
	}
	
	public static OCRInvoice from(JSONObject json) {
		if (json == null) return null; 
		return new OCRInvoice()
			.setCurrency(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.CURRENCY)))
			.setLanguage(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.LANGUAGE)))
			.setIsCreditNote(OCRBooleanJSON.from(OCRJSONUtils.getObject(json, OCRNames.IS_CREDIT_NOTE)))
			.setNumberFormat(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.NUMBER_FORMAT)))
			.setInvoiceRef(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.INVOICE_REF)))
			.setSeriesCode(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.SERIES_CODE)))
			.setTaxClass(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.TAX_CLASS)))
			.setIssuerCountry(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.ISSUER_COUNTRY)))
			.setRecipientCountry(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.RECIPIENT_COUNTRY)))
			.setIssuerAddressDetails(OCRAddressJSON.from(OCRJSONUtils.getObject(json, OCRNames.ISSUER_ADDRESS_DETAILS)))
			.setRecipientAddressDetails(OCRAddressJSON.from(OCRJSONUtils.getObject(json, OCRNames.RECIPIENT_ADDRESS_DETAILS)))
			.setDocumentNumber(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.DOCUMENT_NUMBER)))
			.setIssueDate(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.ISSUE_DATE)))
			.setIssuerName(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.ISSUER_NAME)))
			.setIssuerTaxId(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.ISSUER_TAX_ID)))
			.setIssuerAddress(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.ISSUER_ADDRESS)))
			.setIssuerEmail(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.ISSUER_EMAIL)))
			.setIssuerPhoneNumber(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.ISSUER_PHONE_NUMBER)))
			.setIssuerWebsite(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.ISSUER_WEBSITE)))
			.setRecipientName(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.RECIPIENT_NAME)))
			.setRecipientTaxId(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.RECIPIENT_TAX_ID)))
			.setRecipientAddress(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.RECIPIENT_ADDRESS)))
			.setShippingAddress(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.SHIPPING_ADDRESS)))
			.setRecipientEmail(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.RECIPIENT_EMAIL)))
			.setRecipientWebsite(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.RECIPIENT_WEBSITE)))
			.setRecipientPhoneNumber(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.RECIPIENT_PHONE_NUMBER)))
			.setPaymentMethod(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.PAYMENT_METHOD)))
			.setIBAN(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.IBAN)))
			.setSWIFT(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.SWIFT)))
			.setClientCode(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.CLIENT_CODE)))
			.setDeliveryNoteRef(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.DELIVERY_NOTE_REF)))
			.setOrderRef(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.ORDER_REF)))
			.setContractRef(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.CONTRACT_REF)))
			.setIncoterms(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.INCOTERMS)))
			.setDocumentType(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.DOCUMENT_TYPE)))
			.setAdditionalNotes(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.ADDITIONAL_NOTES)))
			.setLegalNotes(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.LEGAL_NOTES)))
			.setTotalTaxAmount(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.TOTAL_TAX_AMOUNT)))
			.setTotalTaxBaseAmount(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.TOTAL_TAX_BASE_AMOUNT)))
			.setTotalAmount(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.TOTAL_AMOUNT)))
			.setTotalGrossAmount(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.TOTAL_GROSS_AMOUNT)))
			.setTotalDueAmount(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.TOTAL_DUE_AMOUNT)))
			.setWithholdingTaxRate(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.WITHHOLDING_TAX_RATE)))
			.setWithholdingTaxAmount(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.WITHHOLDING_TAX_AMOUNT)))
			.setTotalFeesAmount(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.TOTAL_FEES_AMOUNT)))
			.setTotalDiscountAmount(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.TOTAL_DISCOUNT_AMOUNT)))
			.setReimbursableExpensesAmount(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.REIMBURSABLE_EXPENSES_AMOUNT)))
			.setAdditionalChargesAmount(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.ADDITIONAL_CHARGES_AMOUNT)))
			.setAdditionalDiscountsAmount(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.ADDITIONAL_DISCOUNTS_AMOUNT)))
			.setServiceAddress(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.SERVICE_ADDRESS)))
			.setSupplyNumber(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.SUPPLY_NUMBER)))
			.setMeterNumber(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.METER_NUMBER)))
			.setTotalUsage(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.TOTAL_USAGE)))
			.setUsageUnitOfMeasurement(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.USAGE_UNIT_OF_MEASUREMENT)))
			.setLines(OCRInvoiceLineJSON.from(OCRJSONUtils.getArray(json, OCRNames.LINES)))
			.setBreakdowns(OCRInvoiceBreakdownJSON.from(OCRJSONUtils.getArray(json, OCRNames.BREAKDOWNS)))
			.setDues(OCRInvoiceDueJSON.from(OCRJSONUtils.getArray(json, OCRNames.DUES)))
			.setReadings(OCRReadingJSON.from(OCRJSONUtils.getArray(json, OCRNames.READINGS)))
		;
		
	}
	
	public static JSONArray to(List<OCRInvoice> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRInvoice> stream) {
		return stream
			.map(OCRInvoiceJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRInvoice error) {
		if (error == null) return null;
		return new JSONObject()
				.putOpt(OCRNames.CURRENCY, OCRStringJSON.to(error.getCurrency().orElse(null)))
				.putOpt(OCRNames.LANGUAGE, OCRStringJSON.to(error.getLanguage().orElse(null)))
				.putOpt(OCRNames.IS_CREDIT_NOTE, OCRBooleanJSON.to(error.getIsCreditNote().orElse(null)))
				.putOpt(OCRNames.NUMBER_FORMAT, OCRStringJSON.to(error.getNumberFormat().orElse(null)))
				.putOpt(OCRNames.INVOICE_REF, OCRStringJSON.to(error.getInvoiceRef().orElse(null)))
				.putOpt(OCRNames.SERIES_CODE, OCRStringJSON.to(error.getSeriesCode().orElse(null)))
				.putOpt(OCRNames.TAX_CLASS, OCRStringJSON.to(error.getTaxClass().orElse(null)))
				.putOpt(OCRNames.ISSUER_COUNTRY, OCRStringJSON.to(error.getIssuerCountry().orElse(null)))
				.putOpt(OCRNames.RECIPIENT_COUNTRY, OCRStringJSON.to(error.getRecipientCountry().orElse(null)))
				.putOpt(OCRNames.ISSUER_ADDRESS_DETAILS, OCRAddressJSON.to(error.getIssuerAddressDetails().orElse(null)))
				.putOpt(OCRNames.RECIPIENT_ADDRESS_DETAILS, OCRAddressJSON.to(error.getRecipientAddressDetails().orElse(null)))
				.putOpt(OCRNames.DOCUMENT_NUMBER, OCRStringJSON.to(error.getDocumentNumber().orElse(null)))
				.putOpt(OCRNames.ISSUE_DATE, OCRStringJSON.to(error.getIssueDate().orElse(null)))
				.putOpt(OCRNames.ISSUER_NAME, OCRStringJSON.to(error.getIssuerName().orElse(null)))
				.putOpt(OCRNames.ISSUER_TAX_ID, OCRStringJSON.to(error.getIssuerTaxId().orElse(null)))
				.putOpt(OCRNames.ISSUER_ADDRESS, OCRStringJSON.to(error.getIssuerAddress().orElse(null)))
				.putOpt(OCRNames.ISSUER_EMAIL, OCRStringJSON.to(error.getIssuerEmail().orElse(null)))
				.putOpt(OCRNames.ISSUER_PHONE_NUMBER, OCRStringJSON.to(error.getIssuerPhoneNumber().orElse(null)))
				.putOpt(OCRNames.ISSUER_WEBSITE, OCRStringJSON.to(error.getIssuerWebsite().orElse(null)))
				.putOpt(OCRNames.RECIPIENT_NAME, OCRStringJSON.to(error.getRecipientName().orElse(null)))
				.putOpt(OCRNames.RECIPIENT_TAX_ID, OCRStringJSON.to(error.getRecipientTaxId().orElse(null)))
				.putOpt(OCRNames.RECIPIENT_ADDRESS, OCRStringJSON.to(error.getRecipientAddress().orElse(null)))
				.putOpt(OCRNames.SHIPPING_ADDRESS, OCRStringJSON.to(error.getShippingAddress().orElse(null)))
				.putOpt(OCRNames.RECIPIENT_EMAIL, OCRStringJSON.to(error.getRecipientEmail().orElse(null)))
				.putOpt(OCRNames.RECIPIENT_WEBSITE, OCRStringJSON.to(error.getRecipientWebsite().orElse(null)))
				.putOpt(OCRNames.RECIPIENT_PHONE_NUMBER, OCRStringJSON.to(error.getRecipientPhoneNumber().orElse(null)))
				.putOpt(OCRNames.PAYMENT_METHOD, OCRStringJSON.to(error.getPaymentMethod().orElse(null)))
				.putOpt(OCRNames.IBAN, OCRStringJSON.to(error.getIBAN().orElse(null)))
				.putOpt(OCRNames.SWIFT, OCRStringJSON.to(error.getSWIFT().orElse(null)))
				.putOpt(OCRNames.CLIENT_CODE, OCRStringJSON.to(error.getClientCode().orElse(null)))
				.putOpt(OCRNames.DELIVERY_NOTE_REF, OCRStringJSON.to(error.getDeliveryNoteRef().orElse(null)))
				.putOpt(OCRNames.ORDER_REF, OCRStringJSON.to(error.getOrderRef().orElse(null)))
				.putOpt(OCRNames.CONTRACT_REF, OCRStringJSON.to(error.getContractRef().orElse(null)))
				.putOpt(OCRNames.INCOTERMS, OCRStringJSON.to(error.getIncoterms().orElse(null)))
				.putOpt(OCRNames.DOCUMENT_TYPE, OCRStringJSON.to(error.getDocumentType().orElse(null)))
				.putOpt(OCRNames.ADDITIONAL_NOTES, OCRStringJSON.to(error.getAdditionalNotes().orElse(null)))
				.putOpt(OCRNames.LEGAL_NOTES, OCRStringJSON.to(error.getLegalNotes().orElse(null)))
				.putOpt(OCRNames.TOTAL_TAX_AMOUNT, OCRNumberJSON.to(error.getTotalTaxAmount().orElse(null)))
				.putOpt(OCRNames.TOTAL_TAX_BASE_AMOUNT, OCRNumberJSON.to(error.getTotalTaxBaseAmount().orElse(null)))
				.putOpt(OCRNames.TOTAL_AMOUNT, OCRNumberJSON.to(error.getTotalAmount().orElse(null)))
				.putOpt(OCRNames.TOTAL_GROSS_AMOUNT, OCRNumberJSON.to(error.getTotalGrossAmount().orElse(null)))
				.putOpt(OCRNames.TOTAL_DUE_AMOUNT, OCRNumberJSON.to(error.getTotalDueAmount().orElse(null)))
				.putOpt(OCRNames.WITHHOLDING_TAX_RATE, OCRNumberJSON.to(error.getWithholdingTaxRate().orElse(null)))
				.putOpt(OCRNames.WITHHOLDING_TAX_AMOUNT, OCRNumberJSON.to(error.getWithholdingTaxAmount().orElse(null)))
				.putOpt(OCRNames.TOTAL_FEES_AMOUNT, OCRNumberJSON.to(error.getTotalFeesAmount().orElse(null)))
				.putOpt(OCRNames.TOTAL_DISCOUNT_AMOUNT, OCRNumberJSON.to(error.getTotalDiscountAmount().orElse(null)))
				.putOpt(OCRNames.REIMBURSABLE_EXPENSES_AMOUNT, OCRNumberJSON.to(error.getReimbursableExpensesAmount().orElse(null)))
				.putOpt(OCRNames.ADDITIONAL_CHARGES_AMOUNT, OCRNumberJSON.to(error.getAdditionalChargesAmount().orElse(null)))
				.putOpt(OCRNames.ADDITIONAL_DISCOUNTS_AMOUNT, OCRNumberJSON.to(error.getAdditionalDiscountsAmount().orElse(null)))
				.putOpt(OCRNames.SERVICE_ADDRESS, OCRStringJSON.to(error.getServiceAddress().orElse(null)))
				.putOpt(OCRNames.SUPPLY_NUMBER, OCRNumberJSON.to(error.getSupplyNumber().orElse(null)))
				.putOpt(OCRNames.METER_NUMBER, OCRNumberJSON.to(error.getMeterNumber().orElse(null)))
				.putOpt(OCRNames.TOTAL_USAGE, OCRNumberJSON.to(error.getTotalUsage().orElse(null)))
				.putOpt(OCRNames.USAGE_UNIT_OF_MEASUREMENT, OCRNumberJSON.to(error.getUsageUnitOfMeasurement().orElse(null)))
				.putOpt(OCRNames.LINES, OCRInvoiceLineJSON.to(error.getLines().orElse(null)))
				.putOpt(OCRNames.BREAKDOWNS, OCRInvoiceBreakdownJSON.to(error.getBreakdowns().orElse(null)))
				.putOpt(OCRNames.DUES, OCRInvoiceDueJSON.to(error.getDues().orElse(null)))
				.putOpt(OCRNames.READINGS, OCRReadingJSON.to(error.getReadings().orElse(null)))
		;
	}
}
