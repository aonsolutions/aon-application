package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.ActivityType;
import com.esferalia.aon.occam.api.model.IJsonNames;

public class ActivityTypeJSON {

	private ActivityTypeJSON() {
	
	}
	
//	activity_type
	public static List<ActivityType> fromJSON(JSONArray json) {
		LinkedList<ActivityType> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static ActivityType fromJSON(JSONObject json) {
		if(json == null) return new ActivityType();
		return new ActivityType()
				.setId(JsonUtils.optInteger(json, IJsonNames.ID))
				.setDomain(JsonUtils.optInteger(json, IJsonNames.DOMAIN))
				.setDescription(JsonUtils.optString(json, IJsonNames.DESCRIPTION))
				.setProjectType(JsonUtils.optInteger(json, IJsonNames.PROJECT_TYPE))
				.setActive(json.optBoolean(IJsonNames.ACTIVE))
				.setDirty(json.optBoolean(IJsonNames.DIRTY));
	}
	
	public static JSONArray toJSON(List<ActivityType> projects) {
		return toJSON(projects.stream());
	}
	
	public static JSONArray toJSON(Stream<ActivityType> datas) {
		JSONArray array = new JSONArray();
		datas.forEach(d -> array.put(toJSON(d)));
		return array;
	}
	
	public static JSONObject toJSON(ActivityType d) {
		return new JSONObject()
				.put(IJsonNames.ID, d.getId())
				.put(IJsonNames.DOMAIN, d.getDomain())
				.put(IJsonNames.DESCRIPTION, d.getDescription())
				.put(IJsonNames.PROJECT_TYPE, d.getProjectType())
				.put(IJsonNames.ACTIVE, d.isActive())
				.put(IJsonNames.DIRTY, d.isDirty());
	}
}
