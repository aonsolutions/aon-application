package com.esferalia.aon.ui.calendar.print;

import com.esferalia.aon.calendar.Calendar;

public class PrintableCalendar {
	
	private String enterprise;
	private String workPlace;
	private String person;
	private Calendar calendar;
	private Integer year;
	private Double totalHours;
	private CalendarFactory calendarFactory;
	
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
	public CalendarFactory getCalendarFactory() {
		if(calendarFactory==null){
			calendarFactory = new CalendarFactory();
		}
		return calendarFactory;
	}
	public void setCalendarFactory(CalendarFactory calendarFactory) {
		this.calendarFactory = calendarFactory;
	}
	public Double getTotalHours() {
		calculateHours();
		return totalHours;
	}
	public void setTotalHours(Double totalHours) {
		this.totalHours = totalHours;
	}
	
	private void calculateHours(){
		setTotalHours(0.0);
		for(PrintableMonth m: getCalendarFactory().getMonthList()){
			if(m.getHours()!=null){
				setTotalHours(totalHours+m.getHours());
			}
		}
	}
	
		
}
