package net.aonsolutions.invofox.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.invofox.model.OCRBox;

public class OCRBoxJSON {
	
	private OCRBoxJSON() {
	}
	
	public static List<OCRBox> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCRBoxJSON::from)
			.toList();		
	}
	
	public static OCRBox from(JSONObject json) {
		if (json == null) return null; 
		return new OCRBox()
			.setPageIndex(OCRJSONUtils.getInteger(json, OCRNames.PAGE))
			.setCoordinates(OCRJSONUtils.getIntegerArray(json, OCRNames.BOX))
		;
	}
	
	public static JSONArray to(List<OCRBox> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRBox> stream) {
		return stream
			.map(OCRBoxJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRBox box) {
		if (box == null) return null;
		return new JSONObject()
			.putOpt(OCRNames.PAGE, box.getPageIndex().orElse(null))
			.putOpt(OCRNames.BOX, box.getCoordinates().orElse(null))
		;
	}
}
