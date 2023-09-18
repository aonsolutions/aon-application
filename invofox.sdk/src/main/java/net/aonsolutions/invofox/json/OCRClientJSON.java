package net.aonsolutions.invofox.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.invofox.model.OCRClientData;

public class OCRClientJSON {
	
	private OCRClientJSON() {
	}
	
	public static List<OCRClientData> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCRClientJSON::from)
			.toList();		
	}
	
	public static OCRClientData from(JSONObject json) {
		if (json == null) return null; 
		return new OCRClientData()
			.setFilename(OCRJSONUtils.getString(json, OCRNames.FILENAME))
			.setKey(OCRJSONUtils.getString(json, OCRNames.KEY))
			.setValue(OCRJSONUtils.getString(json, OCRNames.VALUE))
		;
	}
	
	public static JSONArray to(List<OCRClientData> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRClientData> stream) {
		return stream
			.map(OCRClientJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRClientData document) {
		if (document == null) return null;
		return new JSONObject()
			.putOpt(OCRNames.FILENAME, document.getFilename().orElse(null))
			.putOpt(OCRNames.KEY, document.getKey().orElse(null))
			.putOpt(OCRNames.VALUE, document.getValue().orElse(null))
		;
	}
}
