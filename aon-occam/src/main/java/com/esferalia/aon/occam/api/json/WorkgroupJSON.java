package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Workgroup;

public class WorkgroupJSON {

	private WorkgroupJSON() {
	
	}
	
	public static List<Workgroup> fromJSON(JSONArray json) {
		LinkedList<Workgroup> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Workgroup fromJSON(JSONObject json) {
		if(json==null || json.isEmpty()) return new Workgroup();
		return new Workgroup()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setDescription(JsonUtils.getString(json, IJsonNames.DESCRIPTION))
			.setActive(JsonUtils.getboolean(json, IJsonNames.ACTIVE))
			.setDirty(JsonUtils.getboolean(json, IJsonNames.DIRTY))
			.setRemoved(JsonUtils.getboolean(json, IJsonNames.REMOVED));
	}
	
	public static JSONArray toJSON(List<Workgroup> workgroups) {
		return toJSON(workgroups.stream());
	}
	
	public static JSONArray toJSON(Stream<Workgroup> workgroups) {
		JSONArray array = new JSONArray();
		workgroups.forEach(workgroup -> array.put(toJSON(workgroup)));
		return array;
	}
	
	public static JSONObject toJSON(Workgroup workgroup) {
		return new JSONObject()
			.put(IJsonNames.ID, workgroup.getId())
			.put(IJsonNames.DOMAIN, workgroup.getDomain())
			.put(IJsonNames.DESCRIPTION, workgroup.getDescription())
			.put(IJsonNames.ACTIVE, workgroup.isActive())
			.put(IJsonNames.DIRTY, workgroup.isDirty())
			.put(IJsonNames.REMOVED, workgroup.isRemoved())
			;
	}
}
