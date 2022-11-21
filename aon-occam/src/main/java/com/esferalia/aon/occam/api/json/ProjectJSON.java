package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.registry.Project;

public class ProjectJSON {

	private ProjectJSON() {
	
	}
	
	
	public static List<Project> fromJSON(JSONArray json) {
		LinkedList<Project> list = new LinkedList<>();
		if(json==null) return list;
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Project fromJSON(JSONObject json) {
		return new Project()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setDomain(DomainJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.DOMAIN)))
				.setAlias(JsonUtils.getString(json, IJsonNames.ALIAS))
				.setType(ProjectTypeJSON.fromJSON(json.optJSONObject(IJsonNames.TYPE)))
				.setName(JsonUtils.getString(json, IJsonNames.NAME))
				.setDate(JsonUtils.getDate(json, IJsonNames.DATE))
				.setRegistry(RegistryJSON.fromJSON(json.optJSONObject(IJsonNames.REGISTRY)))
				.setActive(JsonUtils.getboolean(json, IJsonNames.ACTIVE))
				.setProjectHolder(ProjectHolderJSON.fromJSON(json.optJSONObject(IJsonNames.PROJECT_HOLDER)))
				.setProjectHolders(ProjectHolderJSON.fromJSON(json.optJSONArray("projectHolders")))
				.setProjectActivities(ProjectActivityJSON.fromJSON(json.optJSONArray("projectActivities")))
				.setDirty(JsonUtils.getboolean(json, IJsonNames.DIRTY));
	}
	
	public static JSONArray toJSON(List<Project> projects) {
		return toJSON(projects.stream());
	}
	
	public static JSONArray toJSON(Stream<Project> projects) {
		JSONArray array = new JSONArray();
		projects.forEach(project -> array.put(toJSON(project)));
		return array;
	}
	
	public static JSONObject toJSON(Project project) {
		if(project == null || project.isEmpty()) return new JSONObject();
		return new JSONObject()
				.put(IJsonNames.ID, project.getId())
				.put(IJsonNames.DOMAIN, DomainJSON.toJSON(project.getDomain()))
				.put(IJsonNames.TYPE, ProjectTypeJSON.toJSON(project.getType()))
				.put(IJsonNames.REGISTRY, RegistryJSON.toJSON(project.getRegistry()))
				.put(IJsonNames.NAME, project.getName())
				.put(IJsonNames.ALIAS, project.getAlias())
				.put(IJsonNames.DATE, JsonUtils.getDateJSON(project.getDate()))
				.put(IJsonNames.ACTIVE, project.isActive())
				.put(IJsonNames.PROJECT_HOLDER, ProjectHolderJSON.toJSON(project.getProjectHolder()))
				.put(IJsonNames.TAS, project.isTas())
				.put(IJsonNames.COMMERCIAL, project.isCommercial())
				.put(IJsonNames.RESERVATION, project.isReservation())
				.put(IJsonNames.DIRTY, project.isDirty())
				.put("projectHolders", ProjectHolderJSON.toJSON(project.getProjectHolders()))
				;
	}
}
