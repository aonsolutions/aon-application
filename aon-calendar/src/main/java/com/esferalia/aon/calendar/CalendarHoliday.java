package com.esferalia.aon.calendar;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.CalendarHolidayDB;


@Entity
@Table(name="calendar_holiday")
@Heritable
public class CalendarHoliday extends CalendarHolidayDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}