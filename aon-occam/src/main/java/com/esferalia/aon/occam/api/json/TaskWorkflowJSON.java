package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
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
			.setEmail(JsonUtils.optString(json, IJsonNames.EMAIL))
			.setTaskHolder(TaskHolderJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.TASK_HOLDER)))
			.setType(TaskWorkflowType.safeValueOf(JsonUtils.getString(json, IJsonNames.TYPE)))
			.setComment(JsonUtils.getString(json, IJsonNames.COMMENT))
			.setCreationDate(JsonUtils.getDate(json, IJsonNames.CREATION_DATE))
			.setCreationUser(JsonUtils.getString(json, IJsonNames.CREATION_USER))
			.setModificationDate(JsonUtils.getDate(json, IJsonNames.MODIFICATION_DATE))
			.setModificationUser(JsonUtils.getString(json, IJsonNames.MODIFICATION_USER))
			.setNotificationDate(JsonUtils.getDate(json, IJsonNames.NOTIFICATION_DATE))
			.setNotificationUser(JsonUtils.getString(json, IJsonNames.NOTIFICATION_USER))
			;
	}
	
	public static JSONArray toJSON(LinkedList<TaskWorkflow> taskWorkflows) {
		return toJSON(taskWorkflows.stream());
	}
	
	public static JSONArray toJSON(Stream<TaskWorkflow> taskWorkflows) {
		JSONArray array = new JSONArray();
		taskWorkflows.forEach(taskWorkflow -> array.put(toJSON(taskWorkflow)));
		return array;
	}
	
	public static JSONObject toJSON(TaskWorkflow workflow) {
		return new JSONObject()
			.put(IJsonNames.ID, workflow.getId())
			.put(IJsonNames.DOMAIN, workflow.getDomain())
			.put(IJsonNames.TASK_HOLDER, TaskHolderJSON.toJSON(workflow.getTaskHolder()))
			.put(IJsonNames.TYPE, workflow.getType().getName())
			.put(IJsonNames.COMMENT, workflow.getComment())
			.put(IJsonNames.EMAIL, workflow.getEmail())
			.put(IJsonNames.TASK, workflow.getTask())
			.put(IJsonNames.CREATION_DATE,  workflow.getCreationDate()!=null ? workflow.getCreationDate().getTime() : null)
			.put(IJsonNames.CREATION_USER, workflow.getCreationUser())
			.put(IJsonNames.MODIFICATION_DATE, workflow.getModificationDate()!=null ? workflow.getModificationDate().getTime(): null)
			.put(IJsonNames.MODIFICATION_USER, workflow.getModificationUser())
			.put("notification_date", workflow.getNotificationDate()!=null ? workflow.getNotificationDate().getTime(): null)
			.put("notification_user", workflow.getNotificationUser());	
	}
}
