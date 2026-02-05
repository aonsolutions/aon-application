package com.esferalia.aon.occam.api.model.calendar;

import java.io.Serializable;
import java.util.List;

public class Holiday implements Serializable {

	
	private Integer id;
	private Integer domain;
	private String description;
	private Integer holidayParent;
	private boolean isEditable;
	
	private List<HolidayDetail> details;
	
	public Holiday() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public Holiday setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public Holiday setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Holiday setDescription(String description) {
		this.description = description;
		return this;
	}

	public Integer getHolidayParent() {
		return holidayParent;
	}

	public Holiday setHolidayParent(Integer holidayParent) {
		this.holidayParent = holidayParent;
		return this;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public Holiday setEditable(boolean isEditable) {
		this.isEditable = isEditable;
		return this;
	}

	public List<HolidayDetail> getDetails() {
		return details;
	}

	public Holiday setDetails(List<HolidayDetail> details) {
		this.details = details;
		return this;
	}
	
}
