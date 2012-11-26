package com.esferalia.aon.calendar;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.HolidayDB;

@Entity
@Table(name="holiday")
public class Holiday extends HolidayDB {
	
	private static final long serialVersionUID = 1L;
	
}