package com.esferalia.aon.calendar;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.calendar.enumeration.DayType;
import com.esferalia.aon.entity.master.CalendarDB;

@Entity
@Table(name="calendar")
public class Calendar extends CalendarDB {
	
	private static final long serialVersionUID = 1L;

    public Calendar() {
    	super();
    	setAnualHours(0.0);
    	setMonday(DayType.WORKING_DAY);
		setMondayHours(8.0);
		setTuesday(DayType.WORKING_DAY);
		setTuesdayHours(8.0);
		setWednesday(DayType.WORKING_DAY);
		setWednesdayHours(8.0);
		setThursday(DayType.WORKING_DAY);
		setThursdayHours(8.0);
		setFriday(DayType.WORKING_DAY);
		setFridayHours(8.0);
		setSaturday(DayType.NOT_WORKING_DAY);
		setSaturdayHours(0.0);
		setSunday(DayType.NOT_WORKING_DAY);
		setSundayHours(0.0);
	}
	
}