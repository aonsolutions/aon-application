package com.esferalia.aon.calendar;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.HolidayDetailDB;

@Entity
@Table(name="holiday_detail")
public class HolidayDetail extends HolidayDetailDB {
	
	private static final long serialVersionUID = 1L;
   
}