package com.esferalia.aon.gwt.payroll.shared;

import static com.esferalia.aon.gwt.payroll.shared.CalendarDraft.DayType.NOT_WORKING_DAY;
import static com.esferalia.aon.gwt.payroll.shared.CalendarDraft.DayType.WORKING_DAY;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CalendarDraft implements Serializable {
	
	
	public static enum DayType {
		WORKING_DAY,
		NOT_WORKING_DAY;
		
		public static DayType valueOf(byte ordinal){
			if ( ordinal == 1)
				return NOT_WORKING_DAY;
			
			return WORKING_DAY;
		}
	}

	private List<HolidayDraft> holidayDrafts;
	private DayType daysTypes [] = {
			NOT_WORKING_DAY,
			WORKING_DAY,
			WORKING_DAY,
			WORKING_DAY,
			WORKING_DAY,
			WORKING_DAY,
			NOT_WORKING_DAY
	};
	
	private Integer calendarHoliday;
	
	public DayType getDayType(int weekDay){
		return daysTypes[weekDay];
	}
	
	public void setDayType(int weekDay, DayType type ){
		daysTypes[weekDay] = type;
	}
	
	public DayType[] getDaysTypes() {
		return daysTypes;
	}
	
	public List<HolidayDraft> getHolidayDrafts() {
		return Collections.unmodifiableList(holidayDrafts);
	}
	
	public void setHolidayDrafts(List<HolidayDraft> holidayDrafts) {
		this.holidayDrafts = holidayDrafts;
	}
	public Integer getCalendarHoliday() {
		return calendarHoliday;
	}
	public void setCalendarHoliday(Integer calendarHoliday) {
		this.calendarHoliday = calendarHoliday;
	}
	
}
