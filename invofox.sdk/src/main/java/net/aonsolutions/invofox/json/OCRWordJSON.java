package net.aonsolutions.invofox.json;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.invofox.model.OCRWord;

public class OCRWordJSON {
	
	private OCRWordJSON() {
	}
	
	public static OCRWord[] from(JSONArray array) {
		if (array == null || array.isEmpty()) return new OCRWord[] {};
		return OCRJSONUtils.stream(array)
			.map(OCRWordJSON::from)
			.toArray( OCRWord[]::new );		
	}
	
	public static OCRWord from(JSONObject json) {
		if (json == null) return null; 
		return new OCRWord()
			.setText(OCRJSONUtils.getString(json, OCRNames.TEXT))
			.setBoundingBox(OCRJSONUtils.getBigDecimalArray(json, OCRNames.BOUNDING_BOX))
		;
	}
	
	public static JSONArray to(OCRWord [] arr) {
		if (arr == null || arr.length == 0) return new JSONArray();
		return to(Arrays.stream(arr));
	}

	public static JSONArray to(List<OCRWord> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}

	public static JSONArray to(Stream<OCRWord> stream) {
		return stream
			.map(OCRWordJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRWord word) {
		if (word == null) return null;
		return new JSONObject()
			.putOpt(OCRNames.TEXT, word.getText().orElse(null))
			.putOpt(OCRNames.CONFIDENCE, word.getConfidence().orElse(null))
			.putOpt(OCRNames.BOUNDING_BOX, word.getBoundingBox().orElse(null))
		;
	}
}
