package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.project.ProjectActivity;

public class ProjectActivityJSON {

	private ProjectActivityJSON() {

	}

	public static List<ProjectActivity> fromJSON(JSONArray json) {
		LinkedList<ProjectActivity> list = new LinkedList<>();
		if(json==null) return list;
		for (Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
		return list;
	}

	public static ProjectActivity fromJSON(JSONObject json) {
		if(json == null) return new ProjectActivity();
		return new ProjectActivity().setId(JsonUtils.optInteger(json, IJsonNames.ID))
				.setDomain(JsonUtils.optInteger(json, IJsonNames.DOMAIN))
				.setProject(JsonUtils.optInteger(json, IJsonNames.PROJECT))
				.setActivityType(JsonUtils.optInteger(json, "activityType"))
				.setActive(JsonUtils.getboolean(json, IJsonNames.ACTIVE))
				.setDirty(JsonUtils.getboolean(json, IJsonNames.DIRTY))
				.setRemoved(JsonUtils.getboolean(json, IJsonNames.REMOVED));
	}

	public static JSONArray toJSON(List<ProjectActivity> projectActivities) {
		return toJSON(projectActivities.stream());
	}

	public static JSONArray toJSON(Stream<ProjectActivity> projectActivities) {
		JSONArray array = new JSONArray();
		projectActivities.forEach(projectActivity -> array.put(toJSON(projectActivity)));
		return array;
	}

	public static JSONObject toJSON(ProjectActivity projectActivity) {
		return new JSONObject().put(IJsonNames.ID, projectActivity.getId())
				.put(IJsonNames.DOMAIN, projectActivity.getDomain())
				.put(IJsonNames.PROJECT, projectActivity.getProject())
				.put("activityType", projectActivity.getActivityType())
				.put(IJsonNames.ACTIVE, projectActivity.isActive())
				.put(IJsonNames.DIRTY, projectActivity.isDirty());

	}
}
