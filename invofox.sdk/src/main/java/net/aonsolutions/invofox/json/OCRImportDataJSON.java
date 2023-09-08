package net.aonsolutions.invofox.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.invofox.model.OCRImportData;

public class OCRImportDataJSON {
	
	private OCRImportDataJSON() {
	}
	
	public static List<OCRImportData> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCRImportDataJSON::from)
			.toList();		
	}
	
	public static OCRImportData from(JSONObject json) {
		if (json == null) return null; 
		return new OCRImportData()
			.setRef(OCRJSONUtils.getString(json, OCRNames.REF))
			.setChannel(OCRJSONUtils.getString(json, OCRNames.CHANNEL))
			.setInfo(OCRJSONUtils.getStringArray(json, OCRNames.INFO))
		;
		
	}
	
	public static JSONArray to(List<OCRImportData> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRImportData> stream) {
		return stream
			.map(OCRImportDataJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRImportData info) {
		if (info == null) return null;
		return new JSONObject()
			.putOpt(OCRNames.REF, info.getRef().orElse(null))
			.putOpt(OCRNames.CHANNEL, info.getChannel().orElse(null))
			.putOpt(OCRNames.INFO, info.getInfo().orElse(null))
		;
	}
}
