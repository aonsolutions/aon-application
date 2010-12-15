package com.esferalia.aon.ui.calendar.print;


public class PrintableDay {
	
	private Integer dayOfMonth;
	private boolean weekEnd;
	private boolean holiday;
	private boolean period;
	
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
	public boolean isWeekEnd() {
		return weekEnd;
	}
	public void setWeekEnd(boolean weekEnd) {
		this.weekEnd = weekEnd;
	}
	public boolean isPeriod() {
		return period;
	}
	public void setPeriod(boolean period) {
		this.period = period;
	}
	
	
}
