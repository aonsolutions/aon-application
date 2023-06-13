package net.aonsolutions.infovox.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import net.aonsolutions.infovox.model.OCRPage;
import net.aonsolutions.infovox.model.OCRUnit;
import net.aonsolutions.watson.client.util.AonCollectionUtils;

public class OCRPageJSON {
	
	private OCRPageJSON() {
	}
	
	public static List<OCRPage> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCRPageJSON::from)
			.toList();		
	}
	
	public static OCRPage from(JSONObject json) {
		if (json == null) return null; 
		return new OCRPage()
			.setWidth(OCRJSONUtils.getNumber(json, OCRNames.WIDTH))
			.setHeight(OCRJSONUtils.getNumber(json, OCRNames.HEIGHT))
			.setUnit( OCRUnit.safeValueOf(OCRJSONUtils.getString(json, OCRNames.UNIT)).orElse(null) )
			.setAngle(OCRJSONUtils.getNumber(json, OCRNames.ANGLE))
		;
	}
	
	public static JSONArray to(List<OCRPage> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRPage> stream) {
		return stream
			.map(OCRPageJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRPage page) {
		if (page == null) return null;
		return new JSONObject()
			.putOpt(OCRNames.WIDTH, page.getWidth().orElse(null))
			.putOpt(OCRNames.HEIGHT, page.getHeight().orElse(null))
			.putOpt(OCRNames.UNIT, page.getUnit().orElse(null))
			.putOpt(OCRNames.ANGLE, page.getAngle().orElse(null))
		;
	}
}
