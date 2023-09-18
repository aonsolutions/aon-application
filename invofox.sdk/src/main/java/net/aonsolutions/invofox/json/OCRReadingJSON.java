package net.aonsolutions.invofox.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.invofox.model.OCRReading;

public class OCRReadingJSON {
	
	private OCRReadingJSON() {
	}
	
	public static List<OCRReading> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCRReadingJSON::from)
			.toList();		
	}
	
	public static OCRReading from(JSONObject json) {
		if (json == null) return null; 
		return new OCRReading()
			.setPrevious(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.PREVIOUS)))
			.setCurrent(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.CURRENT)))
			.setUsage(OCRNumberJSON.from(OCRJSONUtils.getObject(json, OCRNames.USAGE)))
		;
	}
	
	public static JSONArray to(List<OCRReading> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRReading> stream) {
		return stream
			.map(OCRReadingJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRReading field) {
		if (field == null) return null;
		return new JSONObject()
			.putOpt(OCRNames.PREVIOUS, OCRNumberJSON.to(field.getPrevious().orElse(null)))
			.putOpt(OCRNames.CURRENT, OCRNumberJSON.to(field.getCurrent().orElse(null)))
			.putOpt(OCRNames.USAGE, OCRNumberJSON.to(field.getUsage().orElse(null)))
		;
	}
}
