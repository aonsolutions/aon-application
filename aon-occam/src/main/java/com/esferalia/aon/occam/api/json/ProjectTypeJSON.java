package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.project.ProjectType;

public class ProjectTypeJSON {

	private ProjectTypeJSON() {
	
	}
	
	public static List<ProjectType> fromJSON(JSONArray json) {
		LinkedList<ProjectType> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static ProjectType fromJSON(JSONObject json) {
		if(json == null) return new ProjectType();
		return new ProjectType()
				.setId(JsonUtils.optInteger(json, IJsonNames.ID))
				.setDomain(JsonUtils.optInteger(json, IJsonNames.DOMAIN))
				.setDescription(JsonUtils.optString(json, IJsonNames.DESCRIPTION))
				.setActive(json.optBoolean(IJsonNames.ACTIVE));
	}
	
	public static JSONArray toJSON(List<ProjectType> projects) {
		return toJSON(projects.stream());
	}
	
	public static JSONArray toJSON(Stream<ProjectType> projects) {
		JSONArray array = new JSONArray();
		projects.forEach(project -> array.put(toJSON(project)));
		return array;
	}
	
	
	public static JSONObject toJSON(ProjectType projectType) {
		return new JSONObject()
				.put(IJsonNames.ID, projectType.getId())
				.put(IJsonNames.DOMAIN, projectType.getDomain())
				.put(IJsonNames.DESCRIPTION, projectType.getDescription())
				.put(IJsonNames.ACTIVE, projectType.isActive());
	}
}
