package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.gwt.payroll.client.Quartet;

public class EmployeeCalendarData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private ArrayList<Quartet<Date, Date, String, String>> contractHoursList;
	private ArrayList<Quartet<Date, Date, String, String>> contractTypeDaysList;
	private ArrayList<java.util.Date> contractFestivesDaysList;
	private ArrayList<Byte> contractNonWorkingDaysList;
	private boolean fullTimeJourney;
	
	public EmployeeCalendarData() {
		super();
	}

	public EmployeeCalendarData(ArrayList<Quartet<Date, Date, String, String>> contractHoursList,
			ArrayList<Quartet<Date, Date, String, String>> contractTypeDaysList,
			ArrayList<java.util.Date> contractFestivesDaysList,
			ArrayList<Byte> contractNonWorkingDaysList, 
			boolean fullTimeJourney) {
		super();
		this.contractHoursList = contractHoursList;
		this.contractTypeDaysList = contractTypeDaysList;
		this.contractFestivesDaysList = contractFestivesDaysList;
		this.contractNonWorkingDaysList = contractNonWorkingDaysList;
		this.fullTimeJourney = fullTimeJourney;
	}
	
	// ------------- GETTERS / SETTERS -------------

	public List<Quartet<Date, Date, String, String>> getContractHoursList() {
		return contractHoursList;
	}

	public EmployeeCalendarData setContractHoursList(ArrayList<Quartet<Date, Date, String, String>> contractHoursList) {
		this.contractHoursList = contractHoursList;
		return this;
	}

	public List<Quartet<Date, Date, String, String>> getContractTypeDaysList() {
		return contractTypeDaysList;
	}

	public EmployeeCalendarData setContractTypeDaysList(ArrayList<Quartet<Date, Date, String, String>> contractTypeDaysList) {
		this.contractTypeDaysList = contractTypeDaysList;
		return this;
	}

	public ArrayList<java.util.Date> getContractFestiveDaysList() {
		return contractFestivesDaysList;
	}

	public EmployeeCalendarData setContractFestiveDaysList(ArrayList<java.util.Date> contractFestivesDaysList) {
		this.contractFestivesDaysList = contractFestivesDaysList;
		return this;
	}

	public ArrayList<Byte> getContractNonWorkingDaysList() {
		return contractNonWorkingDaysList;
	}

	public EmployeeCalendarData setContractNonWorkingDaysList(ArrayList<Byte> contractNonWorkingDaysList) {
		this.contractNonWorkingDaysList = contractNonWorkingDaysList;
		return this;
	}

	public boolean isFullTimeJourney() {
		return fullTimeJourney;
	}

	public EmployeeCalendarData setFullTimeJourney(boolean fullTimeJourney) {
		this.fullTimeJourney = fullTimeJourney;
		return this;
	}
	
	
	
	
}
