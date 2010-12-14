package com.esferalia.aon.ui.calendar.print;

import com.esferalia.aon.calendar.Calendar;

public class PrintableCalendar {
	
	private String enterprise;
	private String workPlace;
	private String person;
	private Calendar calendar;
	private Integer year;
	private MonthFactory monthFactory;
	
	public Calendar getCalendar() {
		return calendar;
	}
	public String getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(String enterprise) {
		this.enterprise = enterprise;
	}
	public String getWorkPlace() {
		return workPlace;
	}
	public void setWorkPlace(String workPlace) {
		this.workPlace = workPlace;
	}
	public String getPerson() {
		return person;
	}
	public void setPerson(String person) {
		this.person = person;
	}
	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public MonthFactory getMonthFactory() {
		return monthFactory;
	}
	public void setMonthFactory(MonthFactory monthFactory) {
		this.monthFactory = monthFactory;
	}
	
}
