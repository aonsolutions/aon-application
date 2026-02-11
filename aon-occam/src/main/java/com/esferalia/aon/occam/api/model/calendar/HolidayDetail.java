package com.esferalia.aon.occam.api.model.calendar;

import java.io.Serializable;
import java.util.Date;

public class HolidayDetail implements Serializable {

	private Integer id;
	private Integer domain;
	private Integer holiday;
	private Date date;
	private String description;
	
	private boolean isDirty = false;
	
	public HolidayDetail() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public HolidayDetail setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public HolidayDetail setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public HolidayDetail setDescription(String description) {
		this.description = description;
		return this;
	}

	public Integer getHoliday() {
		return holiday;
	}

	public HolidayDetail setHoliday(Integer holiday) {
		this.holiday = holiday;
		return this;
	}

	public Date getDate() {
		return date;
	}

	public HolidayDetail setDate(Date date) {
		this.date = date;
		return this;
	}
	
	public boolean isDirty() {
		return isDirty;
	}

	public HolidayDetail setDirty(boolean isDirty) {
		this.isDirty = isDirty;
		return this;
	}
	
}
