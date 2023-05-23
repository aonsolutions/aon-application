package net.aonsolutions.occam.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import net.aonsolutions.occam.api.AonNames;
import net.aonsolutions.occam.api.config.Activity;
import net.aonsolutions.watson.client.util.AonCollectionUtils;

public class ActivityJSON {
	
	private ActivityJSON() {
	}
	
	public static List<Activity> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return AonJSONUtils.stream(array)
			.map(ActivityJSON::from)
			.toList();		
	}
	
	public static Activity from(JSONObject json) {
		if (json == null) return null; 
		return new Activity()
			.setId(AonJSONUtils.getInteger(json, AonNames.ID))
			.setDomain(AonJSONUtils.getInteger(json, AonNames.DOMAIN))
			.setDescription(AonJSONUtils.getString(json, AonNames.DESCRIPTION))
			.setEpigraph(AonJSONUtils.getString(json, AonNames.EPIGRAPH))
		;
	}
	
	public static JSONArray to(List<Activity> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<Activity> stream) {
		return stream
			.map(ActivityJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(Activity account) {
		if (account == null) return null;
		return new JSONObject()
			.put(AonNames.ID, account.getId())
			.put(AonNames.DOMAIN, account.getDomain())
			.putOpt(AonNames.DESCRIPTION, account.getDescription())
			.putOpt(AonNames.EPIGRAPH, account.getEpigraph())
			;
	}
}
