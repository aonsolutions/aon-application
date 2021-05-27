package com.esferalia.aon.payroll.calculator.sql;

import java.util.Calendar;

import com.esferalia.aon.calendar.enumeration.DayType;

public interface ICalendar {
	
	DayType getDayType(Calendar day);

	DayType getWeekDayType(int weekDay);
}
