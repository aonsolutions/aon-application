package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.task.TaskWorkflow;
import com.esferalia.aon.occam.api.model.task.TaskWorkflowType;

public class TaskWorkflowJSON {
	
	public static LinkedList<TaskWorkflow> fromJSON(JSONArray json) {
		LinkedList<TaskWorkflow> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static TaskWorkflow fromJSON(JSONObject json) {
		return new TaskWorkflow()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setTask(JsonUtils.getInteger(json, IJsonNames.TASK))
			.setTaskHolder(TaskHolderJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.TASK_HOLDER)))
			.setType(TaskWorkflowType.safeValueOf(JsonUtils.getString(json, IJsonNames.TYPE)))
			.setComment(JsonUtils.getString(json, IJsonNames.COMMENT));
	}
	
	public static JSONArray toJSON(LinkedList<TaskWorkflow> taskWorkflows) {
		return toJSON(taskWorkflows.stream());
	}
	
	public static JSONArray toJSON(Stream<TaskWorkflow> taskWorkflows) {
		JSONArray array = new JSONArray();
		taskWorkflows.forEach(taskWorkflow -> array.put(toJSON(taskWorkflow)));
		return array;
	}
	
	public static JSONObject toJSON(TaskWorkflow taskWorkflow) {
		return new JSONObject()
			.put(IJsonNames.ID, taskWorkflow.getId())
			.put(IJsonNames.DOMAIN, taskWorkflow.getDomain())
			.put(IJsonNames.TASK_HOLDER, TaskHolderJSON.toJSON(taskWorkflow.getTaskHolder()))
			.put(IJsonNames.TYPE, taskWorkflow.getType().getName())
			.put(IJsonNames.COMMENT, taskWorkflow.getComment())
			.put(IJsonNames.CREATION_DATE,  taskWorkflow.getCreationDate()!=null ? taskWorkflow.getCreationDate().getTime() : null)
			.put(IJsonNames.CREATION_USER, taskWorkflow.getCreationUser())
			.put(IJsonNames.MODIFICATION_DATE, taskWorkflow.getModificationDate()!=null ? taskWorkflow.getModificationDate().getTime(): null)
			.put(IJsonNames.MODIFICATION_USER, taskWorkflow.getModificationUser());	
	}
}
