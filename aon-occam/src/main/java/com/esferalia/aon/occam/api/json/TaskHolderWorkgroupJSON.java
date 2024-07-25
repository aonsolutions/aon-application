package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.security.TaskHolderWorkgroup;
import com.esferalia.aon.occam.api.model.task.TaskHolderWorkgroupType;
import com.esferalia.aon.watson.server.AonDateUtils;

public class TaskHolderWorkgroupJSON {
	
	private TaskHolderWorkgroupJSON() {
	
	}
	
	public static List<TaskHolderWorkgroup> fromJSON(JSONArray json) {
		LinkedList<TaskHolderWorkgroup> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static TaskHolderWorkgroup fromJSON(JSONObject json) {
		if(json == null || json.isEmpty()) return new TaskHolderWorkgroup();
		return new TaskHolderWorkgroup()
				.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
				.setTaskHolder(TaskHolderJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.TASK_HOLDER)))
				.setWorkgroup(WorkgroupJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.WORKGROUP)))
				.setTaskHolderWorkgroupType(TaskHolderWorkgroupType.safeValueOf(JsonUtils.getString(json, "task_holder_workgroup_type")))
				.setStartDate(AonDateUtils.parse(JsonUtils.getString(json, IJsonNames.START_DATE), "yyyy-MM-dd"))
				.setEndDate(AonDateUtils.parse(JsonUtils.getString(json, IJsonNames.END_DATE), "yyyy-MM-dd"))
				;
	}
	
	public static JSONArray toJSON(List<TaskHolderWorkgroup> taskHolderWorkgroups) {
		return toJSON(taskHolderWorkgroups.stream());
	}
	
	public static JSONArray toJSON(Stream<TaskHolderWorkgroup> taskHolderWorkgroups) {
		JSONArray array = new JSONArray();
		taskHolderWorkgroups.forEach(taskHolderWorkgroup -> array.put(toJSON(taskHolderWorkgroup)));
		return array;
	}
	
	public static JSONObject toJSON(TaskHolderWorkgroup taskHolderWorkgroup) {
		return new JSONObject()
		.put(IJsonNames.DOMAIN, taskHolderWorkgroup.getDomain())
		.put(IJsonNames.TASK_HOLDER, TaskHolderJSON.toJSON(taskHolderWorkgroup.getTaskHolderObj()))
		.put(IJsonNames.WORKGROUP, WorkgroupJSON.toJSON(taskHolderWorkgroup.getWorkgroup()))
		.put("task_holder_workgroup_type", taskHolderWorkgroup.getTaskHolderWorkgroupType())
		.put(IJsonNames.START_DATE, taskHolderWorkgroup.getStartDate())
		.put(IJsonNames.END_DATE, taskHolderWorkgroup.getEndDate())
		;	
	}

}
