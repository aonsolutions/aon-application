package net.aonsolutions.invofox.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.invofox.model.OCRS3Object;

public class OCRS3ObjectJSON {
	
	private OCRS3ObjectJSON() {
	}
	
	public static List<OCRS3Object> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCRS3ObjectJSON::from)
			.toList();		
	}
	
	public static OCRS3Object from(JSONObject json) {
		if (json == null) return null; 
		return new OCRS3Object()
			.setBucket(OCRJSONUtils.getString(json, OCRNames.BUCKET))
			.setDomain(OCRJSONUtils.getString(json, OCRNames.DOMAIN))
			.setUser(OCRJSONUtils.getString(json, OCRNames.USER))
			.setKey(OCRJSONUtils.getString(json, OCRNames.KEY))
		;
	}
	
	public static JSONArray to(List<OCRS3Object> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRS3Object> stream) {
		return stream
			.map(OCRS3ObjectJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRS3Object s3Object) {
		if (s3Object == null) return null;
		return new JSONObject()
			.putOpt(OCRNames.DOMAIN, s3Object.getDomain().orElse(null))
			.putOpt(OCRNames.KEY, s3Object.getKey().orElse(null))
			.putOpt(OCRNames.USER, s3Object.getUser().orElse(null))
			.putOpt(OCRNames.BUCKET, s3Object.getBucket().orElse(null))
		;
	}
}
