package net.aonsolutions.infovox.json;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.infovox.model.OCRBox;
import net.aonsolutions.infovox.model.OCRGeometry;

public class OCRGeometryJSON {
	
	private OCRGeometryJSON() {
	}
	
	public static List<OCRGeometry> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCRGeometryJSON::from)
			.toList();		
	}
	
	public static OCRGeometry from(JSONObject json) {
		if (json == null) return null;
		OCRGeometry geometry = new OCRGeometry()
			.setPages(OCRPageJSON.from(OCRJSONUtils.getArray(json, OCRNames.PAGES)))
		;
		JSONObject boxes = OCRJSONUtils.getObject(json, OCRNames.BOXES);
		if (boxes != null && boxes.length() > 0) {
			geometry.setBoxes(new HashMap<>());
			boxes.keySet()
				.stream()
				.forEach( k -> geometry.getBoxes().get().put(k,OCRBoxJSON.from(boxes.getJSONArray(k))));
		}
		
		return geometry;
	}
	
	public static JSONArray to(List<OCRGeometry> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRGeometry> stream) {
		return stream
			.map(OCRGeometryJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRGeometry geometry) {
		if (geometry == null) return null;
		JSONObject geoJson = new JSONObject()
			.putOpt(OCRNames.PAGES, OCRPageJSON.to(geometry.getPages().orElse(null)))
			;
		Optional<Map<String, List<OCRBox>>> optBoxes = geometry.getBoxes();
		if ( optBoxes.isPresent() && optBoxes.get().size() > 0) {
			optBoxes.get().entrySet().stream()
				.forEach(e -> geoJson.putOpt(e.getKey(), OCRBoxJSON.to(e.getValue())));
		}
		return geoJson;
	}
}
