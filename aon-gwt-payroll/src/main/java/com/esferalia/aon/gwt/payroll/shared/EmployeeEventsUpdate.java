package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

import com.esferalia.aon.gwt.payroll.client.Quartet;

public class EmployeeEventsUpdate implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private List<Quartet<Date, Date, String, String>> variablesEventsList;
	private Boolean fullTimeEmployee;
	
	public EmployeeEventsUpdate() {
		super();
	}

	public EmployeeEventsUpdate(List<Quartet<Date, Date, String, String>> variableEventsList, 
			Boolean fullTimeEmployee) {
		super();
		this.variablesEventsList = variableEventsList;
		this.fullTimeEmployee = fullTimeEmployee;
		
	}

	// ------------ GETTERS / SETTERS ------------
	
	public List<Quartet<Date, Date, String, String>> getVariableEventsList() {
		return variablesEventsList;
	}

	public EmployeeEventsUpdate setVariableEventsList(List<Quartet<Date, Date, String, String>> variableEventsList) {
		this.variablesEventsList = variableEventsList;
		return this;
	}

	public Boolean getFullTimeEmployee() {
		return this.fullTimeEmployee;
	}

	public EmployeeEventsUpdate setFullTimeEmployee(Boolean fullTimeEmployee) {
		this.fullTimeEmployee = fullTimeEmployee;
		return this;
	}
	
	
	

	
	
}
