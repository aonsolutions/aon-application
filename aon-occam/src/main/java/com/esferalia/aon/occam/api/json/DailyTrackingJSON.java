package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.task.DailyTracking;

public class DailyTrackingJSON {
	
	public static List<DailyTracking> fromJSON(JSONArray json) {
		LinkedList<DailyTracking> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static DailyTracking fromJSON(JSONObject json) {
		return new DailyTracking()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(DomainJSON.fromJSON(json.optJSONObject(IJsonNames.DOMAIN)))
			.setTaskHolder(TaskHolderJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.TASK_HOLDER)))
			.setTrackingDate(JsonUtils.getDate(json, "tracking_date"))
			.setTrackingDuration(JsonUtils.getDouble(json, "tracking_duration"))
			.setJobType(JsonUtils.getInteger(json, "job_type"))
			.setRegistry(RegistryJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.REGISTRY)))
			.setProject(ProjectJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.PROJECT)))
			.setActivityType(JsonUtils.getInteger(json, IJsonNames.ACTIVITY_TYPE))
			.setComments(JsonUtils.getString(json, IJsonNames.COMMENTS))
			.setTask(JsonUtils.getInteger(json, IJsonNames.TASK))
			.setCost(JsonUtils.getDouble(json, "cost"))
			;
	}
	
	public static JSONArray toJSON(LinkedList<DailyTracking> dts) {
		return toJSON(dts.stream());
	}
	
	public static JSONArray toJSON(List<DailyTracking> dts) {
		return toJSON(dts.stream());
	}
	
	public static JSONArray toJSON(Stream<DailyTracking> dts) {
		JSONArray array = new JSONArray();
		dts.forEach(dt -> array.put(toJSON(dt)));
		return array;
	}
	
	public static JSONObject toJSON(DailyTracking dt) {
		JSONObject json = new JSONObject();
		if(dt!=null && dt.getId()!=null) {	
			json
			.put(IJsonNames.ID, dt.getId())
			.put(IJsonNames.DOMAIN, DomainJSON.toJSON(dt.getDomain()))
			.put(IJsonNames.TASK_HOLDER, TaskHolderJSON.toJSON(dt.getTaskHolder()))
			.put(IJsonNames.REGISTRY, dt.getRegistry()!=null ? RegistryJSON.toJSON(dt.getRegistry()) : null)
			.put(IJsonNames.PROJECT, dt.getProject()!=null ? ProjectJSON.toJSON(dt.getProject()): null)
			.put(IJsonNames.ACTIVITY_TYPE, dt.getActivityType())
			.put(IJsonNames.COMMENTS, dt.getComments())
			.put(IJsonNames.TASK, dt.getTask())
			.put("tracking_date", dt.getTrackingDate().getTime())
			.put("tracking_duration", dt.getTrackingDuration())
			.put("job_type", dt.getJobType())
			.put("cost", dt.getCost())
			;
		}
		return json;
	}

}
