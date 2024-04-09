package net.aonsolutions.invofox.json;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.invofox.model.OCRPage;
import net.aonsolutions.invofox.model.OCRUnit;

public class OCRPageJSON {
	
	private OCRPageJSON() {
	}
	
	public static OCRPage[] from(JSONArray array) {
		if (array == null || array.isEmpty()) return new OCRPage[0];
		return OCRJSONUtils.stream(array)
			.map(OCRPageJSON::from)
			.toArray(OCRPage[]::new);		
	}
	
	public static OCRPage from(JSONObject json) {
		if (json == null) return null; 
		return new OCRPage()
			.setPage(OCRJSONUtils.getInteger(json, OCRNames.PAGE))
			.setWidth(OCRJSONUtils.getNumber(json, OCRNames.WIDTH))
			.setHeight(OCRJSONUtils.getNumber(json, OCRNames.HEIGHT))
			.setAngle(OCRJSONUtils.getNumber(json, OCRNames.ANGLE))
			.setLines(OCRLineJSON.from(OCRJSONUtils.getArray(json, OCRNames.LINES)))
			.setUnit( OCRUnit.safeValueOf(OCRJSONUtils.getString(json, OCRNames.UNIT)).orElse(null) )
		;
	}
	
	public static JSONArray to(OCRPage[] arr) {
		if (arr == null || arr.length == 0) return new JSONArray();
		return to(Arrays.stream(arr));
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
