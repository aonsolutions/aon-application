package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class EventEmployee implements Serializable{
	
	private Integer contractId;
	private String name;
	private String surName;
	private String fullName;
	
	private EmployeeEventsData employeeEventsData;
	
	public EventEmployee() {
		super();
	}
	
	public EventEmployee(Integer contractId, String name, String surName, String fullName,
			EmployeeEventsData employeeEventsData) {
		super();
		this.contractId = contractId;
		this.name = name;
		this.surName = surName;
		this.fullName = fullName;
		this.employeeEventsData = employeeEventsData;
	}

	public Integer getContractId() {
		return contractId;
	}

	public EventEmployee setContractId(Integer contractId) {
		this.contractId = contractId;
		return this;
	}

	public String getName() {
		return name;
	}

	public EventEmployee setName(String name) {
		this.name = name;
		return this;
	}

	public String getSurName() {
		return surName;
	}

	public EventEmployee setSurName(String surName) {
		this.surName = surName;
		return this;
	}

	public String getFullName() {
		return fullName;
	}

	public EventEmployee setFullName(String fullName) {
		this.fullName = fullName;
		return this;
	}

	public EmployeeEventsData getEmployeeEventsData() {
		return employeeEventsData;
	}

	public EventEmployee setEmployeeEventsData(EmployeeEventsData employeeEventsData) {
		this.employeeEventsData = employeeEventsData;
		return this;
	}
	
}
