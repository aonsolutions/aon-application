package com.esferalia.aon.occam.api.json;

import java.util.Base64;
import java.util.LinkedList;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.task.TaskAttach;
import com.esferalia.aon.occam.api.model.type.MimeType;

public class TaskAttachJSON {
	
	public static LinkedList<TaskAttach> fromJSON(JSONArray json) {
		LinkedList<TaskAttach> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static TaskAttach fromJSON(JSONObject json) {
		return new TaskAttach()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setTask(JsonUtils.getInteger(json, IJsonNames.TASK))
			.setTaskWorkflow(JsonUtils.getInteger(json, IJsonNames.WORKFLOW))
			.setMimetype(MimeType.get(JsonUtils.getString(json, IJsonNames.CONTENT_TYPE)))
			.setData( Base64.getDecoder().decode(JsonUtils.getString(json, "content")));
	}
	
	public static JSONArray toJSON(LinkedList<TaskAttach> tasks) {
		return toJSON(tasks.stream());
	}
	
	public static JSONArray toJSON(Stream<TaskAttach> tasks) {
		JSONArray array = new JSONArray();
		tasks.forEach(task -> array.put(toJSON(task)));
		return array;
	}
	
	public static JSONObject toJSON(TaskAttach task) {
		return new JSONObject()
			.put(IJsonNames.ID, task.getId())
			.put(IJsonNames.DOMAIN, task.getDomain())
			.put(IJsonNames.TASK, task.getTask())
			.put(IJsonNames.WORKFLOW, task.getTaskWorkflow())
			.put(IJsonNames.CONTENT_TYPE, task.getMimetype()!=null ? task.getMimetype().getName(): null)
			.put("content", task.getData()!=null ? Base64.getEncoder().encodeToString(task.getData()) : null );	
	}
}
