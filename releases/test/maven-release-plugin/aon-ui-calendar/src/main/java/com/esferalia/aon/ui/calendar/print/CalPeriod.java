package com.esferalia.aon.ui.calendar.print;

import com.code.aon.common.enumeration.Month;

public class CalPeriod {
	
	private String description;
	private Month month;
	private Integer startDay;
	private Integer endDay;

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Month getMonth() {
		return month;
	}
	public void setMonth(Month month) {
		this.month = month;
	}
	public Integer getStartDay() {
		return startDay;
	}
	public void setStartDay(Integer startDay) {
		this.startDay = startDay;
	}
	public Integer getEndDay() {
		return endDay;
	}
	public void setEndDay(Integer endDay) {
		this.endDay = endDay;
	}
	
	
}