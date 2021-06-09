package com.esferalia.aon.occam.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskHolderType;

public class TaskHolderJSON {
	
	public static TaskHolder fromJSON(JSONObject json) {
		return new TaskHolder()
				.copy(RegistryJSON.fromJSON(json))
				.setActive(JsonUtils.getBoolean(json, IJsonNames.ACTIVE))
				.setType(TaskHolderType.INTERNAL)
				.setUserId(JsonUtils.getInteger(json, IJsonNames.USER));
	}
	
	public static JSONObject toJSON(TaskHolder taskHolder) {
		JSONObject json = RegistryJSON.toJSON(taskHolder);
		return json.put(IJsonNames.ACTIVE, taskHolder.isActive())
				.put(IJsonNames.USER, taskHolder.getUserId());	
	}

}
