package com.esferalia.aon.calendar;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.CalendarPeriodDB;

@Entity
@Table(name="calendar_period")
@Heritable
public class CalendarPeriod extends CalendarPeriodDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}