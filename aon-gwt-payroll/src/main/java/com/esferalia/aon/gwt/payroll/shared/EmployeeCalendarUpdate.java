package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.payroll.client.Quartet;
import com.esferalia.aon.gwt.payroll.client.EmployeeCalendarDraftObjectData.DayType;

public class EmployeeCalendarUpdate implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private HashMap<java.util.Date, DayType> daysTypesMap;
	private HashMap<java.util.Date, Double> daysHoursMap;
	private List<Quartet<Date, Date, String, String>> monthExtraHoursList;
	private Map<java.util.Date, Double> mapDaysCoefficientStrike;
	private Map<java.util.Date, Double> mapDaysCoefficientEre;
	private Map<java.util.Date, String> mapInactivityDays;
	private Double ereCoefficient;
	private Boolean fullTimeEmployee;
	private ArrayList<java.util.Date> festiveWorkingDays;
	private boolean hasChangeHours;
	
	public EmployeeCalendarUpdate() {
		super();
	}

	public EmployeeCalendarUpdate(HashMap<java.util.Date, DayType> daysTypesMap, HashMap<java.util.Date, Double> daysHoursMap,
			List<Quartet<Date, Date, String, String>> monthExtraHoursList, Double ereCoefficient, Boolean fullTimeEmployee,
			Map<java.util.Date, String> mapInactivityDays, ArrayList<java.util.Date> festiveWorkingDays) {
		super();
		this.daysTypesMap = daysTypesMap;
		this.daysHoursMap = daysHoursMap;
		this.monthExtraHoursList = monthExtraHoursList;
		this.ereCoefficient = ereCoefficient;
		this.fullTimeEmployee = fullTimeEmployee;
		this.mapInactivityDays = mapInactivityDays;
		this.festiveWorkingDays = festiveWorkingDays;
		
	}

	// ------------ GETTERS / SETTERS ------------
	
	public List<Quartet<Date, Date, String, String>> getMonthExtraHoursList() {
		return monthExtraHoursList;
	}

	public EmployeeCalendarUpdate setMonthExtraHoursList(List<Quartet<Date, Date, String, String>> monthExtraHoursList) {
		this.monthExtraHoursList = monthExtraHoursList;
		return this;
	}

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

	public void setStrikeDaysValues(Map<java.util.Date, Double> draftMapDaysCoefficientStrike) {
		this.mapDaysCoefficientStrike = draftMapDaysCoefficientStrike;
		
	}

	public void setEreDaysValues(Map<java.util.Date, Double> draftMapDaysCoefficientEre) {
		this.mapDaysCoefficientEre = draftMapDaysCoefficientEre;
		
	}

	public Map<java.util.Date, Double> getMapDaysCoefficientStrike() {
		return mapDaysCoefficientStrike;
	}

	public Map<java.util.Date, Double> getMapDaysCoefficientEre() {
		return mapDaysCoefficientEre;
	}

	public Map<java.util.Date, String> getMapInactivityDays() {
		return mapInactivityDays;
	}

	public void setMapInactivityDays(Map<java.util.Date, String> mapInactivityDays) {
		this.mapInactivityDays = mapInactivityDays;
	}

	public ArrayList<java.util.Date> getFestiveWorkingDays() {
		return festiveWorkingDays;
	}

	public void setFestiveWorkingDays(ArrayList<java.util.Date> festiveWorkingDays) {
		this.festiveWorkingDays = festiveWorkingDays;
	}

	public boolean isHasChangeHours() {
		return hasChangeHours;
	}

	public void setHasChangeHours(boolean hasChangeHours) {
		this.hasChangeHours = hasChangeHours;
	}
	
	
	
}
