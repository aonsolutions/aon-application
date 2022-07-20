package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
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
			.setSource(TaskSource.safeValueOf(JsonUtils.optString(json, IJsonNames.SOURCE)))
			.setSourceId(JsonUtils.getInteger(json, IJsonNames.SOURCE_ID))
			.setStatus( TaskStatus.safeValueOf(JsonUtils.optString(json, IJsonNames.STATUS))) 
			.setRegistry(RegistryJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.REGISTRY)))
			.setWorkgroup(WorkgroupJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.WORKGROUP)))
			.setWorkflows(TaskWorkflowJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.WORKFLOW)))
			.setGtaskId(JsonUtils.getString(json, "gtask_id"))
			.setTags( TagJSON.fromJSON(JsonUtils.getJSONArray(json, "tags")) )
			.setChilds( TaskJSON.fromJSON(JsonUtils.getJSONArray(json, "childs")) )
			.setRepeatPeriod(TaskPeriod.NONE) // TODO
			;
	}
	
	public static JSONArray toJSON(LinkedList<Task> tasks) {
		return toJSON(tasks.stream());
	}
	
	public static JSONArray toJSON(List<Task> tasks) {
		JSONArray array = new JSONArray();
		tasks.forEach(t -> array.put(toJSON(t)));
		return array;
	}
	
	public static JSONArray toJSON(Stream<Task> tasks) {
		JSONArray array = new JSONArray();
		tasks.forEach(t -> array.put(toJSON(t)));
		return array;
	}
	
	public static JSONObject toJSON(Task task) {
		if(task==null) return new JSONObject();
		return new JSONObject()
			.put(IJsonNames.ID, task.getId())
			.put(IJsonNames.DOMAIN, DomainJSON.toJSON(task.getDomain()))
			.put(IJsonNames.DESCRIPTION, task.getDescription())
			.put(IJsonNames.TITLE, task.getTitle())
			.put(IJsonNames.NUMBER, task.getNumber())
			.put(IJsonNames.PERCENT, task.getPercent())
			.put(IJsonNames.STATUS,  task.getStatus()!=null ? task.getStatus().getName() : null)
			.put(IJsonNames.PRIORITY, task.getPriority()!=null ? task.getPriority().getName() : null)
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
			.put(IJsonNames.PARENT, task.getParent())
			.put(IJsonNames.CREATION_USER, task.getCreationUser())
			.put(IJsonNames.CREATION_DATE, task.getCreationDate()!=null ? task.getCreationDate().getTime() : null)
			.put("gtask_id", task.getGtaskId().isPresent() ? task.getGtaskId().get() : null)
			.put("evaluation", task.getEvaluation()!=null ? task.getEvaluation().getName() : null)
			.put("tags", TagJSON.toJSON(task.getTags()))
			.put("childs", toJSON(task.getChilds()))
//			.put(IJsonNames.MODIFICATION_USER, task.getModificationUser())
//			.put(IJsonNames.MODIFICATION_DATE, task.getModificationDate())
			;
	}

}
