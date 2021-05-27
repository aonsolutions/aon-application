package com.esferalia.aon.ui.calendar.print;

import java.io.Serializable;
import java.util.Date;

import com.code.aon.AonVersion;
import com.esferalia.aon.calendar.enumeration.DayType;

public class CalendarDay implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String description;
	private Date date;
	private DayType type;
	private Double hours;
	private boolean overwritten;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public DayType getType() {
		return type;
	}
	public void setType(DayType type) {
		this.type = type;
	}
	public Double getHours() {
		return hours;
	}
	public void setHours(Double hours) {
		this.hours = hours;
	}
	public boolean isOverwritten() {
		return overwritten;
	}
	public void setOverwritten(boolean overwritten) {
		this.overwritten = overwritten;
	}
	
	
}
