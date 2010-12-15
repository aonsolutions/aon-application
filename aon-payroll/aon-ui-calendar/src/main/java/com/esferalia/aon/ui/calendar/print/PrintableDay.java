package com.esferalia.aon.ui.calendar.print;


public class PrintableDay {
	
	private Integer dayOfMonth;
	private boolean holiday;
	
	public boolean isHoliday() {
		return holiday;
	}
	public void setHoliday(boolean holiday) {
		this.holiday = holiday;
	}
	public Integer getDayOfMonth() {
		return dayOfMonth;
	}
	public void setDayOfMonth(Integer dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}
	
		
	
}
