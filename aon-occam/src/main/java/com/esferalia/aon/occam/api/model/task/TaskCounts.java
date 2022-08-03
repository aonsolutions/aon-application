package com.esferalia.aon.occam.api.model.task;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;

public class TaskCounts  implements Serializable{	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList<GeneralData> status;
	
	private ArrayList<GeneralData> workgroup;
	
	private ArrayList<GeneralData> tags;
	
	public TaskCounts() { 
		this.status = new ArrayList<>();
		this.workgroup = new ArrayList<>();
		this.tags = new ArrayList<>();
	}
	
	public void addStatus(TaskStatus status, Integer count) {
		GeneralData statusData = new GeneralData();
		statusData.field = status.getName();
		statusData.count = count;
		addStatus(statusData);
	}
	
	public void addWorkgroup(String workgroup, Integer count) {
		GeneralData workgroupData = new GeneralData();
		workgroupData.field = workgroup;
		workgroupData.count = count;
		addWorkgroup(workgroupData);
	}
	
	public void addTag(String tag, Integer count) {
		GeneralData tagData = new GeneralData();
		tagData.field = tag;
		tagData.count = count;
		addTag(tagData);
	}
	
	protected void addStatus(GeneralData statusData) {
		status.add(statusData);
	}
	
	protected void addWorkgroup(GeneralData workgroupData) {
		workgroup.add(workgroupData);
	}
	
	protected void addTag(GeneralData workgroupData) {
		tags.add(workgroupData);
	}
	
	private static class GeneralData {
		private String field;
		private Integer count;
		
		public String getField() {
			return field;
		}
		public Integer getCount() {
			return count;
		}
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		
		if(!status.isEmpty()) {
			JSONObject jsonStatus = new JSONObject();
			status.forEach(d->
				jsonStatus.put(d.getField(), d.getCount())
			);
			json.put(IJsonNames.STATUS, jsonStatus);
		}
		
		if(!workgroup.isEmpty()) {
			JSONObject jsonWorkgroup = new JSONObject();
			workgroup.forEach(d->
				jsonWorkgroup.put(d.getField(), d.getCount())
			);
			json.put(IJsonNames.WORKGROUPS, jsonWorkgroup);
		}
		
		if(!tags.isEmpty()) {
			JSONObject jsonTags = new JSONObject();
			tags.forEach(d->
				jsonTags.put(d.getField(), d.getCount())
			);
			json.put(IJsonNames.TAG, jsonTags);
		}
		return json;
	}
}
