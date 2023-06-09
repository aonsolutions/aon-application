package net.aonsolutions.infovox.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import net.aonsolutions.infovox.model.OCRApprovalInfo;
import net.aonsolutions.infovox.model.OCRSeverity;
import net.aonsolutions.watson.client.util.AonCollectionUtils;

public class OCRApprovalInfoJSON {
	
	private OCRApprovalInfoJSON() {
	}
	
	public static List<OCRApprovalInfo> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCRApprovalInfoJSON::from)
			.toList();		
	}
	
	public static OCRApprovalInfo from(JSONObject json) {
		if (json == null) return null; 
		return new OCRApprovalInfo()
			.setBeginning(OCRJSONUtils.getString(json, OCRNames.BEGINNING))
			.setResult( OCRSeverity.safeValueOf(OCRJSONUtils.getString(json, OCRNames.RESULT)).orElse(null) )
			.setComments(OCRJSONUtils.getString(json, OCRNames.COMMENTS))
			.setAppliedWorkflow(OCRJSONUtils.getString(json, OCRNames.APPLIED_WORKFLOW))
			.setSteps(OCRJSONUtils.getStringArray(json, OCRNames.STEPS))
		;
	}
	
	public static JSONArray to(List<OCRApprovalInfo> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRApprovalInfo> stream) {
		return stream
			.map(OCRApprovalInfoJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRApprovalInfo info) {
		if (info == null) return null;
		return new JSONObject()
			.putOpt(OCRNames.BEGINNING, info.getBeginning().orElse(null))
			.putOpt(OCRNames.RESULT, info.getResult().orElse(null))
			.putOpt(OCRNames.COMMENTS, info.getComments().orElse(null))
			.putOpt(OCRNames.APPLIED_WORKFLOW, info.getAppliedWorkflow().orElse(null))
			.putOpt(OCRNames.STEPS, info.getSteps().orElse(null))
		;
	}
}
