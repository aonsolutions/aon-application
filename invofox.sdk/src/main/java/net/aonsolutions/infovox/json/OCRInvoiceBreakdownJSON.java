package net.aonsolutions.infovox.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.infovox.model.OCRInvoiceBreakdown;

public class OCRInvoiceBreakdownJSON {
	
	private OCRInvoiceBreakdownJSON() {
	}
	
	public static List<OCRInvoiceBreakdown> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCRInvoiceBreakdownJSON::from)
			.toList();		
	}
	
	public static OCRInvoiceBreakdown from(JSONObject json) {
		if (json == null) return null; 
		return new OCRInvoiceBreakdown()
			.setTaxRate(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.TAX_RATE)))
			.setTaxBaseAmount(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.TAX_BASE_AMOUNT)))
			.setTaxAmount(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.TAX_AMOUNT)))
			.setReRate(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.RE_RATE)))
			.setReAmount(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.RE_AMOUNT)))
			.setTotalAmount(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.TOTAL_AMOUNT)))
			.setGrossAmount(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.GROSS_AMOUNT)))
			.setDiscountBaseAmount(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.DISCOUNT_BASE_AMOUNT)))
			.setDiscountAmount(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.DISCOUNT_AMOUNT)))
			.setDiscountRate(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.DISCOUNT_RATE)))
			.setFeesBaseAmount(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.FEES_BASE_AMOUNT)))
			.setFeesAmount(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.FEES_AMOUNT)))
			.setFeesRate(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.FEES_RATE)))
		;
	}
	
	public static JSONArray to(List<OCRInvoiceBreakdown> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRInvoiceBreakdown> stream) {
		return stream
			.map(OCRInvoiceBreakdownJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRInvoiceBreakdown line) {
		if (line== null) return null;
		return new JSONObject()
			.putOpt(OCRNames.TAX_RATE, OCRNumberJSON.to(line.getTaxRate().orElse(null)))
			.putOpt(OCRNames.TAX_BASE_AMOUNT, OCRNumberJSON.to(line.getTaxBaseAmount().orElse(null)))
			.putOpt(OCRNames.TAX_AMOUNT, OCRNumberJSON.to(line.getTaxAmount().orElse(null)))
			.putOpt(OCRNames.RE_RATE, OCRNumberJSON.to(line.getReRate().orElse(null)))
			.putOpt(OCRNames.RE_AMOUNT, OCRNumberJSON.to(line.getReAmount().orElse(null)))
			.putOpt(OCRNames.TOTAL_AMOUNT, OCRNumberJSON.to(line.getTotalAmount().orElse(null)))
			.putOpt(OCRNames.GROSS_AMOUNT, OCRNumberJSON.to(line.getGrossAmount().orElse(null)))
			.putOpt(OCRNames.DISCOUNT_BASE_AMOUNT, OCRNumberJSON.to(line.getDiscountBaseAmount().orElse(null)))
			.putOpt(OCRNames.DISCOUNT_AMOUNT, OCRNumberJSON.to(line.getDiscountAmount().orElse(null)))
			.putOpt(OCRNames.DISCOUNT_RATE, OCRNumberJSON.to(line.getDiscountRate().orElse(null)))
			.putOpt(OCRNames.FEES_BASE_AMOUNT, OCRNumberJSON.to(line.getFeesBaseAmount().orElse(null)))
			.putOpt(OCRNames.FEES_AMOUNT, OCRNumberJSON.to(line.getFeesAmount().orElse(null)))
			.putOpt(OCRNames.FEES_RATE, OCRNumberJSON.to(line.getFeesRate().orElse(null))) 
		;
	}
}
