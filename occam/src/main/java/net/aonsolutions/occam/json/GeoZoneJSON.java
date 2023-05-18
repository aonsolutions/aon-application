package net.aonsolutions.occam.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import net.aonsolutions.occam.api.AonNames;
import net.aonsolutions.occam.api.config.GeoZone;
import net.aonsolutions.watson.client.util.AonCollectionUtils;

public class GeoZoneJSON {
	
	private GeoZoneJSON() {
	}
	
	public static List<GeoZone> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return AonJSONUtils.stream(array)
			.map(GeoZoneJSON::from)
			.toList();		
	}
	
	public static GeoZone from(JSONObject json) {
		if (json == null) return null;
		return new GeoZone()
			.setId(AonJSONUtils.getInteger(json, AonNames.ID))
			.setDomain(AonJSONUtils.getInteger(json, AonNames.DOMAIN))
			.setCode(AonJSONUtils.getString(json, AonNames.CODE))
			.setName(AonJSONUtils.getString(json, AonNames.NAME))
			.setSystem(AonJSONUtils.getBoolean(json, AonNames.SYSTEM))
		;
	}
	
	public static JSONArray to(List<GeoZone> list) {
		if (AonCollectionUtils.isEmpty(list)) return null;
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<GeoZone> stream) {
		return stream
			.map(GeoZoneJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(GeoZone geozone) {
		if (geozone == null) return null;
		return new JSONObject()
			.put(AonNames.ID, geozone.getId())
			.put(AonNames.DOMAIN, geozone.getDomain())
			.put(AonNames.CODE, geozone.getCode())
			.put(AonNames.NAME, geozone.getName())
			.put(AonNames.SYSTEM, geozone.isSystem())
			;
	}
}
