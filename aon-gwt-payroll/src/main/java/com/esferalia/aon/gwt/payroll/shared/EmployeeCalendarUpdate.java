package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.HashMap;

import com.esferalia.aon.gwt.payroll.client.EmployeeCalendarDraftObjectData.DayType;

public class EmployeeCalendarUpdate implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private HashMap<java.util.Date, DayType> daysTypesMap;
	private HashMap<java.util.Date, Double> daysHoursMap;
	private Double ereCoefficient;
	private Boolean fullTimeEmployee;
	
	public EmployeeCalendarUpdate() {
		super();
	}

	public EmployeeCalendarUpdate(HashMap<java.util.Date, DayType> daysTypesMap, HashMap<java.util.Date, Double> daysHoursMap,
			Double ereCoefficient, Boolean fullTimeEmployee) {
		super();
		this.daysTypesMap = daysTypesMap;
		this.daysHoursMap = daysHoursMap;
		this.ereCoefficient = ereCoefficient;
		this.fullTimeEmployee = fullTimeEmployee;
		
	}

	// ------------ GETTERS / SETTERS ------------
	
	public HashMap<java.util.Date, DayType> getDaysTypeMap() {
		return daysTypesMap;
	}

	public EmployeeCalendarUpdate setDaysTypeMap(HashMap<java.util.Date, DayType> daysTypesMap) {
		this.daysTypesMap = daysTypesMap;
		return this;
	}

	public HashMap<java.util.Date, Double> getDaysHourMap() {
		return daysHoursMap;
	}

	public EmployeeCalendarUpdate setDaysHourMap(HashMap<java.util.Date, Double> daysHoursMap) {
		this.daysHoursMap = daysHoursMap;
		return this;
	}

	public Double getEreCoefficient() {
		return ereCoefficient;
	}

	public EmployeeCalendarUpdate setEreCoefficient(Double ereCoefficient) {
		this.ereCoefficient = ereCoefficient;
		return this;
	}
	
	public Boolean getFullTimeEmployee() {
		return this.fullTimeEmployee;
	}

	public EmployeeCalendarUpdate setFullTimeEmployee(Boolean fullTimeEmployee) {
		this.fullTimeEmployee = fullTimeEmployee;
		return this;
	}
	
	
	

	
	
}
