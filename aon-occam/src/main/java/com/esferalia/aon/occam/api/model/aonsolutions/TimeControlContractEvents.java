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
	private Double[] workingDaysHours;
	private Double annualHolidays;
	private List<TimeControlContractEvent> events;
	private List<TimeControlContractEvent> festives;
	private List<TimeControlContractEvent> contractDaysType;
	private List<TimeControlContractEvent> contractITs;

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

	public Double[] getWorkingDaysHours() {
		return workingDaysHours;
	}

	public TimeControlContractEvents setWorkingDaysHours(Double[] workingDaysHours) {
		this.workingDaysHours = workingDaysHours;
		return this;
	}
	
	public Double getAnnualHolidays() {
		return annualHolidays;
	}

	public TimeControlContractEvents setAnnualHolidays(Double annualHolidays) {
		this.annualHolidays = annualHolidays;
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

	public List<TimeControlContractEvent> getContractDaysType() {
		return null == contractDaysType ? Collections.emptyList() : contractDaysType;
	}

	public TimeControlContractEvents setContractDaysType(List<TimeControlContractEvent> contractDaysType) {
		this.contractDaysType = contractDaysType;
		return this;
	}
	
	public List<TimeControlContractEvent> getContractITs() {
		return null == contractITs ? Collections.emptyList() : contractITs;
	}

	public TimeControlContractEvents setContractITs(List<TimeControlContractEvent> contractITs) {
		this.contractITs = contractITs;
		return this;
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		
		json.put("fulltime", getFullTime());
		
		JSONArray festivesArr = new JSONArray();
		getFestives().forEach(f -> festivesArr.put(f.toJSON()));
		
		JSONArray daysTypeArr = new JSONArray();
		getContractDaysType().forEach(f -> daysTypeArr.put(f.toJSON()));
		
		JSONArray contractITsArr = new JSONArray();
		getContractITs().forEach(f -> contractITsArr.put(f.toJSON()));
		
		JSONArray eventsArr = new JSONArray();
		getEvents().forEach(e -> eventsArr.put(e.toJSON()));
		
		JSONArray workingDaysArr = new JSONArray();
		if(null != getWorkingDays())
			for(int i = 0; i < 7; i++)
				workingDaysArr.put(workingDays[i]);
		
		JSONArray workingDaysHoursArr = new JSONArray();
		if(null != getWorkingDaysHours())
			for(int i = 0; i < 7; i++)
				workingDaysHoursArr.put(workingDaysHours[i]);
		
		json.put("festives", festivesArr);
		json.put("daysType", daysTypeArr);
		json.put("contractITs", contractITsArr);
		json.put("events", eventsArr);
		json.put("workingDays", workingDaysArr);
		json.put("workingDaysHours", workingDaysHoursArr);
		json.put("annualHolidays", annualHolidays);
		
		return json;
	}
}
