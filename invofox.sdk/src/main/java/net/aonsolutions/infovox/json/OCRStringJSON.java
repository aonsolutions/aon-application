package net.aonsolutions.infovox.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import net.aonsolutions.infovox.model.OCRString;
import net.aonsolutions.watson.client.util.AonCollectionUtils;

public class OCRStringJSON {
	
	private OCRStringJSON() {
	}
	
	public static List<OCRString> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCRStringJSON::from)
			.toList();		
	}
	
	public static OCRString from(JSONObject json) {
		if (json == null) return null; 
		return new OCRString()
			.setValue(OCRJSONUtils.getString(json, OCRNames.NAME))
			.setConfidence(OCRJSONUtils.getNumber(json, OCRNames.CONFIDENCE))
		;
	}
	
	public static JSONArray to(List<OCRString> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRString> stream) {
		return stream
			.map(OCRStringJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRString field) {
		if (field == null) return null;
		return new JSONObject()
			.putOpt(OCRNames.VALUE, field.getValue())
			.putOpt(OCRNames.CONFIDENCE, field.getConfidence())
		;
	}
}
