package com.esferalia.aon.calendar;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.HolidayDB;

@Entity
@Table(name="holiday")
@Heritable
public class Holiday extends HolidayDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}