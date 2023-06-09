package net.aonsolutions.infovox.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import net.aonsolutions.infovox.model.OCRInvoiceDue;
import net.aonsolutions.watson.client.util.AonCollectionUtils;

public class OCRInvoiceDueJSON {
	
	private OCRInvoiceDueJSON() {
	}
	
	public static List<OCRInvoiceDue> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCRInvoiceDueJSON::from)
			.toList();		
	}
	
	public static OCRInvoiceDue from(JSONObject json) {
		if (json == null) return null; 
		return new OCRInvoiceDue()
			.setDate(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.DATE)))
			.setAmount(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.AMOUNT)))
		;
	}
	
	public static JSONArray to(List<OCRInvoiceDue> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRInvoiceDue> stream) {
		return stream
			.map(OCRInvoiceDueJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRInvoiceDue field) {
		if (field == null) return null;
		return new JSONObject()
			.putOpt(OCRNames.DATE, OCRStringJSON.to(field.getDate().orElse(null)))
			.putOpt(OCRNames.AMOUNT, OCRNumberJSON.to(field.getAmount().orElse(null)))
		;
	}
}
