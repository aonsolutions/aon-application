package com.esferalia.aon.occam.api.model.aonsolutions;

import java.io.Serializable;
import java.util.Date;

import org.json.JSONObject;

public class TimeControlContractEvent implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
    private Integer id;
	private Date startDate;
	private Date endDate;
	private String description;
	private TimeControlContractEventSource source;
	
	public TimeControlContractEvent() {
		super();
	}
	
	public Integer getId() {
		return id;
	}

	public TimeControlContractEvent setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Date getStartDate() {
		return startDate;
	}

	public TimeControlContractEvent setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}
	
	public Date getEndDate() {
		return endDate;
	}

	public TimeControlContractEvent setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public TimeControlContractEvent setDescription(String description) {
		this.description = description;
		return this;
	}

	public TimeControlContractEventSource getSource() {
		return source;
	}

	public TimeControlContractEvent setSource(TimeControlContractEventSource source) {
		this.source = source;
		return this;
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("id", getId());
		json.put("start_date", getStartDate() != null ? getStartDate().getTime() : null);
		json.put("end_date", getEndDate() != null ? getEndDate().getTime() : null);
		json.put("description", getDescription());
		json.put("source", getSource().name());
		return json;
	}
}
