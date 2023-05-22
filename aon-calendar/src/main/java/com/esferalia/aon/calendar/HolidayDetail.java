package com.esferalia.aon.calendar;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.HolidayDetailDB;

@Entity
@Table(name="holiday_detail")
@Heritable
public class HolidayDetail extends HolidayDetailDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
   
}