package net.aonsolutions.infovox.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import net.aonsolutions.infovox.model.OCRNumber;
import net.aonsolutions.watson.client.util.AonCollectionUtils;

public class OCRNumberJSON {
	
	private OCRNumberJSON() {
	}
	
	public static List<OCRNumber> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCRNumberJSON::from)
			.toList();		
	}
	
	public static OCRNumber from(JSONObject json) {
		if (json == null) return null; 
		return new OCRNumber()
			.setValue(OCRJSONUtils.getNumber(json, OCRNames.NAME))
			.setConfidence(OCRJSONUtils.getNumber(json, OCRNames.CONFIDENCE))
		;
	}
	
	public static JSONArray to(List<OCRNumber> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRNumber> stream) {
		return stream
			.map(OCRNumberJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRNumber field) {
		if (field == null) return null;
		return new JSONObject()
			.putOpt(OCRNames.VALUE, field.getValue())
			.putOpt(OCRNames.CONFIDENCE, field.getConfidence())
		;
	}
}
