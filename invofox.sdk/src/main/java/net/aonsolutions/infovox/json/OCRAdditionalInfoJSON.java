package net.aonsolutions.infovox.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import net.aonsolutions.infovox.model.OCRAdditionalInfo;
import net.aonsolutions.watson.client.util.AonCollectionUtils;

public class OCRAdditionalInfoJSON {
	
	private OCRAdditionalInfoJSON() {
	}
	
	public static List<OCRAdditionalInfo> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCRAdditionalInfoJSON::from)
			.toList();		
	}
	
	public static OCRAdditionalInfo from(JSONObject json) {
		if (json == null) return null; 
		return new OCRAdditionalInfo()
			.setDescription(OCRJSONUtils.getString(json, OCRNames.DESCRIPTION))
			.setPath(OCRJSONUtils.getString(json, OCRNames.PATH))
			.setMessage(OCRJSONUtils.getString(json, OCRNames.MESSAGE))
		;
	}
	
	public static JSONArray to(List<OCRAdditionalInfo> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRAdditionalInfo> stream) {
		return stream
			.map(OCRAdditionalInfoJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRAdditionalInfo response) {
		if (response == null) return null;
		return new JSONObject()
			.putOpt(OCRNames.DESCRIPTION, response.getDescription())
			.putOpt(OCRNames.PATH, response.getPath())
			.putOpt(OCRNames.MESSAGE, response.getMessage())
		;
	}
}
