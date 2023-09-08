package net.aonsolutions.invofox.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.invofox.model.OCRCompanyResponse;

public class OCRCompanyResponseJSON {
	
	private OCRCompanyResponseJSON() {
	}
	
	public static List<OCRCompanyResponse> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCRCompanyResponseJSON::from)
			.toList();		
	}
	
	public static OCRCompanyResponse from(JSONObject json) {
		if (json == null) return null; 
		return new OCRCompanyResponse()
			.setHttpCode(OCRJSONUtils.getInteger(json, OCRNames.HTTP_CODE))
			.setCompanies(OCRCompanyJSON.from(OCRJSONUtils.getObject(json, OCRNames.RESULT)))
			.setError(OCRErrorJSON.from(OCRJSONUtils.getObject(json, OCRNames.ERROR)))
			.setSkip(OCRJSONUtils.getInteger(json, OCRNames.SKIP))		
			.setLimit(OCRJSONUtils.getInteger(json, OCRNames.LIMIT)) 
			.setCount(OCRJSONUtils.getInteger(json, OCRNames.COUNT))
		;
	}
	
	public static JSONArray to(List<OCRCompanyResponse> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRCompanyResponse> stream) {
		return stream
			.map(OCRCompanyResponseJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRCompanyResponse response) {
		if (response == null) return null;
		;
		return new JSONObject()
			.putOpt(OCRNames.HTTP_CODE, response.getHttpCode().orElse(null))
			.putOpt(OCRNames.RESULT, response.getCompany().map( OCRCompanyJSON::to ).orElse(null))
			.putOpt(OCRNames.ERROR, response.getError().map(OCRErrorJSON::to).orElse(null))
			;
	}
}
