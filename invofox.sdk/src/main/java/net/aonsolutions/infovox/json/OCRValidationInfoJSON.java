package net.aonsolutions.infovox.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import net.aonsolutions.infovox.model.OCRSeverity;
import net.aonsolutions.infovox.model.OCRValidationInfo;
import net.aonsolutions.watson.client.util.AonCollectionUtils;

public class OCRValidationInfoJSON {
	
	private OCRValidationInfoJSON() {
	}
	
	public static List<OCRValidationInfo> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCRValidationInfoJSON::from)
			.toList();		
	}
	
	public static OCRValidationInfo from(JSONObject json) {
		if (json == null) return null; 
		return new OCRValidationInfo()
			.setErrors(OCRErrorJSON.from(OCRJSONUtils.getArray(json, OCRNames.ERRORS)))
			.setResult( OCRSeverity.safeValueOf(OCRJSONUtils.getString(json, OCRNames.RESULT)).orElse(null) )
			.setComments(OCRJSONUtils.getString(json, OCRNames.COMMENTS))
			.setValidator(OCRJSONUtils.getString(json, OCRNames.VALIDATOR))
		;
	}
	
	public static JSONArray to(List<OCRValidationInfo> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRValidationInfo> stream) {
		return stream
			.map(OCRValidationInfoJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRValidationInfo info) {
		if (info == null) return null;
		return new JSONObject()
			.putOpt(OCRNames.ERRORS, OCRErrorJSON.to(info.getErrors().orElse(null)))
			.putOpt(OCRNames.RESULT, info.getResult().orElse(null))
			.putOpt(OCRNames.COMMENTS, info.getComments().orElse(null))
			.putOpt(OCRNames.VALIDATOR, info.getValidator().orElse(null))
		;
	}
}
