package net.aonsolutions.infovox.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import net.aonsolutions.infovox.model.OCRField;
import net.aonsolutions.watson.client.util.AonCollectionUtils;

public class OCRFieldJSON {
	
	private OCRFieldJSON() {
	}
	
	public static List<OCRField> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCRFieldJSON::from)
			.toList();		
	}
	
	public static OCRField from(JSONObject json) {
		if (json == null) return null; 
		return new OCRField()
			.setName(OCRJSONUtils.getString(json, OCRNames.NAME))
			.setPrefix(OCRJSONUtils.getString(json, OCRNames.PREFIX))
			.setIndex(OCRJSONUtils.getInteger(json, OCRNames.INDEX))
			.setGroupIndex(OCRJSONUtils.getInteger(json, OCRNames.GROUP_INDEX))
			.setSplitIndex(OCRJSONUtils.getInteger(json, OCRNames.SPLIT_INDEX))
		;
	}
	
	public static JSONArray to(List<OCRField> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRField> stream) {
		return stream
			.map(OCRFieldJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRField field) {
		if (field == null) return null;
		return new JSONObject()
			.putOpt(OCRNames.NAME, field.getName())
			.putOpt(OCRNames.PREFIX, field.getPrefix())
			.putOpt(OCRNames.INDEX, field.getIndex())
			.putOpt(OCRNames.GROUP_INDEX, field.getGroupIndex())
			.putOpt(OCRNames.SPLIT_INDEX, field.getSplitIndex())
		;
	}
}
