package net.aonsolutions.invofox.json;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.invofox.model.OCRLine;

public class OCRLineJSON {
	
	private OCRLineJSON() {
	}
	
	public static OCRLine[] from(JSONArray array) {
		if (array == null || array.isEmpty()) return new OCRLine[0]; 
		return OCRJSONUtils.stream(array)
			.map(OCRLineJSON::from)
			.toArray( OCRLine[]::new);		
	}
	
	public static OCRLine from(JSONObject json) {
		if (json == null) return null; 
		return new OCRLine()
			.setText(OCRJSONUtils.getString(json, OCRNames.TEXT))
			.setWords(OCRWordJSON.from(OCRJSONUtils.getArray(json, OCRNames.WORDS)))
			.setBoundingBox(OCRJSONUtils.getBigDecimalArray(json, OCRNames.BOUNDING_BOX))
		;
	}
	
	public static JSONArray to(OCRLine[] arr) {
		if (arr == null || arr.length == 0 ) return new JSONArray();
		return to(Arrays.stream(arr));
	}

	public static JSONArray to(List<OCRLine> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRLine> stream) {
		return stream
			.map(OCRLineJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRLine line) {
		if (line == null) return null;
		return new JSONObject()
			.putOpt(OCRNames.TEXT, line.getText().orElse(null))
			.putOpt(OCRNames.WORDS, line.getWords().orElse(null))
			.putOpt(OCRNames.BOUNDING_BOX, line.getBoundingBox().orElse(null))
		;
	}
}
