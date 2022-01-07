package com.esferalia.aon.gwt.payroll.shared;

import static com.esferalia.aon.gwt.payroll.shared.Shared.format;
import static com.esferalia.aon.gwt.payroll.shared.Shared.parse;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@SuppressWarnings("serial")
public class EmployeeCalendarInfo implements Serializable {
	
	// ----------------------------------------- Variables
	
	private Boolean isFullTime;
	private Boolean isAgrarian;
	private String contractStartDate;
	private String contractEndDate;
	private CalendarHours calendarHours;
	private CalendarExtraHours calendarExtraHours;
	private Map<java.util.Date, Double> calendarComplementaryHours;
	private Byte[] workingDays;
	private Map<java.util.Date, String> festiveDays;
	private CalendarDaysType calendarDaysType;
	private CalendarDaysType partialityDaysType;
	
	// ----------------------------------------- Constructor
	
	public EmployeeCalendarInfo() {
		super();
	}

	// ----------------------------------------- Getter/Setter
	
	public Boolean isFullTime() {
		return this.isFullTime;
	}

	public EmployeeCalendarInfo setFullTime(Boolean isFullTime) {
		this.isFullTime = isFullTime;
		return this;
	}

	public Boolean isAgrarian() {
		return this.isAgrarian;
	}

	public EmployeeCalendarInfo setAgrarian(Boolean isAgrarian) {
		this.isAgrarian = isAgrarian;
		return this;
	}

	public Date getContractStartDate() {
		return parse(contractStartDate);
	}

	public void setContractStartDate(Date contractStartDate) {
		this.contractStartDate = format(contractStartDate);
	}

	public Date getContractEndDate() {
		return parse(contractEndDate);
	}

	public void setContractEndDate(Date contractEndDate) {
		this.contractEndDate = format(contractEndDate);
	}
	
	public CalendarHours getCalendarHours() {
		return calendarHours;
	}
	
	public EmployeeCalendarInfo setCalendarHours(CalendarHours calendarHours) {
		this.calendarHours = calendarHours;
		return this;
	}
	
	public CalendarExtraHours getCalendarExtraHours() {
		return this.calendarExtraHours;
	}
	
	public EmployeeCalendarInfo setCalendarExtraHours(CalendarExtraHours calendarExtraHours) {
		this.calendarExtraHours = calendarExtraHours;
		return this;
	}

	public Map<java.util.Date, Double> getCalendarComplementaryHours() {
		return this.calendarComplementaryHours;
	}
	
	public EmployeeCalendarInfo setCalendarComplementaryHours(Map<java.util.Date, Double> calendarComplementaryHours) {
		this.calendarComplementaryHours = calendarComplementaryHours;
		return this;
	}
	
	public Byte[] getWorkingDays() {
		return workingDays;
	}

	public EmployeeCalendarInfo setWorkingDays(Byte[] workingDays) {
		this.workingDays = workingDays;
		return this;
	}

	public Map<java.util.Date, String> getFestiveDays() {
		return festiveDays;
	}

	public EmployeeCalendarInfo setFestiveDays(Map<java.util.Date, String> festiveDays) {
		this.festiveDays = festiveDays;
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
	
}
