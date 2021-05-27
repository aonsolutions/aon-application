package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.esferalia.aon.gwt.payroll.client.Quartet;

@SuppressWarnings("serial")
public class EmployeeCalendarInfo implements Serializable {
	
	private Boolean fullTimeJourney;
	private Boolean agrarianContract;
	private Date contractStartDate;
	private Date contractEndDate;
	
	HashMap<Date, String> monthExtraHoursMap;
	
	Byte[] nonWorkingDays;
	
	HashMap<java.util.Date, String> festiveDaysMap;
	
	ArrayList<Quartet<Date, Date, String, String>> daysITList;
	
	CalendarHours calendarHours;
	
	CalendarHoursExtraCompl calendarHoursComplementary;
	
	CalendarDaysType calendarDaysType;
	
	CalendarDaysType partialityDaysType;
	
	public EmployeeCalendarInfo() {
		super();
	}

	public Boolean getFullTimeJourney() {
		return fullTimeJourney;
	}

	public EmployeeCalendarInfo setFullTimeJourney(Boolean fullTimeJourney) {
		this.fullTimeJourney = fullTimeJourney;
		return this;
	}

	public Boolean getAgrarianContract() {
		return agrarianContract;
	}

	public EmployeeCalendarInfo setAgrarianContract(Boolean agrarianContract) {
		this.agrarianContract = agrarianContract;
		return this;
	}

	public HashMap<Date, String> getMonthExtraHoursMap() {
		return monthExtraHoursMap;
	}

	public EmployeeCalendarInfo setMonthExtraHoursMap(HashMap<Date, String> monthExtraHoursMap) {
		this.monthExtraHoursMap = monthExtraHoursMap;
		return this;
	}

	public Byte[] getNonWorkingDays() {
		return nonWorkingDays;
	}

	public EmployeeCalendarInfo setNonWorkingDays(Byte[] nonWorkingDays) {
		this.nonWorkingDays = nonWorkingDays;
		return this;
	}

	public HashMap<java.util.Date, String> getFestiveDaysMap() {
		return festiveDaysMap;
	}

	public EmployeeCalendarInfo setFestiveDaysMap(HashMap<java.util.Date, String> festiveDaysMap) {
		this.festiveDaysMap = festiveDaysMap;
		return this;
	}

	public ArrayList<Quartet<Date, Date, String, String>> getDaysITList() {
		return daysITList;
	}

	public EmployeeCalendarInfo setDaysITList(ArrayList<Quartet<Date, Date, String, String>> daysITList) {
		this.daysITList = daysITList;
		return this;
	}

	public CalendarHours getCalendarHours() {
		return calendarHours;
	}
	
	public CalendarHoursExtraCompl getCalendarHoursExtraCompl() {
		return calendarHoursComplementary;
	}

	public EmployeeCalendarInfo setCalendarHours(CalendarHours calendarHours) {
		this.calendarHours = calendarHours;
		return this;
	}
	
	public EmployeeCalendarInfo setCalendarHoursComplementary(CalendarHoursExtraCompl calendarHoursComplementary) {
		this.calendarHoursComplementary = calendarHoursComplementary;
		return this;
	}

	public CalendarDaysType getCalendarDaysType() {
		return calendarDaysType;
	}

	public EmployeeCalendarInfo setCalendarDaysType(CalendarDaysType calendarDaysType) {
		this.calendarDaysType = calendarDaysType;
		return this;
	}

	public CalendarDaysType getPartialityDaysType() {
		return partialityDaysType;
	}

	public EmployeeCalendarInfo setPartialityDaysType(CalendarDaysType partialityDaysType) {
		this.partialityDaysType = partialityDaysType;
		return this;
	}

	public Date getContractStartDate() {
		return contractStartDate;
	}

	public void setContractStartDate(Date contractStartDate) {
		this.contractStartDate = contractStartDate;
	}

	public Date getContractEndDate() {
		return contractEndDate;
	}

	public void setContractEndDate(Date contractEndDate) {
		this.contractEndDate = contractEndDate;
	}
	
}
