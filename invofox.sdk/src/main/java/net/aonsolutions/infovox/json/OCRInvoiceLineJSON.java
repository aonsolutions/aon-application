package net.aonsolutions.infovox.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import net.aonsolutions.infovox.model.OCRInvoiceLine;
import net.aonsolutions.watson.client.util.AonCollectionUtils;

public class OCRInvoiceLineJSON {
	
	private OCRInvoiceLineJSON() {
	}
	
	public static List<OCRInvoiceLine> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCRInvoiceLineJSON::from)
			.toList();		
	}
	
	public static OCRInvoiceLine from(JSONObject json) {
		if (json == null) return null; 
		return new OCRInvoiceLine()
			.setGroup(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.GROUP)))
			.setIdentifier(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.IDENTIFIER)))
			.setDescription(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.DESCRIPTION)))
			.setDate(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.DATE)))
			.setQuantity(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.QUANTITY)))
			.setUnitOfMeasurement(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.UNIT_OF_MEASUREMENT)))
			.setGrossUnitPrice(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.GROSS_UNIT_PRICE)))
			.setTaxBaseUnitPrice(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.TAX_BASE_UNIT_PRICE)))
			.setTotalUnitPrice(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.TOTAL_UNIT_PRICE)))
			.setDiscountAmount(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.DISCOUNT_AMOUNT)))
			.setDiscountRate(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.DISCOUNT_RATE)))
			.setTaxRate(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.TAX_RATE)))
			.setTaxAmount(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.TAX_AMOUNT)))
			.setTaxBaseAmount(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.TAX_BASE_AMOUNT)))
			.setTotalAmount(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.TOTAL_AMOUNT)))
			.setGrossAmount(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.GROSS_AMOUNT)))
			.setFeesAmount(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.FEES_AMOUNT)))
			.setFeesRate(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.FEES_RATE)))
		;
	}
	
	public static JSONArray to(List<OCRInvoiceLine> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRInvoiceLine> stream) {
		return stream
			.map(OCRInvoiceLineJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRInvoiceLine line) {
		if (line== null) return null;
		return new JSONObject()
			.putOpt(OCRNames.GROUP, OCRStringJSON.to(line.getGroup().orElse(null)))
			.putOpt(OCRNames.IDENTIFIER, OCRStringJSON.to(line.getIdentifier().orElse(null)))
			.putOpt(OCRNames.DESCRIPTION, OCRStringJSON.to(line.getDescription().orElse(null)))
			.putOpt(OCRNames.DATE, OCRStringJSON.to(line.getDate().orElse(null)))
			.putOpt(OCRNames.QUANTITY, OCRNumberJSON.to(line.getQuantity().orElse(null)))
			.putOpt(OCRNames.QUANTITY, OCRNumberJSON.to(line.getQuantity().orElse(null))) 
			.putOpt(OCRNames.UNIT_OF_MEASUREMENT, OCRNumberJSON.to(line.getUnitOfMeasurement().orElse(null))) 
			.putOpt(OCRNames.GROSS_UNIT_PRICE, OCRNumberJSON.to(line.getGrossUnitPrice().orElse(null)))
			.putOpt(OCRNames.TAX_BASE_UNIT_PRICE, OCRNumberJSON.to(line.getTaxBaseUnitPrice().orElse(null))) 
			.putOpt(OCRNames.TOTAL_UNIT_PRICE, OCRNumberJSON.to(line.getTotalUnitPrice().orElse(null)))
			.putOpt(OCRNames.DISCOUNT_AMOUNT, OCRNumberJSON.to(line.getDiscountAmount().orElse(null)))
			.putOpt(OCRNames.DISCOUNT_RATE, OCRNumberJSON.to(line.getDiscountRate().orElse(null)))
			.putOpt(OCRNames.TAX_RATE, OCRNumberJSON.to(line.getTaxRate().orElse(null)))
			.putOpt(OCRNames.TAX_AMOUNT, OCRNumberJSON.to(line.getTaxAmount().orElse(null)))
			.putOpt(OCRNames.TAX_BASE_AMOUNT, OCRNumberJSON.to(line.getTaxBaseAmount().orElse(null)))
			.putOpt(OCRNames.TOTAL_AMOUNT, OCRNumberJSON.to(line.getTotalAmount().orElse(null)))
			.putOpt(OCRNames.GROSS_AMOUNT, OCRNumberJSON.to(line.getGrossAmount().orElse(null)))
			.putOpt(OCRNames.FEES_AMOUNT, OCRNumberJSON.to(line.getFeesAmount().orElse(null)))
			.putOpt(OCRNames.FEES_RATE, OCRNumberJSON.to(line.getFeesRate().orElse(null))) 
		;
	}
}
