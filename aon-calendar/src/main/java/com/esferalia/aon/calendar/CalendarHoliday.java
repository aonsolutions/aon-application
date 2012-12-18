package com.esferalia.aon.calendar;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.CalendarHolidayDB;


@Entity
@Table(name="calendar_holiday")
@Heritable
public class CalendarHoliday extends CalendarHolidayDB {
	
	private static final long serialVersionUID = 1L;

}