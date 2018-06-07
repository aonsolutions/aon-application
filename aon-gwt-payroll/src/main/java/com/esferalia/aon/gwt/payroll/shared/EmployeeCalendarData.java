package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.esferalia.aon.gwt.payroll.client.Quartet;

public class EmployeeCalendarData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private ArrayList<Quartet<Date, Date, String, String>> contractHoursList;
	private ArrayList<Quartet<Date, Date, String, String>> contractExtraHoursList;
	private ArrayList<Quartet<Date, Date, String, String>> contractTypeDaysList;
	private ArrayList<Quartet<Date, Date, String, String>> contractITDayTypeList;
	private ArrayList<Quartet<Date, Date, String, String>> contractCoefficientEREDayTypeList;
	private ArrayList<Quartet<Date, Date, String, String>> contractCoefficientStrikeDayTypeList;
	private HashMap<java.util.Date, String> contractFestivesDaysList;
	private ArrayList<Byte> contractNonWorkingDaysList;
	private boolean fullTimeJourney;
	ArrayList<Quartet<Date, Date, String, String>> typeInactivityList;
	
	
	public EmployeeCalendarData() {
		super();
	}

	public EmployeeCalendarData(ArrayList<Quartet<Date, Date, String, String>> contractHoursList,
			ArrayList<Quartet<Date, Date, String, String>> contractExtraHoursList,
			ArrayList<Quartet<Date, Date, String, String>> contractTypeDaysList,
			ArrayList<Quartet<Date, Date, String, String>> contractITDayTypeList, 
			HashMap<java.util.Date, String> contractFestivesDaysList,
			ArrayList<Byte> contractNonWorkingDaysList, 
			boolean fullTimeJourney,
			ArrayList<Quartet<Date, Date, String, String>> contractCoefficientEREDayTypeList,
			ArrayList<Quartet<Date, Date, String, String>> contractCoefficientStrikeDayTypeList,
			ArrayList<Quartet<Date, Date, String, String>> typeInactivityList) {
		super();
		this.contractHoursList = contractHoursList;
		this.contractExtraHoursList = contractExtraHoursList;
		this.contractTypeDaysList = contractTypeDaysList;
		this.contractITDayTypeList = contractITDayTypeList;
		this.contractFestivesDaysList = contractFestivesDaysList;
		this.contractNonWorkingDaysList = contractNonWorkingDaysList;
		this.fullTimeJourney = fullTimeJourney;
		this.contractCoefficientEREDayTypeList = contractCoefficientEREDayTypeList;
		this.contractCoefficientStrikeDayTypeList = contractCoefficientStrikeDayTypeList;
		this.typeInactivityList = typeInactivityList;
	}
	
	// ------------- GETTERS / SETTERS -------------

	public List<Quartet<Date, Date, String, String>> getContractHoursList() {
		return contractHoursList;
	}

	public EmployeeCalendarData setContractHoursList(ArrayList<Quartet<Date, Date, String, String>> contractHoursList) {
		this.contractHoursList = contractHoursList;
		return this;
	}
	
	public List<Quartet<Date, Date, String, String>> getContractExtraHoursList() {
		return contractExtraHoursList;
	}

	public EmployeeCalendarData setContractExtraHoursList(ArrayList<Quartet<Date, Date, String, String>> contractExtraHoursList) {
		this.contractExtraHoursList = contractExtraHoursList;
		return this;
	}

	public List<Quartet<Date, Date, String, String>> getContractTypeDaysList() {
		return contractTypeDaysList;
	}

	public EmployeeCalendarData setContractTypeDaysList(ArrayList<Quartet<Date, Date, String, String>> contractTypeDaysList) {
		this.contractTypeDaysList = contractTypeDaysList;
		return this;
	}
	
	public List<Quartet<Date, Date, String, String>> getcontractITDayTypeList() {
		return contractITDayTypeList;
	}

	public EmployeeCalendarData setcontractITDayTypeList(ArrayList<Quartet<Date, Date, String, String>> contractITDayTypeList) {
		this.contractITDayTypeList = contractITDayTypeList;
		return this;
	}

	public HashMap<java.util.Date, String> getContractFestiveDaysList() {
		return contractFestivesDaysList;
	}

	public EmployeeCalendarData setContractFestiveDaysList(HashMap<java.util.Date, String> contractFestivesDaysList) {
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

	public ArrayList<Quartet<Date, Date, String, String>> getContractCoefficientEREDayTypeList() {
		return contractCoefficientEREDayTypeList;
	}

	public ArrayList<Quartet<Date, Date, String, String>> getContractCoefficientStrikeDayTypeList() {
		return contractCoefficientStrikeDayTypeList;
	}

	public ArrayList<Quartet<Date, Date, String, String>> getTypeInactivityList() {
		return typeInactivityList;
	}

	public void setTypeInactivityList(ArrayList<Quartet<Date, Date, String, String>> typeInactivityList) {
		this.typeInactivityList = typeInactivityList;
	}
}
