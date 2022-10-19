package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.project.ProjectHolder;

public class ProjectHolderJSON {

	private ProjectHolderJSON() {

	}

	public static List<ProjectHolder> fromJSON(JSONArray json) {
		LinkedList<ProjectHolder> list = new LinkedList<>();
		if(json==null) return list;
		for (Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
		return list;
	}

	public static ProjectHolder fromJSON(JSONObject json) {
		if(json == null) return new ProjectHolder();
		return new ProjectHolder().setId(JsonUtils.optInteger(json, IJsonNames.ID))
				.setDomain(JsonUtils.optInteger(json, IJsonNames.DOMAIN))
				.setStartDate(JsonUtils.getDate(json, IJsonNames.START_DATE))
				.setEndDate(JsonUtils.getDate(json, IJsonNames.END_DATE))
				.setProject(JsonUtils.optInteger(json, IJsonNames.PROJECT))
				.setWorkgroup(WorkgroupJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.WORKGROUP)))
				.setTaskHolder(TaskHolderJSON.fromJSON(JsonUtils.getJSONObject(json, "taskHolder")))
				.setDirty(JsonUtils.getboolean(json, IJsonNames.DIRTY));
	}

	public static JSONArray toJSON(List<ProjectHolder> projectHolders) {
		return toJSON(projectHolders.stream());
	}

	public static JSONArray toJSON(Stream<ProjectHolder> projectHolders) {
		JSONArray array = new JSONArray();
		projectHolders.forEach(projectHolder -> array.put(toJSON(projectHolder)));
		return array;
	}

	public static JSONObject toJSON(ProjectHolder projectHolder) {
		if (projectHolder.isEmpty())
			return new JSONObject();
		return new JSONObject().put(IJsonNames.ID, projectHolder.getId())
				.put(IJsonNames.DOMAIN, projectHolder.getDomain())
				.put(IJsonNames.PROJECT, projectHolder.getProject())
				.put(IJsonNames.START_DATE, projectHolder.getStartDate())
				.put(IJsonNames.END_DATE, projectHolder.getEndDate())
				.put(IJsonNames.WORKGROUP, WorkgroupJSON.toJSON(projectHolder.getWorkgroup()))
				//.put(IJsonNames.TASK_HOLDER, TaskHolderJSON.toJSON(projectHolder.getTaskHolder()));
				.put("taskHolder", TaskHolderJSON.toJSON(projectHolder.getTaskHolder()))
				.put(IJsonNames.DIRTY, projectHolder.isDirty());

	}
}
