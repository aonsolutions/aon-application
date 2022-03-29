package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskPeriod;
import com.esferalia.aon.occam.api.model.task.TaskSource;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.api.model.type.Priority;

public class TaskJSON {
	
	public static LinkedList<Task> fromJSON(JSONArray json) {
		LinkedList<Task> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Task fromJSON(JSONObject json) {
		return new Task()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(DomainJSON.fromJSON(json.optJSONObject(IJsonNames.DOMAIN)))
//			.setActivityType(JsonUtils.getInteger(json, IJsonNames.ACTIVITY_TYPE))
			.setTitle(JsonUtils.getString(json, IJsonNames.TITLE))
			.setDescription(JsonUtils.getString(json, IJsonNames.DESCRIPTION))
			.setDueDate(JsonUtils.getDate(json, IJsonNames.DUE_DATE))
			.setStartDate(JsonUtils.getDateTime(json, IJsonNames.START_DATE))
			.setEndDate(JsonUtils.getDate(json, IJsonNames.END_DATE))
			.setNumber(JsonUtils.getInteger(json, IJsonNames.NUMBER))
			.setParent(JsonUtils.getInteger(json, IJsonNames.PARENT))
			.setPercent(JsonUtils.getByte(json, IJsonNames.PERCENT))
			.setPriority(Priority.safeValueOf(json.optString(IJsonNames.PRIORITY)))
			.setSender(TaskHolderJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.SENDER)))
			.setTaskHolder(TaskHolderJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.TASK_HOLDER)))
			.setProject(ProjectJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.PROJECT)))
			.setRepeatPeriod(TaskPeriod.NONE) // TODO
			.setSource(TaskSource.safeValueOf(JsonUtils.optString(json, IJsonNames.SOURCE)))
			.setSourceId(JsonUtils.getInteger(json, "source_id"))
			.setStatus( TaskStatus.safeValueOf(JsonUtils.optString(json, "status"))) 
			.setRegistry(RegistryJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.REGISTRY)))
			.setWorkgroup(WorkgroupJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.WORKGROUP)))
			.setWorkflows(TaskWorkflowJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.WORKFLOW)))
			.setGtaskId(JsonUtils.getString(json, "gtask_id"))
			.setIsCau(!json.optString("cau").isEmpty() && json.optInt("cau") > 0) 
			;
	}
	
	public static JSONArray toJSON(LinkedList<Task> tasks) {
		return toJSON(tasks.stream());
	}
	
	public static JSONArray toJSON(Stream<Task> tasks) {
		JSONArray array = new JSONArray();
		tasks.forEach(task -> array.put(toJSON(task)));
		return array;
	}
	
	public static JSONObject toJSON(Task task) {
		return new JSONObject()
			.put(IJsonNames.ID, task.getId())
			.put(IJsonNames.DOMAIN, DomainJSON.toJSON(task.getDomain()))
			.put(IJsonNames.DESCRIPTION, task.getDescription())
			.put(IJsonNames.TITLE, task.getTitle())
			.put(IJsonNames.NUMBER, task.getNumber())
//			.put(IJsonNames.ACTIVITY_TYPE, task.getActivityType())
			.put(IJsonNames.PRIORITY, task.getPriority().getName())
			.put(IJsonNames.STATUS, task.getStatus().getName())
			.put(IJsonNames.PERCENT, task.getPercent())
			.put(IJsonNames.SENDER, TaskHolderJSON.toJSON(task.getSender()))
			.put(IJsonNames.TASK_HOLDER, TaskHolderJSON.toJSON(task.getTaskHolder()))
			.put(IJsonNames.REGISTRY, task.getRegistry()!=null  ? RegistryJSON.toJSON(task.getRegistry()) : null)
			.put(IJsonNames.WORKGROUP, WorkgroupJSON.toJSON(task.getWorkgroup()))
			.put(IJsonNames.SOURCE, task.getSource()!=null ? task.getSource().getName() : null )
			.put(IJsonNames.SOURCE_ID, task.getSourceId())
			.put(IJsonNames.PROJECT,  task.getProject()!=null ?  ProjectJSON.toJSON(task.getProject()): null)
			.put(IJsonNames.PERIOD, task.getRepeatPeriod()!=null ? task.getRepeatPeriod().getValue(): null)
			.put(IJsonNames.DUE_DATE, task.getDueDate()!=null ?  task.getDueDate().getTime() : null)
			.put(IJsonNames.START_DATE, task.getStartDate()!=null ?  task.getStartDate().getTime() : null)
			.put(IJsonNames.END_DATE, task.getEndDate()!=null ?  task.getEndDate().getTime() : null)
			.put("gtask_id", task.getGtaskId())
			.put(IJsonNames.PARENT, task.getParent())
//			.put(IJsonNames.CREATION_USER, task.getCreationUser())
//			.put(IJsonNames.CREATION_DATE, task.getCreationDate())
//			.put(IJsonNames.MODIFICATION_USER, task.getModificationUser())
//			.put(IJsonNames.MODIFICATION_DATE, task.getModificationDate())
			;
	}

}
