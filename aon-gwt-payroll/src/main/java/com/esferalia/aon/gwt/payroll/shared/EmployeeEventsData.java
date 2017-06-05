package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Map;

import com.esferalia.aon.gwt.payroll.client.Quartet;

public class EmployeeEventsData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Map<String,ArrayList<Quartet<Date, Date, String, String>>> contractEventsList;
	private boolean fullTimeJourney;
	
	public EmployeeEventsData() {
		super();
	}

	public EmployeeEventsData(Map<String,ArrayList<Quartet<Date, Date, String, String>>> contractEventsList,
			boolean fullTimeJourney) {
		super();
		this.contractEventsList = contractEventsList;
		this.fullTimeJourney = fullTimeJourney;
	}
	
	// ------------- GETTERS / SETTERS -------------

	public Map<String,ArrayList<Quartet<Date, Date, String, String>>> getContractEventsList() {
		return contractEventsList;
	}

	public EmployeeEventsData setContractEventsList(Map<String,ArrayList<Quartet<Date, Date, String, String>>> contractEventsList) {
		this.contractEventsList = contractEventsList;
		return this;
	}
	
	public boolean isFullTimeJourney() {
		return fullTimeJourney;
	}

	public EmployeeEventsData setFullTimeJourney(boolean fullTimeJourney) {
		this.fullTimeJourney = fullTimeJourney;
		return this;
	}
	
}
