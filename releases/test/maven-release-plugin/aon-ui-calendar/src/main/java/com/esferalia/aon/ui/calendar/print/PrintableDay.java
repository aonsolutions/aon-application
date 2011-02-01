package com.esferalia.aon.ui.calendar.print;


public class PrintableDay {
	
	private Integer dayOfMonth;
	private boolean holiday;
	private boolean notWorkingDay;
	private boolean vacation;
	private boolean continuousTime;
	private boolean other;
	private Double hours;

	public Integer getDayOfMonth() {
		return dayOfMonth;
	}
	public void setDayOfMonth(Integer dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}
	public boolean isHoliday() {
		return holiday;
	}
	public void setHoliday(boolean holiday) {
		this.holiday = holiday;
	}
	public boolean isNotWorkingDay() {
		return notWorkingDay;
	}
	public void setNotWorkingDay(boolean notWorkingDay) {
		this.notWorkingDay = notWorkingDay;
	}
	public boolean isVacation() {
		return vacation;
	}
	public void setVacation(boolean vacation) {
		this.vacation = vacation;
	}
	public boolean isContinuousTime() {
		return continuousTime;
	}
	public void setContinuousTime(boolean continuousTime) {
		this.continuousTime = continuousTime;
	}
	public boolean isOther() {
		return other;
	}
	public void setOther(boolean other) {
		this.other = other;
	}
	public Double getHours() {
		return hours;
	}
	public void setHours(Double hours) {
		this.hours = hours;
	}

	public void disableAllTypes() {
		setHoliday(false); 
		setNotWorkingDay(false);
		setVacation(false);
		setContinuousTime(false);
		setOther(false); 
	}
	
	
}
