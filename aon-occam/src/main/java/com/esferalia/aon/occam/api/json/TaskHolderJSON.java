package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskHolderType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;

public class TaskHolderJSON {
	
	private TaskHolderJSON() {
	
	}
	
	public static List<TaskHolder> fromJSON(JSONArray json) {
		LinkedList<TaskHolder> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static TaskHolder fromJSON(JSONObject json) {
		if(json.isEmpty()) return new TaskHolder();
		return new TaskHolder()
				.copy(RegistryJSON.fromJSON(json))
				.setActive(JsonUtils.getBoolean(json, IJsonNames.ACTIVE))
				.setType(TaskHolderType.INTERNAL)
				.setUserId(JsonUtils.getInteger(json, IJsonNames.USER))
				.setStatus(!json.isNull(IJsonNames.STATUS) ? RegistryStatus.safeValueOf(JsonUtils.getString(json, IJsonNames.STATUS)) : null)
				;
	}
	
	public static JSONArray toJSON(List<TaskHolder> taskHolders) {
		return toJSON(taskHolders.stream());
	}
	
	public static JSONArray toJSON(Stream<TaskHolder> taskHolders) {
		JSONArray array = new JSONArray();
		taskHolders.forEach(taskHolder -> array.put(toJSON(taskHolder)));
		return array;
	}
	
	public static JSONObject toJSON(TaskHolder taskHolder) {
		return RegistryJSON.toJSON(taskHolder)
		.put(IJsonNames.ACTIVE, taskHolder.isActive())
		.put(IJsonNames.USER, taskHolder.getUserId())
		.put(IJsonNames.STATUS, taskHolder.getStatus().name())
		;	
	}

}
