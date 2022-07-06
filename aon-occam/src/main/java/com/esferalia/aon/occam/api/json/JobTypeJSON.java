package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.task.JobType;
public class JobTypeJSON {

	private JobTypeJSON() {
	   throw new IllegalStateException("Utility class");
	}
	
	public static List<JobType> fromJSON(JSONArray json) {
		LinkedList<JobType> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static JobType fromJSON(JSONObject json) {
		return new JobType()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(DomainJSON.fromJSON(json.optJSONObject(IJsonNames.DOMAIN)))
			.setDescription(JsonUtils.getString(json, IJsonNames.DESCRIPTION))

			;
	}
	
	public static JSONArray toJSON(LinkedList<JobType> dts) {
		return toJSON(dts.stream());
	}
	
	public static JSONArray toJSON(List<JobType> dts) {
		return toJSON(dts.stream());
	}
	
	public static JSONArray toJSON(Stream<JobType> dts) {
		JSONArray array = new JSONArray();
		dts.forEach(dt -> array.put(toJSON(dt)));
		return array;
	}
	
	public static JSONObject toJSON(JobType dt) {
		return new JSONObject()
			.put(IJsonNames.ID, dt.getId())
			.put(IJsonNames.DOMAIN, DomainJSON.toJSON(dt.getDomain()))
			.put(IJsonNames.DESCRIPTION, dt.getDescription());
	}

}
