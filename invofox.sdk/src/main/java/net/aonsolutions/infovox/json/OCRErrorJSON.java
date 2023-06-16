package net.aonsolutions.infovox.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import net.aonsolutions.infovox.model.OCRError;
import net.aonsolutions.infovox.model.OCRSeverity;
import net.aonsolutions.watson.client.util.AonCollectionUtils;

public class OCRErrorJSON {
	
	private OCRErrorJSON() {
	}
	
	public static List<OCRError> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCRErrorJSON::from)
			.toList();		
	}
	
	public static OCRError from(JSONObject json) {
		if (json == null) return null; 
		return new OCRError()
			.setSeverity( OCRSeverity.safeValueOf(OCRJSONUtils.getString(json, OCRNames.SEVERITY)).orElse(null) )
			.setCode(OCRJSONUtils.getString(json, OCRNames.CODE))
			.setAdditionalInfo(OCRAdditionalInfoJSON.from(OCRJSONUtils.getObject(json, OCRNames.ADDITIONAL_INFO)))
			.setTimestamp(OCRJSONUtils.getString(json, OCRNames.TIMESTAMP))
			.setUser(OCRJSONUtils.getString(json, OCRNames.USER))
			.setInfo(OCRJSONUtils.getString(json, OCRNames.INFO))
			.setTransactionId(OCRJSONUtils.getString(json, OCRNames.TRANSACTION_ID))
			.setFields(OCRFieldJSON.from(OCRJSONUtils.getArray(json, OCRNames.FIELDS)))
		;
	}
	
	public static JSONArray to(List<OCRError> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRError> stream) {
		return stream
			.map(OCRErrorJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRError error) {
		if (error == null) return null;
		return new JSONObject()
			.putOpt(OCRNames.SEVERITY, error.getSeverity().orElse(null))
			.putOpt(OCRNames.CODE, error.getCode().orElse(null))
			.putOpt(OCRNames.ADDITIONAL_INFO, error.getAdditionalInfo().map(OCRAdditionalInfoJSON::to).orElse(null))
			.putOpt(OCRNames.TIMESTAMP, error.getTimestamp().orElse(null))
			.putOpt(OCRNames.USER, error.getUser().orElse(null))
			.putOpt(OCRNames.INFO, error.getInfo().orElse(null))
			.putOpt(OCRNames.TRANSACTION_ID, error.getTransactionId().orElse(null))
			.putOpt(OCRNames.FIELDS, OCRFieldJSON.to(error.getFields().orElse(null)))
		;
	}
}
