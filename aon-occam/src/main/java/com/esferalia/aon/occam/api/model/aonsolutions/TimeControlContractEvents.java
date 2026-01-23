package com.esferalia.aon.occam.api.model.aonsolutions;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class TimeControlContractEvents implements Serializable {

	private static final long serialVersionUID = 1L;

	private Boolean fullTime;
	private Byte[] workingDays;
	private List<TimeControlContractEvent> events;
	private List<TimeControlContractEvent> festives;

	public TimeControlContractEvents() {
		super();
	}
		
	public Boolean getFullTime() {
		return fullTime;
	}

	public TimeControlContractEvents setFullTime(Boolean fullTime) {
		this.fullTime = fullTime;
		return this;
	}
	
	public Byte[] getWorkingDays() {
		return workingDays;
	}

	public TimeControlContractEvents setWorkingDays(Byte[] workingDays) {
		this.workingDays = workingDays;
		return this;
	}

	public List<TimeControlContractEvent> getEvents() {
		return null == events ? Collections.emptyList() : events;
	}

	public TimeControlContractEvents setEvents(List<TimeControlContractEvent> events) {
		this.events = events;
		return this;
	}

	public List<TimeControlContractEvent> getFestives() {
		return null == festives ? Collections.emptyList() : festives;
	}

	public TimeControlContractEvents setFestives(List<TimeControlContractEvent> festives) {
		this.festives = festives;
		return this;
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		
		json.put("fulltime", getFullTime());
		
		JSONArray festivesArr = new JSONArray();
		getFestives().forEach(f -> festivesArr.put(f.toJSON()));
		
		JSONArray eventsArr = new JSONArray();
		getEvents().forEach(e -> eventsArr.put(e.toJSON()));
		
		JSONArray workingDaysArr = new JSONArray();
		if(null != getWorkingDays())
			for(int i = 0; i < 7; i++)
				workingDaysArr.put(workingDays[i]);
		
		json.put("festives", festivesArr);
		json.put("events", eventsArr);
		json.put("workingDays", workingDaysArr);
		
		return json;
	}
}
