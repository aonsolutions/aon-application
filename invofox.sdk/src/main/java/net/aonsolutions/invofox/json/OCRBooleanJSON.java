package net.aonsolutions.invofox.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.invofox.model.OCRBoolean;

public class OCRBooleanJSON {
	
	private OCRBooleanJSON() {
	}
	
	public static List<OCRBoolean> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCRBooleanJSON::from)
			.toList();		
	}
	
	public static OCRBoolean from(JSONObject json) {
		if (json == null) return null; 
		return new OCRBoolean()
			.setValue(OCRJSONUtils.getBoolean(json, OCRNames.NAME))
			.setConfidence(OCRJSONUtils.getNumber(json, OCRNames.CONFIDENCE))
		;
	}
	
	public static JSONArray to(List<OCRBoolean> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRBoolean> stream) {
		return stream
			.map(OCRBooleanJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRBoolean field) {
		if (field == null) return null;
		return new JSONObject()
			.putOpt(OCRNames.VALUE, field.getValue().orElse(null))
			.putOpt(OCRNames.CONFIDENCE, field.getConfidence().orElse(null))
		;
	}
}
